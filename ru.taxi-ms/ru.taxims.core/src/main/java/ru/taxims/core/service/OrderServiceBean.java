package ru.taxims.core.service;

import org.apache.log4j.Logger;
import ru.taxims.core.cache.EntityCache;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.dao.OrderDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;
import ru.taxims.domain.interfaces.distribution.OrderDistributor;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 06.08.14.
 */

@Stateless
public class OrderServiceBean implements OrderService
{
	Logger logger = Logger.getLogger(OrderServiceBean.class);

	@EJB
	OrderDao orderDao;
	@EJB
	DriverService driverService;
//	@EJB
//	UserService userService;
	@EJB(beanName = "OrderCacheBean")
	EntityCache<Order> orderEntityCache;
	@EJB(beanName = "OrderDistributionStateDictionaryDaoBean")
	Dictionary<OrderDistributionState> orderDistributionStateDictionary;
	@EJB(beanName = "OrderStateDictionaryDaoBean")
	Dictionary<OrderState> orderStateDictionary;
	@EJB(beanName = "SimpleOrderDistributionBean")
	OrderDistributor orderDistributor;

	@Override
	public long persist(Order order) throws DaoException, CacheException
	{
		if (! orderDao.verify(order)) {
			throw new DaoException("Verify exception. Order is not full");
		}
//		if (order.getCreator().getUserId() == 0){
//			userService.persist(order.getCreator());
//		}
		orderDao.persist(order);
		if (orderEntityCache.getItem(order.getOrderId()) != null){
			throw new CacheException("Order cache persist exception. Order is exists in cache");
		}
		orderEntityCache.putItem(order);
		orderDistributor.distributeOrder(order);
		return order.getOrderId();
	}

	@Lock(LockType.READ)
	@Override
	public Order find(long orderId) throws DaoException, CacheException
	{
		Order order;
		order = orderEntityCache.getItem(orderId);
		if (order == null) {
			order = orderDao.find(orderId);
			if (order == null){
				throw new DaoException("Find exception. Order is not exists in database");
			}
		}
		return order;
	}

	@Override
	public Map<Long, Order> findAll() throws DaoException, CacheException
	{
		return orderEntityCache.getItems();
	}

	@Lock(LockType.READ)
	@Override
	public List<Order> findByState(int stateId) throws DaoException, CacheException
	{
		List<Order> orders = new ArrayList<>();
		for (Order order: orderEntityCache.getItems().values()){
			if (order.getState().getId() == stateId) {
				orders.add(order);
			}
		}
		return orders;
	}

	@Override
	public Order changeState(long orderId, int newStateId, String comment) throws DaoException, CacheException
	{
		Order order = find(orderId);
		if (order == null || ! order.getState().availableState(newStateId)) {
			throw new DaoException("State change exception. State is not available");
		}
		OrderState newState = orderStateDictionary.getItem(newStateId);
		orderDao.changeState(orderId, order.getState().getId(), newStateId, comment);
		order.setState(newState);
		if (order.getState().isFinalState()){
			removeFromCache(order.getOrderId());
		}
		return order;
	}

	@Override
	public Order insertAmount(long orderId, float amount) throws DaoException, CacheException
	{
		Order order = find(orderId);
		orderDao.insertAmount(orderId, amount);
		order.setAmount(amount);
		return order;
	}

	@Override
	public Order insertFeatures(long orderId, Map<Integer, String> features) throws DaoException, CacheException
	{
		Order order = find(orderId);
		orderDao.insertOrderFeatures(order.getOrderId(), features);
		for (Map.Entry<Integer, String> entry: features.entrySet()){
			order.getFeatures().put(entry.getKey(), entry.getValue());
		}
		return order;
	}

	@Override
	public Order insertWaypoints(
		long orderId, Map<Integer, GeoObject> waypoints) throws DaoException, CacheException
	{
		Order order = find(orderId);
		orderDao.insertOrderWaypoints(order.getOrderId(), waypoints);
		for (Map.Entry<Integer, GeoObject> entry: waypoints.entrySet()){
			order.getWaypoints().put(entry.getKey(), entry.getValue());
		}
		return order;
	}

	@Override
	public Order orderRefuse(long orderId, long driverId) throws DaoException, CacheException
	{
		Order order = find(orderId);
		Driver driver = driverService.find(driverId);
		OrderDistribution orderDistribution = new OrderDistribution(driverId);
		orderDistribution.setDistributionState(orderDistributionStateDictionary.getItem(3));//todo
		orderDao.insertOrderDistribution(order.getOrderId(), orderDistribution);
		for(OrderDistribution distribution: order.getDistributions()){
			if (distribution.getDriverId() == orderDistribution.getDriverId()){
				distribution.setDistributionState(orderDistribution.getDistributionState());
				distribution.setCreatedDate(orderDistribution.getCreatedDate());
			}
		}
		if (driver.getAttachedOrders().contains(order.getOrderId())){
			driver.getAttachedOrders().remove(order.getOrderId());
		}
		orderDistributor.distributeOrder(order);
		return order;
	}

	@Override
	public Order orderOffer(long orderId, long driverId) throws DaoException, CacheException
	{
		Order order = find(orderId);
		Driver driver = driverService.find(driverId);
		OrderDistribution orderDistribution = new OrderDistribution(driverId);
		orderDistribution.setDistributionState(orderDistributionStateDictionary.getItem(1));//todo
		orderDao.insertOrderDistribution(order.getOrderId(), orderDistribution);
		order.getDistributions().add(orderDistribution);
		driver.getAttachedOrders().add(order.getOrderId());
		return order;
	}

	@Override
	public Order assignDriver(long orderId, long driverId) throws DaoException, CacheException
	{
		Order order = find(orderId);
		Driver driver = driverService.find(driverId);
		OrderDistribution orderDistribution = new OrderDistribution(driverId);
		orderDistribution.setDistributionState(orderDistributionStateDictionary.getItem(2));//todo
		orderDao.insertOrderDistribution(order.getOrderId(), orderDistribution);
		for(OrderDistribution distribution: order.getDistributions()){
			if (distribution.getDriverId() == orderDistribution.getDriverId()){
				distribution.setDistributionState(orderDistribution.getDistributionState());
				distribution.setCreatedDate(orderDistribution.getCreatedDate());
			}
		}
		orderDao.assignDriver(order.getOrderId(), driver.getDriverId());
		order.setDriver(driver);
		if (driver.getOrder() == null){
			driver.setOrder(order);
		}
		return order;
	}

	private void removeFromCache(long orderId) throws CacheException, DaoException
	{
		Order order = orderEntityCache.getItem(orderId);
		if (order == null) return;
		Driver driver = driverService.find(order.getDriver().getDriverId());
		order.setDriver(null);
		order.setUser(null);
		order.setCreator(null);
		orderEntityCache.removeItem(order.getOrderId());
		if (driver == null) return;
		if (driver.getOrder() == order){
			driver.setOrder(null);
		}
		if (driver.getAttachedOrders().contains(order.getOrderId())){
			driver.getAttachedOrders().remove(order.getOrderId());
		}
	}
}
