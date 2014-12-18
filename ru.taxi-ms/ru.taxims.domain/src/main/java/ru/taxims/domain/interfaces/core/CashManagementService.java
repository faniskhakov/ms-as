package ru.taxims.domain.interfaces.core;

import ru.taxims.domain.datamodels.Account;
import ru.taxims.domain.datamodels.Transaction;
import ru.taxims.domain.exception.DaoException;

import java.util.List;

/**
 * Created by Developer_DB on 03.12.14.
 */
public interface CashManagementService
{
	public long persist(Account account) throws DaoException;
	public Account find(long accountId) throws DaoException;
	public List<Account> findAccounts(String sql) throws DaoException;
	public void disable(long accountId) throws DaoException;
	public void enable(long accountId) throws DaoException;
	public Transaction insertTransaction(Transaction transaction) throws DaoException;
	public Transaction reverseTransaction(Transaction transaction) throws DaoException;
	public void changeTransactionState(long transactionId, int stateId) throws DaoException;
}
