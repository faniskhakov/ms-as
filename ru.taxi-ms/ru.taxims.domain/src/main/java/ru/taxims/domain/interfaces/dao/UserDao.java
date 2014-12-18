package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 21.05.14.
 */
@Local
public interface UserDao extends Dao<User>
{
//	User find(User user) throws DaoException;
	void merge(User user) throws DaoException;
	List<User> findUsers(String sql) throws DaoException;
	void changeDefaultCommunication(long userId, long communicationId) throws DaoException;
	void changeDefaultAccount(long userId, long accountId) throws DaoException;
	void changeRating(long userId, int addRating) throws DaoException;
	void enable(long userId) throws DaoException;
	void disable(long userId) throws DaoException;
}
