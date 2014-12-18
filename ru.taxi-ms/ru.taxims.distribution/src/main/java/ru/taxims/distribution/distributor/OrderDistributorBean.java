package ru.taxims.distribution.distributor;

import org.apache.log4j.Logger;
import ru.taxims.distribution.core.Buncher;
import ru.taxims.distribution.core.Selector;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.distribution.MainOrderDistribution;
import ru.taxims.domain.interfaces.distribution.OrderDistributor;

import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Developer_DB on 09.09.14.
 */
@MainOrderDistribution
@Stateless
@Local
public class OrderDistributorBean implements OrderDistributor
{
	Logger logger = Logger.getLogger(OrderDistributorBean.class);

	@EJB
	OrderService orderService;

	@EJB
	Buncher buncher;

	@EJB
	Selector selector;

	@Override
	public Map<Long, Long> distributeOrder() throws CacheException, DaoException
	{
		Map<Long, Long> mapOrderIdDriverId = new HashMap<>();
		for (Map.Entry<Integer, List<Order>> buncherEntry: buncher.grouping(orderService.findAll()).entrySet()){
			for (Map.Entry<Long, Long> selectorEntry:  selector.distributorSelect(buncherEntry.getKey(), buncherEntry.getValue()).entrySet()){
				mapOrderIdDriverId.put(selectorEntry.getKey(), selectorEntry.getValue());
				logger.info(" ");
				logger.info(" MapOrderIdDriverId (Order ID " + selectorEntry.getKey() + " Driver ID = " + selectorEntry.getValue() + ")");
				logger.info(" ");
			}
		}
		return mapOrderIdDriverId;
	}

	@Override
	public Future<Long> distributeOrder(Order order) throws CacheException, DaoException
	{
		return new AsyncResult<>(selector.distributorSelect(buncher.grouping(order), order));
	}
}
