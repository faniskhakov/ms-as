package ru.taxims.distribution.core;

import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 10.09.14.
 */
public interface Buncher
{
	public Map<Integer, List<Order>> grouping(Map<Long, Order> orders) throws DaoException, CacheException;
	public int grouping(Order order) throws DaoException, CacheException;
}
