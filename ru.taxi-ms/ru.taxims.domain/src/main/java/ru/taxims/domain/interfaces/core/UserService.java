package ru.taxims.domain.interfaces.core;

import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.Map;

/**
 * Created by Developer_DB on 12.11.14.
 */
@Local
public interface UserService
{
	public long persist(User user) throws DaoException, CacheException;
	public User find(long userId) throws DaoException, CacheException;
	public Map<Long, User> findAll() throws DaoException, CacheException;
	public User changeDefaultCommunication(long userId, long communicationId) throws DaoException, CacheException;
	public User changeDefaultAccount(long userId, long accountId) throws DaoException, CacheException;
	public User disable(long userId) throws DaoException, CacheException;
	public User enable(long userId) throws DaoException, CacheException;
	public User changeRating(long userId, int rating) throws DaoException, CacheException;
	public void removeFromCache(long userId) throws  CacheException;
	public User addToCache(long userId) throws CacheException, DaoException;
}
