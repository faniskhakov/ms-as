package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.Account;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 30.05.14.
 */
@Local
public interface AccountDao extends Dao<Account>
{
	List<Account> findAccounts(String sql) throws DaoException;
	void changeAmount(long accountId, float amount) throws DaoException;
	void disable(long accountId) throws DaoException;
	void enable(long accountId) throws DaoException;
}

