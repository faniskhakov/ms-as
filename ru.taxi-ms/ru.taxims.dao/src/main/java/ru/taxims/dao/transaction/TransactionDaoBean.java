package ru.taxims.dao.transaction;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.Transaction;
import ru.taxims.domain.datamodels.TransactionState;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.TransactionDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer_DB on 03.06.14.
 */
@Stateless
public class TransactionDaoBean extends AbstractDaoBean<Transaction> implements TransactionDao
{

	private Logger logger = Logger.getLogger(TransactionDaoBean.class);
	@EJB(beanName = "TransactionStateDictionaryDaoBean")
	protected Dictionary<TransactionState> transactionStateDictionary;

	@Override
	public boolean verify(Transaction transaction)
	{
		return false;
	}

	@Override
	public long persist(Transaction transaction) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		long transactionId;
		try {
			connection = dataSource.getConnection();
			String INSERT_TRANSACTION_SQL = "INSERT INTO transaction( " +
				"	source_account_id, destination_account_id, date_create, " +
				"	amount, state_id, order_id) " +
				" VALUES (?, ?, ?, ?, ?, ?) RETURNING transaction_id ; ";
			preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_SQL);
			preparedStatement.setLong(1, transaction.getSourceAccountId());
			preparedStatement.setLong(2, transaction.getDestinationAccountId());
			preparedStatement.setTimestamp(3, new Timestamp(transaction.getDateCreate().getTime()));
			preparedStatement.setFloat(4, transaction.getAmount());
			preparedStatement.setInt(5, transaction.getState().getId());
			preparedStatement.setLong(6, transaction.getOrderId());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				transactionId = generatedKeys.getLong(1);
			} else {
				throw new DaoException("Creating transaction failed, no generated key obtained.");
			}
			return transactionId;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (generatedKeys != null) {
					generatedKeys.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public Transaction find(long transactionId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Transaction transaction = null;
		try {
			connection = dataSource.getConnection();
			String SELECT_TRANSACTION_SQL = "SELECT transaction_id, source_account_id, " +
				"	destination_account_id, date_create, amount, state_id, order_id " +
				" FROM transaction " +
				" WHERE transaction_id = ?; ";
			preparedStatement = connection.prepareStatement(SELECT_TRANSACTION_SQL);
			preparedStatement.setLong(1, transactionId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				transaction = new Transaction(result.getLong("transaction_id"));
				transaction.setSourceAccountId(result.getLong("source_account_id"));
				transaction.setDestinationAccountId(result.getLong("destination_account_id"));
				transaction.setDateCreate(result.getTimestamp("date_create"));
				transaction.setAmount(result.getFloat("amount"));
				transaction.setState(transactionStateDictionary.getItem(result.getInt("state_id")));
				transaction.setOrderId(result.getLong("order_id"));
			}
			else {
				throw new SQLException("Selecting transaction failed, no rows affected.");
			}
			return  transaction;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public List<Transaction> findOrderTransactions(long orderId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			connection = dataSource.getConnection();
			String SELECT_TRANSACTION_SQL = "SELECT transaction_id, source_account_id, " +
				"	destination_account_id, date_create, amount, state_id, order_id " +
				" FROM transaction " +
				" WHERE order_id = ?; ";
			preparedStatement = connection.prepareStatement(SELECT_TRANSACTION_SQL);
			preparedStatement.setLong(1, orderId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				Transaction transaction = new Transaction(result.getLong("transaction_id"));
				transaction.setSourceAccountId(result.getLong("source_account_id"));
				transaction.setDestinationAccountId(result.getLong("destination_account_id"));
				transaction.setDateCreate(result.getTimestamp("date_create"));
				transaction.setAmount(result.getFloat("amount"));
				transaction.setState(transactionStateDictionary.getItem(result.getInt("state_id")));
				transaction.setOrderId(result.getLong("order_id"));
				transactions.add(transaction);
			}
			return  transactions;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public List<Transaction> findTransactions(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			connection = dataSource.getConnection();
			String SELECT_TRANSACTION_SQL = "SELECT transaction_id, source_account_id, " +
				"	destination_account_id, date_create, amount, state_id, order_id " +
				" FROM transaction " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_TRANSACTION_SQL);
			while (result.next()) {
				Transaction transaction = new Transaction(result.getLong("transaction_id"));
				transaction.setSourceAccountId(result.getLong("source_account_id"));
				transaction.setDestinationAccountId(result.getLong("destination_account_id"));
				transaction.setDateCreate(result.getTimestamp("date_create"));
				transaction.setAmount(result.getFloat("amount"));
				transaction.setState(transactionStateDictionary.getItem(result.getInt("state_id")));
				transaction.setOrderId(result.getLong("order_id"));
				transactions.add(transaction);
			}
			return  transactions;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void changeTransactionState(
		Transaction transaction, int newTransactionState, String comment) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TRANSACTION_CHANGE_SQL = "INSERT INTO transaction_state_change( " +
				"	transaction_id, state_id, old_state_id, comment) " +
				" VALUES (?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_CHANGE_SQL);
			preparedStatement.setLong(1, transaction.getTransactionId());
			preparedStatement.setInt(2, newTransactionState);
			preparedStatement.setInt(3, transaction.getState().getId());
			preparedStatement.setString(4, comment);
			preparedStatement.executeUpdate();
			String UPDATE_TRANSACTION_SQL = "UPDATE transaction  " +
				"	SET state_id = ? " +
				" WHERE transaction_id = ?; ";
			preparedStatement = connection.prepareStatement(UPDATE_TRANSACTION_SQL);
			preparedStatement.setInt(1, newTransactionState);
			preparedStatement.setLong(2, transaction.getTransactionId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
					logger.error(e + " Transaction was rolled back");
				} catch(SQLException excep) {
					logger.error(excep + " Error. Transaction was not rolled back");
				}
			}
			throw new DaoException(e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}
