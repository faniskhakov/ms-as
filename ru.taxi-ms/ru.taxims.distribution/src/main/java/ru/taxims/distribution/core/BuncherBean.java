package ru.taxims.distribution.core;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.OrderDistribution;
import ru.taxims.domain.datamodels.OrderTariff;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.*;

/**
 * Created by Developer_DB on 10.09.14.
 */
@Stateless
@Local
public class BuncherBean implements Buncher
{
	Logger logger = Logger.getLogger(BuncherBean.class);

	@EJB(beanName = "OrderTariffDictionaryDaoBean")
	Dictionary<OrderTariff> orderTariffDictionary;

	@EJB
	OrderService orderService;

	public Map<Integer, List<Order>> grouping(Map<Long, Order> orders) throws DaoException, CacheException
	{
		Map<Integer, List<Order>> groupedOrders = new HashMap<>();
		for(Map.Entry<Long, Order> orderEntry: orderService.findAll().entrySet()){
			if (orderEntry.getValue().getState().isOrderFree() || isFree(orderEntry.getValue())) {continue;}
			if (isMainGroup(orderEntry.getValue())){
				if(!groupedOrders.containsKey(1)){
					groupedOrders.put(1, new ArrayList<Order>());
				}
				groupedOrders.get(1).add(orderEntry.getValue());
				continue;
			}
			if (isGPPGroup(orderEntry.getValue())){
				if(!groupedOrders.containsKey(2)){
					groupedOrders.put(2, new ArrayList<Order>());
				}
				groupedOrders.get(2).add(orderEntry.getValue());
			}
		}
		return groupedOrders;
	}

	public int grouping(Order order) throws DaoException, CacheException
	{
		if(!isFree(order)) return 0;
		if (isMainGroup(order)){
			return 1;
		}
		if (isGPPGroup(order)){
			return 2;
		}
		return 0;
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


	private boolean isMainGroup(Order order) throws DaoException
	{
		if (orderTariffCheck(order, orderTariffDictionary.getItem(1))) return true;
		if (orderTariffCheck(order, orderTariffDictionary.getItem(2))) return true;
		if (orderTariffCheck(order, orderTariffDictionary.getItem(3))) return true;
		if (orderTariffCheck(order, orderTariffDictionary.getItem(6))) return true;
		if (orderTariffCheck(order, orderTariffDictionary.getItem(7))) return true;
		return  (orderTariffCheck(order, orderTariffDictionary.getItem(8)));
	}

	private boolean isGPPGroup(Order order) throws DaoException
	{
		return  (orderTariffCheck(order, orderTariffDictionary.getItem(5))) ||
			(orderTariffCheck(order, orderTariffDictionary.getItem(10))) ||
			(orderTariffCheck(order, orderTariffDictionary.getItem(15)));
	}

	private boolean orderTariffCheck(Order order, OrderTariff orderTariff) throws DaoException
	{
		boolean result;
		for (Feature feature: orderTariff.getFeatures().values()){
			result = false;
			for(Integer featureId :order.getFeatures().keySet()){
				if (featureId.equals(feature.getId())) {
					result = true;
					break;
				}
			}
			if (!result) {return false;}
		}
		return true;
	}
}
