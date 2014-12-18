package ru.taxims.gis.router;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.dictionary.DistanceCalculator;
import ru.taxims.domain.interfaces.gis.Router;
import ru.taxims.domain.interfaces.gis.RouterDB;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by Developer_DB on 02.09.14.
 */
@RouterDB
@Stateless
@Local
public class RouterDBBean implements Router
{
	private Logger logger = Logger.getLogger(RouterDBBean.class);
	@EJB(beanName = "DistanceDictionaryDaoBean")
	DistanceCalculator distanceCalculator;

	@Override
	public float getDistancePoints(List<Point> points) throws GISException
	{
		return 0;
	}

	@Override
	public float getDistanceGeoObjects(List<GeoObject> points) throws GISException, DaoException
	{
		if (points.size() < 2) return 0;
		float distance = 0;
		for (int i = 0; i < points.size() - 1; i++){
			distance += distanceCalculator.getDistance(
				points.get(i).getPosition().getSectionId(), points.get(i+1).getPosition().getSectionId());
			logger.info("======= Get Distance " + points.get(i).getAddress() + ", SectionId " + points.get(i).getPosition().getSectionId());
		}
		return distance;
	}
}
