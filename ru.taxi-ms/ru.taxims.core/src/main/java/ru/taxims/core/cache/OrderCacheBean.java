package ru.taxims.core.cache;

import org.apache.log4j.Logger;
import ru.taxims.domain.criteria.OrderSearchCriteria;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.dao.OrderDao;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer_DB on 07.08.14.
 */
@Singleton
@Startup
@Local(EntityCache.class)
@DependsOn({"OrderStateDictionaryDaoBean", "CityDictionaryDaoBean",
	"FeatureDictionaryDaoBean", "CarBrandDictionaryDaoBean", "GeoObjectDictionaryDaoBean"})
public class OrderCacheBean extends EntityCacheBean<Order>
{
	Logger logger = Logger.getLogger(OrderCacheBean.class);

	@EJB
	OrderDao orderDao;

	@Override
	public void putItem(Order order) throws CacheException
	{
		super.putItem(order);
		items.put(order.getOrderId(), order);
	}

	@PostConstruct
	protected void init() throws DaoException, CacheException, GISException, SQLException
	{
 		List<Order> orders = orderDao.findOrders(OrderSearchCriteria.stateCriteria(1));//todo
		for(Order order :orders){
			putItem(order);
		}
	}
}
