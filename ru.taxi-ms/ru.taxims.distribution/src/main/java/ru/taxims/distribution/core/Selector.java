package ru.taxims.distribution.core;

import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 10.09.14.
 */
public interface Selector
{
	Map<Long, Long> distributorSelect(int orderTypeId, List<Order> orders) throws CacheException, DaoException;
	long distributorSelect(int orderTypeId, Order order) throws CacheException, DaoException;
}
