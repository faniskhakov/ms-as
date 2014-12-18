package ru.taxims.distribution.type;

import org.apache.log4j.Logger;
import ru.taxims.distribution.selector.DriverSelector;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.event.OfferOrderEvent;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.util.Pair;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 10.09.14.
 */
@Stateless
@Local
public class DistributorBean implements Distributor
{
	Logger logger = Logger.getLogger(DistributorBean.class);

	@EJB
	DriverService driverService;

	@EJB
	DriverSelector driverSelector;

	@Inject	@OfferOrderEvent
	private Event<Pair<Long, Long>> mapEvent;

	@Override
	public Map<Long, Long> nearestDriverDistribute(List<Order> orders) throws CacheException, DaoException
	{
		logger.info("   ");
		logger.info(" -----------Distributor.nearestDriverDistribute(List<Order> orders)---------------- ");
		Map<Long, Long> mapOrderDriver = new HashMap<Long, Long>();
		for(Order order: orders){
			long driverId = 0;
			double minDistance = Float.MAX_VALUE;
			for (Map.Entry<Long, Driver> driverEntry: driverService.findAll().entrySet()){
				if (mapOrderDriver.containsValue(driverEntry.getValue().getDriverId()) ||driverEntry.getValue().getState().isDriverFree()
					|| driverSelector.isDriverRepeat(order, driverEntry.getValue())	|| !driverSelector.isDriverSatisfy(order, driverEntry.getValue())) continue;//todo
				if (order.getWaypoints().get(1) == null || order.getWaypoints().get(1).getPosition() == null ||
					driverEntry.getValue().getGeoObject() == null) continue;
				float lat = Math.abs(order.getWaypoints().get(1).getPosition().getLatitude() - driverEntry.getValue().getGeoObject().getPosition().getLatitude());
				float lon = Math.abs(order.getWaypoints().get(1).getPosition().getLongitude() - driverEntry.getValue().getGeoObject().getPosition().getLongitude());
				double distance = Math.sqrt(Math.pow(lat, 2) + Math.pow(lon, 2));
				if (distance < minDistance){
					minDistance = distance;
					driverId = driverEntry.getValue().getDriverId();
				}
			}
			if (driverId != 0){
				mapOrderDriver.put(order.getOrderId(), driverId);
			}
		}
		return mapOrderDriver;
	}

	@Override
	public long nearestDriverDistribute(Order order) throws CacheException, DaoException
	{
		long driverId = 0;
		double minDistance = Float.MAX_VALUE;
		logger.info("NearestDriverDistribute 1 orderId " + order.getOrderId());
		for (Map.Entry<Long, Driver> driverEntry: driverService.findAll().entrySet()){
			logger.info("NearestDriverDistribute 2 orderId " + order.getOrderId() + " driverId " + driverEntry.getValue().getDriverId());
			logger.info("NearestDriverDistribute 2 isDriverFree " + driverEntry.getValue().getState().isDriverFree());
			logger.info("NearestDriverDistribute 2 isDriverRepeat " + driverSelector.isDriverRepeat(order, driverEntry.getValue()));
			logger.info("NearestDriverDistribute 2 !isDriverSatisfy " + !driverSelector.isDriverSatisfy(order, driverEntry.getValue()));
			if (!driverEntry.getValue().getState().isDriverFree() || driverSelector.isDriverRepeat(order, driverEntry.getValue())
				|| !driverSelector.isDriverSatisfy(order, driverEntry.getValue())) continue;//todo
			logger.info("NearestDriverDistribute 3 orderId " + order.getOrderId() + " driverId " + driverEntry.getValue().getDriverId());
			if (order.getWaypoints().get(1) == null || order.getWaypoints().get(1).getPosition() == null ||
				driverEntry.getValue().getGeoObject() == null) continue;
			logger.info("NearestDriverDistribute 4 orderId " + order.getOrderId() + " driverId " + driverEntry.getValue().getDriverId());
			float lat = Math.abs(order.getWaypoints().get(1).getPosition().getLatitude() - driverEntry.getValue().getGeoObject().getPosition().getLatitude());
			float lon = Math.abs(order.getWaypoints().get(1).getPosition().getLongitude() - driverEntry.getValue().getGeoObject().getPosition().getLongitude());
			double distance = Math.sqrt(Math.pow(lat, 2) + Math.pow(lon, 2));
			if (distance < minDistance){
				minDistance = distance;
				driverId = driverEntry.getValue().getDriverId();
			}
		}
		if (driverId != 0){
			mapEvent.fire(new Pair<>(order.getOrderId(), driverId));
		}
		return driverId;
	}
}
