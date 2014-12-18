package ru.taxims.distribution.selector;

import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.DaoException;

/**
 * Created by Developer_DB on 24.09.14.
 */
public interface DriverSelector
{
	boolean isDriverSatisfy(Order order, Driver driver) throws DaoException;
	boolean isDriverRepeat(Order order, Driver driver) throws DaoException;
}
