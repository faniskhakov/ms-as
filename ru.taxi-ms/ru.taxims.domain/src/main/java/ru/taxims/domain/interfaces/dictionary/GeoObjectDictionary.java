package ru.taxims.domain.interfaces.dictionary;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.exception.DaoException;

import java.util.List;

/**
 * Created by Developer_DB on 01.09.14.
 */
public interface GeoObjectDictionary
{
	List<GeoObject> getItems(String fragment, int cityId) throws DaoException;
	List<GeoObject> getItemsContainedFragment(String fragment, int cityId) throws DaoException;
	List<GeoObject> getItemsContainedParent(String fragment, String parentFragment, int cityId) throws DaoException;
	List<GeoObject> getItemsContainedType(String fragment, int type, int cityId) throws DaoException;
	List<GeoObject> getItemsContainedParent(String fragment, int parentId) throws DaoException;
}
