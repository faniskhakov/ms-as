package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.Transaction;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 30.05.14.
 */
@Local
public interface TransactionDao extends Dao<Transaction>
{
	List<Transaction> findOrderTransactions(long orderId) throws DaoException;
	List<Transaction> findTransactions(String sql) throws DaoException;
	void changeTransactionState(Transaction transaction, int newTransactionState, String comment)throws DaoException;
}
