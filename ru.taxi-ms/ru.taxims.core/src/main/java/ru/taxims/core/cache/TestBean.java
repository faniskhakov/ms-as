package ru.taxims.core.cache;

import org.apache.log4j.Logger;
import ru.taxims.domain.criteria.OrderSearchCriteria;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.exception.PriceException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.dao.OrderDao;
import ru.taxims.domain.interfaces.gis.ObjectFinder;
import ru.taxims.domain.interfaces.gis.Router;
import ru.taxims.domain.interfaces.price.PriceCalculator;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 18.09.14.
 */
@Singleton
@Startup
@DependsOn({"OrderCacheBean", "DriverCacheBean", "UserCacheBean"})
public class TestBean
{
	Logger logger = Logger.getLogger(TestBean.class);

	@EJB
	OrderDao orderDao;

	@EJB(beanName = "ObjectFinderCacheBean")
	ObjectFinder objectFinder;

	@EJB(beanName = "RouterDBBean")
	Router router;

	@EJB
	PriceCalculator priceCalculator;

//	@EJB
//	OrderDistributor distributor;

	@EJB
	DriverService driverService;


	@PostConstruct
	protected void init() throws DaoException, CacheException, GISException, SQLException, PriceException
	{
		List<Order> orders = orderDao.findOrders(OrderSearchCriteria.stateCriteria(1));
		for(Order order :orders)
		{
			logger.info("Order ID = " + order.getOrderId() + "Order phone = " + order.getPhone());
			for(Map.Entry<Integer, GeoObject> entry: order.getWaypoints().entrySet()){
				logger.info(" Order waypoint "  + entry.getKey() + " Name = " + entry.getValue().getName());
				logger.info(" Order waypoint "  + entry.getKey() + " Address = " + entry.getValue().getAddress());
				if (entry.getValue().getParent() != null){
					logger.info(" Order waypoint "  + entry.getKey() +
						" Parent = " + entry.getValue().getParent().getName());
				}

			}
		}
		logger.info(" OrderCache was constructed ");
		logger.info(" ------------------------------------------------------ ");
		List<GeoObject> geoObjects = new ArrayList<GeoObject>();
		for(GeoObject geoObject: objectFinder.getObjects("Рустав, 19")){

			logger.info("GeoObject Name " + geoObject.getName());
			logger.info("GeoObject Address " + geoObject.getAddress());
			logger.info("GeoObject  Type " + geoObject.getType().getName());
			logger.info("GeoObject Desc " + geoObject.getDescription().getName());
			logger.info("GeoObject SectionId " + geoObject.getPosition().getSectionId());
			if (geoObject.getParent() != null){
				logger.info("GeoObject ParentName " + geoObject.getParent().getName());
			}

		}
		logger.info(" ------------------------------------------------------ ");
		for(GeoObject geoObject: objectFinder.getObjects("Губайдул  17")){

			logger.info("GeoObject Name " + geoObject.getName());
			logger.info("GeoObject Address " + geoObject.getAddress());
			logger.info("GeoObject  Type " + geoObject.getType().getName());
			logger.info("GeoObject Desc " + geoObject.getDescription().getName());
			if (geoObject.getParent() != null){
				logger.info("GeoObject ParentName " + geoObject.getParent().getName());
			}
			logger.info("GeoObject SectionId " + geoObject.getPosition().getSectionId());
		}
		logger.info(" -------------------------------------------------------------------- ");

		logger.info(" -------------router.getDistanceGeoObjects(geoObjects))-------------- ");
		geoObjects.add(objectFinder.getObjects("Аксакова, 44").get(0));
		geoObjects.add(objectFinder.getObjects("Жукова  17").get(0));
		logger.info("Distance between " + geoObjects.get(0).getAddress() + " and " + geoObjects.get(1).getAddress()
			+ " equals " +  router.getDistanceGeoObjects(geoObjects));
		logger.info(" -------------------------------------------------------------------- ");

		logger.info(" -----------priceCalculator.calculatePrice(features))---------------- ");
		Map<Integer, String> features = new HashMap<Integer, String>();
		features.put(1, "11500");
		features.put(8, "11");
		features.put(11, "1");
		features.put(9, "2");
		features.put(16, "1");
		features.put(7, "7");
		features.put(51, "1");
		features.put(6, "2014-09-09 16:35:54");

		logger.info(" Price " + priceCalculator.calculatePrice(features));
		logger.info(" -------------------------------------------------------------------- ");

		logger.info(" -----------priceCalculator.calculatePrice2(features))---------------- ");
		logger.info(" Price " + priceCalculator.calculatePriceAlternative(features));
		logger.info(" -------------------------------------------------------------------- ");

		logger.info(" -----------driverService---------------------------- ");

		for (Map.Entry<Long, Driver> entry: driverService.findByState(1).entrySet()){
			Point point = new Point((float)54.775000, (float)55.992108);
			driverService.changePosition(entry.getValue().getDriverId(), point);
			if (entry.getValue().getGeoObject().getPosition() == null) continue;
			logger.info(" Point (" + point.getLatitude() + " ," + point.getLongitude() + ") DriverId =" + entry.getKey()
				+ " Name=" + entry.getValue().getName() + " Position (" + entry.getValue().getGeoObject().getPosition().getLatitude() + " ,"
				+ entry.getValue().getGeoObject().getPosition().getLongitude() + ")");
		}
		logger.info(" ------------------------------------------------------ ");

//		logger.info(" -----------distributor.distributeOrder()---------------------------- ");
//		mapEvent.fire(orderDistributor.distributeOrder());
//		for (Map.Entry<Long, Long> entry: distributor.distributeOrder().entrySet()){
//			logger.info(" OrderId =" + entry.getKey() + " DriverId = " + entry.getValue());
//		}
//		logger.info(" ------------------------------------------------------ ");

	}
}
