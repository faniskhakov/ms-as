package ru.taxims.core.cache;

import org.apache.log4j.Logger;
import ru.taxims.domain.criteria.UserSearchCriteria;
import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.UserDao;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.List;

/**
 * Created by Developer_DB on 12.11.14.
 */
@Singleton
@Startup
@Local(EntityCache.class)
@DependsOn({"OrderCacheBean"})
public class UserCacheBean  extends EntityCacheBean<User>
{
	Logger logger = Logger.getLogger(OrderCacheBean.class);

	@EJB
	UserDao userDao;

	@Override
	public void putItem(User user) throws CacheException
	{
		super.putItem(user);
		items.put(user.getUserId(), user);
	}

	@PostConstruct
	protected void init() throws DaoException, CacheException
	{
		List<User> users = userDao.findUsers(UserSearchCriteria.blockageCriteria(false));//todo
		for(User user :users){
			putItem(user);
			logger.info(" User  id ="  + user.getUserId() + " Name = " + user.getName());
			logger.info(" User  AccountId ="  + user.getAccount().getAccountId() + " Number = " + user.getCommunication().getNumber());
		}
	}
}
