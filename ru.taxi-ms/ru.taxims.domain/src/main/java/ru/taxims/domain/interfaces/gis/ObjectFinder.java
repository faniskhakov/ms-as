package ru.taxims.domain.interfaces.gis;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer_DB on 27.08.14.
 */

public interface ObjectFinder
{
	public List<GeoObject> getObjects(String fragment) throws GISException, SQLException, DaoException;
	public List<GeoObject> getObjectsByParent(String fragment, int parentId) throws GISException, SQLException, DaoException;
	public List<GeoObject> getObjectsByType(String fragment, int typeId) throws GISException, SQLException, DaoException;
}
