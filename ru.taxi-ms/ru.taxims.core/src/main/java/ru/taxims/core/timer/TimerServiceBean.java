package ru.taxims.core.timer;

import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.OrderDistribution;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.distribution.OrderDistributor;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.Map;

/**
 * Created by Developer_DB on 21.11.14.
 */
@Stateless
public class TimerServiceBean
{
	@EJB(beanName = "SimpleOrderDistributionBean")
	OrderDistributor orderDistributor;

	@EJB
	OrderService orderService;



//	@Schedule(minute = "*/2", hour = "*", persistent=false)
	private void distributeOrder() throws CacheException, DaoException
	{
		orderDistributor.distributeOrder();
	}

	@Schedule(second = "*/30", minute = "*", hour = "*")
	private void refuseOrder() throws CacheException, DaoException
	{

		for(Map.Entry<Long, Order> orderEntry: orderService.findAll().entrySet()){
			if (orderEntry.getValue().getState().isOrderFree() || orderEntry.getValue().getDistributions().isEmpty()) {continue;}
			for (OrderDistribution orderDistribution: orderEntry.getValue().getDistributions()){
				if (orderDistribution.getDistributionState().isOrderOffered()){
					if ((new Date().getTime()) - orderDistribution.getCreatedDate().getTime() > 1000*60*3){ //todo
						orderService.orderRefuse(orderEntry.getValue().getOrderId(), orderDistribution.getDriverId());
					}
				}
			}
		}
	}
}
