package ru.taxims.domain.interfaces.distribution;

import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Developer_DB on 09.09.14.
 */
public interface OrderDistributor
{
	Map<Long, Long> distributeOrder() throws CacheException, DaoException;
	Future<Long> distributeOrder(Order order) throws CacheException, DaoException;
}
