package ru.taxims.gis.finder;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.GeoObjectDescription;
import ru.taxims.domain.datamodels.GeoObjectPosition;
import ru.taxims.domain.datamodels.GeoObjectType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;
import ru.taxims.domain.interfaces.gis.ObjectFinder;
import ru.taxims.domain.interfaces.gis.ObjectFinderDB;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer_DB on 27.08.14.
 */
@ObjectFinderDB
@Stateless
@Local
public class ObjectFinderDBBean implements ObjectFinder
{
	private Logger logger = Logger.getLogger(ObjectFinderDBBean.class);

	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;

	@EJB(beanName = "GeoObjectTypeDictionaryDaoBean")
	Dictionary<GeoObjectType> geoObjectTypeDictionary;

	@EJB(beanName = "GeoObjectDescriptionDictionaryDaoBean")
	Dictionary<GeoObjectDescription> geoObjectDescriptionDictionary;


	@Override
	public List<GeoObject> getObjects(String fragment) throws GISException, SQLException, DaoException
	{
		return getObjects(fragment, 0, 0);
	}

	@Override
	public List<GeoObject> getObjectsByParent(
		String fragment, int parentId) throws GISException, SQLException, DaoException
	{
		return getObjects(fragment, 0, parentId);
	}

	@Override
	public List<GeoObject> getObjectsByType(String fragment, int typeId) throws GISException, SQLException, DaoException
	{
		return getObjects(fragment, typeId, 0);
	}

	public List<GeoObject> getObjects(String fragment, int type, int parentId) throws GISException, SQLException, DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		List<GeoObject> geoObjects = new ArrayList<GeoObject>();
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT * FROM get_object(?, ?, ?, ?);");
			preparedStatement.setString(1, fragment);
			preparedStatement.setInt(2, type);
			preparedStatement.setInt(3, parentId);
			preparedStatement.setInt(4, 20);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				GeoObject geoObject = new GeoObject();
				geoObject.setId(result.getInt("object_id"));
				geoObject.setName(result.getString("object_name"));
				geoObject.setAddress(result.getString("address"));
				geoObject.setType(geoObjectTypeDictionary.getItem(result.getInt("object_type_id")));
				geoObject.setPosition(new GeoObjectPosition(result.getFloat("latitude"), result.getFloat("longitude")));
				geoObject.setDescription(new GeoObjectDescription(result.getString("description")));
				geoObjects.add(geoObject);
			}
			return geoObjects;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}
