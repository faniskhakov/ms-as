package ru.taxims.gis.router;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.gis.Router;
import ru.taxims.domain.interfaces.gis.RouterMain;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by Developer_DB on 02.09.14.
 */
@RouterMain
@Stateless
@Local
public class RouterBean implements Router
{
	@Override
	public float getDistancePoints(List<Point> points) throws GISException
	{
		return 0;
	}

	@Override
	public float getDistanceGeoObjects(List<GeoObject> points) throws GISException
	{
		return 0;
	}
}
