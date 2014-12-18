package ru.taxims.domain.interfaces.core;

import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 06.08.14.
 */
@Local
public interface OrderService
{
	public long persist(Order order) throws DaoException, CacheException;
	public Order find(long orderId) throws DaoException, CacheException;
	public Map<Long, Order> findAll() throws DaoException, CacheException;
	public List<Order> findByState(int stateId) throws DaoException, CacheException;
	public Order changeState(long orderId, int newStateId, String comment) throws DaoException, CacheException;
	public Order insertAmount(long orderId, float amount) throws DaoException, CacheException;
	public Order insertFeatures(long orderId, Map<Integer, String> features) throws DaoException, CacheException;
	public Order insertWaypoints(long orderId, Map<Integer, GeoObject> waypoints) throws DaoException, CacheException;
	public Order orderOffer(long orderId, long driverId) throws DaoException, CacheException;
	public Order orderRefuse(long orderId, long driverId) throws DaoException, CacheException;
	public Order assignDriver(long orderId, long driverId) throws DaoException, CacheException;
}
