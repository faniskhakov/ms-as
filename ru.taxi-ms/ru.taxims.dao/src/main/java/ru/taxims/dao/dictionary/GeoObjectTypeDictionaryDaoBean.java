package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.GeoObjectType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class GeoObjectTypeDictionaryDaoBean extends DictionaryDaoBean<GeoObjectType>
{
	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT object_type_id, \"name\", level FROM object_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int geoObjectTypeId = result.getInt("object_type_id");
				GeoObjectType geoObjectType = new GeoObjectType(geoObjectTypeId);
				geoObjectType.setName(result.getString("name"));
				geoObjectType.setLevel(result.getInt("level"));
				items.put(geoObjectTypeId, geoObjectType);
			}
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

	public int insertGeoObjectType(GeoObjectType geoObjectType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TYPE_SQL = "INSERT INTO object_type " +
				" (\"name\", level) " +
				" VALUES (?, ?) RETURNING object_type_id ";
			preparedStatement = connection.prepareStatement(INSERT_TYPE_SQL);
			preparedStatement.setString(1, geoObjectType.getName());
			preparedStatement.setInt(2, geoObjectType.getLevel());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				geoObjectType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting geoObjectType failed, no generated key obtained.");
			}
			items.put(geoObjectType.getId(), geoObjectType);
			return  geoObjectType.getId();
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (generatedKeys != null) {
					generatedKeys.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
