package ru.taxims.distribution.type;

import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 10.09.14.
 */
public interface Distributor
{
	Map<Long, Long> nearestDriverDistribute(List<Order> orders) throws CacheException, DaoException;
	long nearestDriverDistribute(Order order) throws CacheException, DaoException;
}
