package ru.taxims.domain.interfaces.gis;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;

import java.util.List;

/**
 * Created by Developer_DB on 27.08.14.
 */
public interface Router
{
	public float getDistancePoints(List<Point> points) throws GISException;
	public float getDistanceGeoObjects(List<GeoObject> points) throws GISException, DaoException;
}
