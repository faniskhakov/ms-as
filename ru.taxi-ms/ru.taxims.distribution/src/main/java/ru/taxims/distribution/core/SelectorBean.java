package ru.taxims.distribution.core;

import ru.taxims.distribution.type.Distributor;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 10.09.14.
 */
@Stateless
@Local
public class SelectorBean implements Selector
{
	@EJB
	Distributor distributor;

	@Override
	public Map<Long, Long> distributorSelect(int orderTypeId, List<Order> orders) throws CacheException, DaoException
	{
		switch (orderTypeId) {
			case 1: return distributor.nearestDriverDistribute(orders);
			case 2: return distributor.nearestDriverDistribute(orders);
			default: return null;
		}
	}

	@Override
	public long distributorSelect(int orderTypeId, Order order) throws CacheException, DaoException
	{
		switch (orderTypeId) {
			case 1: return distributor.nearestDriverDistribute(order);
			case 2: return distributor.nearestDriverDistribute(order);
			default: return 0l;
		}
	}
}
