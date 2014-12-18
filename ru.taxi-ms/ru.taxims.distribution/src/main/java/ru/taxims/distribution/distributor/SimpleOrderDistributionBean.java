package ru.taxims.distribution.distributor;

import org.apache.log4j.Logger;
import ru.taxims.distribution.type.Distributor;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.OrderDistribution;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.distribution.OrderDistributor;
import ru.taxims.domain.interfaces.distribution.SimpleOrderDistribution;

import javax.ejb.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Developer_DB on 27.11.14.
 */
@SimpleOrderDistribution
@Stateless
@Local
public class SimpleOrderDistributionBean implements OrderDistributor
{
	Logger logger = Logger.getLogger(SimpleOrderDistributionBean.class);

	@EJB
	OrderService orderService;
	@EJB
	Distributor distributor;

	@Override
//	@Asynchronous
	public Map<Long, Long> distributeOrder() throws CacheException, DaoException
	{
		Map<Long, Long> mapOrderIdDriverId = new HashMap<>();
		for(Map.Entry<Long, Order> orderEntry: orderService.findAll().entrySet()){
			if (orderEntry.getValue().getState().getId() != 1 || isFree(orderEntry.getValue())) {continue;}
			long driverId = distributor.nearestDriverDistribute(orderEntry.getValue());
			if (driverId != 0){
				mapOrderIdDriverId.put(orderEntry.getValue().getOrderId(), driverId);
			}
		}
		return mapOrderIdDriverId;
	}

	@Override
	@Asynchronous
//	@Lock(LockType.READ)
	public Future<Long> distributeOrder(Order order) throws CacheException, DaoException
	{
		logger.info("distributeOrder 1 orderId " + order.getOrderId());
		return new AsyncResult<>(distributor.nearestDriverDistribute(order));
	}

	private boolean isFree(Order order) throws DaoException, CacheException
	{
		if (order.getDistributions().isEmpty()) {return true;}
		for (OrderDistribution orderDistribution: order.getDistributions()){
			if (orderDistribution.getDistributionState().isOrderOffered()){
				if ((new Date().getTime()) - orderDistribution.getCreatedDate().getTime() > 1000*60*3){ //todo
					orderService.orderRefuse(order.getOrderId(), orderDistribution.getDriverId());
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
}
