package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.OrderDistribution;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 15.05.14.
 */
@Local
public interface OrderDao extends Dao<Order>
{
	List<Order> findOrders(String sql) throws DaoException;
	void insertAmount(long orderId, float amount) throws DaoException;
	void changeState(long orderId, int oldState, int newState, String comment)throws DaoException;
	void assignDriver(long orderId, long driverId)throws DaoException;
	void insertOrderFeatures(long orderId, Map<Integer, String> features) throws DaoException;
	void insertOrderWaypoints(long orderId, Map<Integer, GeoObject> waypoints) throws DaoException;
	void insertOrderDistribution(long orderId, OrderDistribution orderDistribution) throws DaoException;
}

