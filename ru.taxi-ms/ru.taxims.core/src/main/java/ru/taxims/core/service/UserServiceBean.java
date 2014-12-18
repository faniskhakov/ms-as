package ru.taxims.core.service;

import org.apache.log4j.Logger;
import ru.taxims.core.cache.EntityCache;
import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.UserService;
import ru.taxims.domain.interfaces.dao.AccountDao;
import ru.taxims.domain.interfaces.dao.CommunicationDao;
import ru.taxims.domain.interfaces.dao.UserDao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * Created by Developer_DB on 12.11.14.
 */
@Stateless
public class UserServiceBean implements UserService
{
	Logger logger = Logger.getLogger(UserServiceBean.class);

	@EJB
	UserDao userDao;
	@EJB
	CommunicationDao communicationDao;
	@EJB
	AccountDao accountDao;
	@EJB(beanName = "UserCacheBean")
	EntityCache<User> userEntityCache;


	@Override
	public long persist(User user) throws DaoException, CacheException
	{
		if (! userDao.verify(user)) {
			throw new DaoException("Verify exception. User is not full");
		}
		if (userEntityCache.getItem(user.getUserId()) != null){
			throw new CacheException("User cache persist exception. User is exists in cache");
		}
		userDao.persist(user);
		userEntityCache.putItem(user);
		return user.getUserId();
	}

	@Override
	public User find(long userId) throws DaoException, CacheException
	{
		User user = userEntityCache.getItem(userId);
		if (user == null) {
			user = userDao.find(userId);
			if (user == null){
				throw new DaoException("Find exception. User is not exists in database");
			}
		}
		return user;
	}

	@Override
	public Map<Long, User> findAll() throws DaoException, CacheException
	{
		return userEntityCache.getItems();
	}

	@Override
	public User changeDefaultCommunication(long userId, long communicationId) throws DaoException, CacheException
	{
		User user = find(userId);
		userDao.changeDefaultCommunication(userId, communicationId);
		user.setCommunication(communicationDao.find(communicationId));
		return user;
	}

	@Override
	public User changeDefaultAccount(long userId, long accountId) throws DaoException, CacheException
	{
		User user = find(userId);
		userDao.changeDefaultAccount(userId, accountId);
		user.setAccount(accountDao.find(accountId));
		return user;
	}

	@Override
	public User disable(long userId) throws DaoException, CacheException
	{
		User user = find(userId);
		userDao.disable(user.getUserId());
		user.setBlockage(true);
		return user;
	}

	@Override
	public User enable(long userId) throws DaoException, CacheException
	{
		User user = find(userId);
		userDao.enable(user.getUserId());
		user.setBlockage(false);
		return user;
	}


	@Override
	public User changeRating(long userId, int rating) throws DaoException, CacheException
	{
		User user = find(userId);
		userDao.changeRating(user.getUserId(), rating);
		user.setRating(rating);
		return user;
	}



//	@Override
//	public void changePosition(User user, Point point) throws DaoException, CacheException
//	{
//		GeoObject geoObject = new GeoObject();
//		geoObject.setPosition(new GeoObjectPosition(point));
//		userDao.changePosition(user.getUserId(), point);
//		if (userEntityCache.getItem(user.getUserId()) != null){
//			userEntityCache.getItem(user.getUserId()).setGeoObject(geoObject);
//		}
//	}
//
//	@Override
//	public void changePosition(User user, GeoObject geoObject) throws DaoException, CacheException
//	{
//		userDao.changePosition(user.getUserId(), geoObject.getPosition());
//		if (userEntityCache.getItem(user.getUserId()) != null){
//			userEntityCache.getItem(user.getUserId()).setGeoObject(geoObjectDictionary.getItem(geoObject.getId()));
//		}
//	}


	@Override
	public void removeFromCache(long userId) throws CacheException
	{
		if (userEntityCache.getItem(userId) != null){
			userEntityCache.removeItem(userId);
		}
	}

	@Override
	public User addToCache(long userId) throws CacheException, DaoException
	{
		User user = userEntityCache.getItem(userId);
		if (user == null) {
			user = userDao.find(userId);
			if (user == null) {
				throw new DaoException("User's communication change exception. User is not exist");
			}
			userEntityCache.putItem(user);
		}
		return user;
	}
}
