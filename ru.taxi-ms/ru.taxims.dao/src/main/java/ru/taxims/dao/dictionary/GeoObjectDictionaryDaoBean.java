package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.GeoObjectDescription;
import ru.taxims.domain.datamodels.GeoObjectPosition;
import ru.taxims.domain.datamodels.GeoObjectType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;
import ru.taxims.domain.interfaces.dictionary.GeoObjectDictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Developer_DB on 29.08.14.
 */
@Singleton
@Startup
@Local({Dictionary.class, GeoObjectDictionary.class})
@DependsOn({"GeoObjectTypeDictionaryDaoBean", "GeoObjectDescriptionDictionaryDaoBean"})
public class GeoObjectDictionaryDaoBean extends DictionaryDaoBean<GeoObject>
	implements GeoObjectDictionary
{
	@EJB(beanName = "GeoObjectTypeDictionaryDaoBean")
	protected Dictionary<GeoObjectType> geoObjectType;
	@EJB(beanName = "GeoObjectDescriptionDictionaryDaoBean")
	protected  Dictionary<GeoObjectDescription> geoObjectDescription;

	@PostConstruct
	public void init() throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			GeoObject tempGeoObject = null;
			String address = "";
			int tempKey = 2; //todo
			String SELECT_WAYPOINTS_SQL = " SELECT DISTINCT \n" +
				"  l.object_id AS sequence,\n" +
				"  o.object_id, \n" +
				"  o.name, \n" +
				"  o.object_type_id, \n" +
				"  o.description_id, \n" +
				"  p.latitude, \n" +
				"  p.longitude, \n" +
				"  p.section_id, \n" +
				"  t.level \n" +
				" FROM object_link l \n" +
				" JOIN \"object\" o ON (o.object_id = l.parent_id OR o.object_id = l.object_id) \n" +
				" JOIN object_position p ON (p.position_id = o.position_id) \n" +
				" JOIN object_type t ON (t.object_type_id = o.object_type_id) \n" +
				" ORDER BY l.object_id ASC, t.level ASC ";
			preparedStatement = connection.prepareStatement(SELECT_WAYPOINTS_SQL);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				GeoObject geoObject = new GeoObject();
				geoObject.setId(result.getInt("object_id"));
				geoObject.setName(result.getString("name"));
				geoObject.setType(geoObjectType.getItem(result.getInt("object_type_id")));
				geoObject.setDescription(geoObjectDescription.getItem(result.getInt("description_id")));
				GeoObjectPosition position = new GeoObjectPosition(result.getFloat("latitude"), result.getFloat("longitude"));
				position.setSectionId(result.getInt("section_id"));
				geoObject.setPosition(position);
				int key = result.getInt("sequence");
				if (key != tempKey && tempGeoObject != null) {
					tempGeoObject.setAddress(address.substring(0,  address.length() - 2));
					items.put(tempKey, tempGeoObject);
					//logger.info("address = " + address.substring(0,  address.length() - 2));
					address = "";
					tempGeoObject = null;
				}
				address +=  result.getString("name") + ", ";
				geoObject.setParent(tempGeoObject);
				tempGeoObject = geoObject;
				tempKey = key;
			}
			tempGeoObject.setAddress(address.substring(0,  address.length() - 2));
			logger.info("address = " + tempGeoObject.getAddress());
			items.put(tempKey, tempGeoObject);
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

	@Lock(LockType.READ)
	@Override
	public List<GeoObject> getItems(String fragment, int cityId) throws DaoException
	{
		List<GeoObject> returnItems = new LinkedList<GeoObject>();
		for (GeoObject item: items.values())
		{
			if (item.getName().indexOf(fragment) == 0){
				if(item.getType().getId() == 41)
					returnItems.add(item);
				else if (isRequiredParent(item, cityId))
					returnItems.add(item);
			}
		}
		return returnItems;
	}

	@Lock(LockType.READ)
	@Override
	public List<GeoObject> getItemsContainedFragment(String fragment, int cityId) throws DaoException
	{
		List<GeoObject> returnItems = new LinkedList<GeoObject>();
		for (GeoObject item: items.values())
		{
			if (item.getName().contains(fragment)) {
				if(item.getType().getId() == 41)
					returnItems.add(item);
				else if (isRequiredParent(item, cityId))
					returnItems.add(item);
			}
		}
		return returnItems;
	}

	@Override
	public List<GeoObject> getItemsContainedParent(
		String fragment, String parentFragment, int cityId) throws DaoException
	{
		List<GeoObject> returnItems = new ArrayList<GeoObject>();
		for (GeoObject item: items.values())
		{
			if (item.getParent() != null){
				if (item.getName().indexOf(fragment) == 0 && item.getParent().getName().contains(parentFragment)) {
					if (isRequiredParent(item, cityId)){
						returnItems.add(item);
						logger.info("getItemsContainedParent(String " + fragment + ", String " + parentFragment + ") = " + item.getName() +
							" ParentName = " + item.getParent().getName() + " SectionId " +
							item.getPosition().getSectionId() + " item.getId " + item.getId() + " address " + item.getAddress());
					}
				}
			}
		}
		return returnItems;
	}

	@Override
	public List<GeoObject> getItemsContainedType(String fragment, int typeId, int cityId) throws DaoException
	{
		List<GeoObject> geoObjects = new ArrayList<GeoObject>();
		logger.info(" getItemsContainedType fragment " + fragment + " typeId " + typeId + " cityId " + cityId);
		for (GeoObject item: items.values()){
			if (item.getName().contains(fragment)
				&& item.getType().getId() == typeId
				&& isRequiredParent(item, cityId))
			{
				logger.info(" !!!Find Object name " + item.getName() + " typeId " + item.getType() + " parent  " + item.getParent().getName());
				geoObjects.add(item);
			}
		}
		return geoObjects;
	}

	@Override
	public List<GeoObject> getItemsContainedParent(String fragment, int parentId) throws DaoException
	{
		List<GeoObject> geoObjects = new ArrayList<GeoObject>();
		for (GeoObject item: items.values()){
			if (item.getParent() != null){
				if (item.getName().contains(fragment) && item.getParent().getId() == parentId){
					geoObjects.add(item);
				}
			}
		}
		return geoObjects;
	}

	private boolean isRequiredParent(GeoObject geoObject, int parentId) throws DaoException
	{
		GeoObject parentGeoObject = geoObject.getParent();
		while (parentGeoObject != null){
			if (parentGeoObject.getId() == parentId) {
				return true;
			}
			parentGeoObject = parentGeoObject.getParent();
		}
		return false;
	}

}
