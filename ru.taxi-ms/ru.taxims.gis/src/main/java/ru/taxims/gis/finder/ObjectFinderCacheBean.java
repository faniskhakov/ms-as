package ru.taxims.gis.finder;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.dictionary.GeoObjectDictionary;
import ru.taxims.domain.interfaces.gis.ObjectFinder;
import ru.taxims.domain.interfaces.gis.ObjectFinderCache;
import ru.taxims.domain.util.Modificator;
import ru.taxims.domain.util.Validator;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer_DB on 29.08.14.
 */
@ObjectFinderCache
@Stateless
@Local
public class ObjectFinderCacheBean implements ObjectFinder
{
	int cityId = 1;
	int typeId = 1;
	private Logger logger = Logger.getLogger(ObjectFinderCacheBean.class);

	@EJB(beanName = "GeoObjectDictionaryDaoBean")
	GeoObjectDictionary geoObjectDictionary;

	@Override
	public List<GeoObject> getObjects(String fragment) throws GISException, DaoException
	{
		fragment = fragment.trim();
		if (fragment.contains(",")) {
			return fragmentModificator(fragment, ",");
		}
		if (fragment.contains(" ")) {
			return fragmentModificator(fragment, " ");
		}
		return geoObjectDictionary.getItemsContainedType(fragment, typeId, cityId);
	}

	private List<GeoObject> fragmentModificator(String fragment, String separator) throws DaoException
	{
		String streetName = fragment.substring(0, fragment.indexOf(separator));
		String houseName = fragment.substring(fragment.indexOf(separator) + 1);
		streetName = Modificator.fragmentModificator(streetName, 1);
		houseName = Modificator.fragmentModificator(houseName, 2);
		logger.info("fragmentModificator " + fragment + " street " + streetName +
			" house " + houseName + " separator" + separator);
		if (Validator.isNumeric(houseName)){
			logger.info("fragmentModificator isNumeric " + houseName);
			return geoObjectDictionary.getItemsContainedParent(houseName, streetName, cityId);
		}
		logger.info("fragmentModificator is not Numeric fragment " + fragment);
		return geoObjectDictionary.getItemsContainedFragment(fragment, cityId);
	}



	@Override
	public List<GeoObject> getObjectsByParent(
		String fragment, int parentId) throws GISException, SQLException, DaoException
	{
		if (parentId != 0){
			return geoObjectDictionary.getItemsContainedParent(fragment, parentId);
		}
		return geoObjectDictionary.getItemsContainedFragment(fragment, cityId);
	}

	@Override
	public List<GeoObject> getObjectsByType(String fragment, int typeId) throws GISException, SQLException, DaoException
	{
		fragment = fragment.trim();
		if (typeId != 0){
			return geoObjectDictionary.getItemsContainedType(fragment, typeId, cityId);
		}
		return geoObjectDictionary.getItemsContainedFragment(fragment, cityId);
	}
}
