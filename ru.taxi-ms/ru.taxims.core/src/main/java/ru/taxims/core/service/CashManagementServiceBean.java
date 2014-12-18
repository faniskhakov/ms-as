package ru.taxims.core.service;

import ru.taxims.domain.datamodels.Account;
import ru.taxims.domain.datamodels.Transaction;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.CashManagementService;
import ru.taxims.domain.interfaces.dao.AccountDao;
import ru.taxims.domain.interfaces.dao.TransactionDao;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer_DB on 03.12.14.
 */
public class CashManagementServiceBean implements CashManagementService
{
	@EJB
	AccountDao accountDao;

	@EJB
	TransactionDao transactionDao;


	@Override
	public long persist(Account account) throws DaoException
	{
		return accountDao.persist(account);
	}

	@Override
	public Account find(long accountId) throws DaoException
	{
		return accountDao.find(accountId);
	}

	@Override
	public List<Account> findAccounts(String sql) throws DaoException
	{
		return accountDao.findAccounts(sql);
	}

	@Override
	public void disable(long accountId) throws DaoException
	{
		accountDao.disable(accountId);
	}

	@Override
	public void enable(long accountId) throws DaoException
	{
		accountDao.enable(accountId);
	}

	@Override
	public Transaction insertTransaction(Transaction transaction) throws DaoException
	{
		accountDao.changeAmount(transaction.getSourceAccountId(), -transaction.getAmount());
		accountDao.changeAmount(transaction.getDestinationAccountId(), transaction.getAmount());
		transaction.setTransactionId(transactionDao.persist(transaction));
		return transaction;
	}

	@Override
	public Transaction reverseTransaction(Transaction transaction) throws DaoException
	{
		Transaction reversedTransaction = new Transaction();
		reversedTransaction.setAmount(transaction.getAmount());
		reversedTransaction.setSourceAccountId(transaction.getDestinationAccountId());
		reversedTransaction.setDestinationAccountId(transaction.getSourceAccountId());
		reversedTransaction.setDateCreate(new Date());
		reversedTransaction.setOrderId(transaction.getOrderId());
		reversedTransaction.setState(transaction.getState());
		accountDao.changeAmount(reversedTransaction.getSourceAccountId(), -transaction.getAmount());
		accountDao.changeAmount(reversedTransaction.getDestinationAccountId(), transaction.getAmount());
		transactionDao.persist(reversedTransaction);
		return reversedTransaction;
	}

	@Override
	public void changeTransactionState(long transactionId, int stateId) throws DaoException
	{

	}
}
