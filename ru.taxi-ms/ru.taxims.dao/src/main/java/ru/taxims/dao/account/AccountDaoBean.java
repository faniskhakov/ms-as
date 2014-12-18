package ru.taxims.dao.account;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.Account;
import ru.taxims.domain.datamodels.AccountType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.AccountDao;
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
public class AccountDaoBean extends AbstractDaoBean<Account> implements AccountDao
{
	private Logger logger = Logger.getLogger(AccountDaoBean.class);
	@EJB(beanName = "AccountTypeDictionaryDaoBean")
	private Dictionary<AccountType> accountTypeDictionary;

	@Override
	public boolean verify(Account account)
	{
		return !((account == null) ||
			(account.getUserId() == 0) ||
			(account.getAmount() == 0) ||
			(account.getType().getId() == 0));
	}

	@Override
	public long persist(Account account) throws DaoException
	{
		if (!verify(account)) {
			throw new DaoException("Verify exception. Account is not full");
		}
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ACCOUNT_SQL = "INSERT INTO account(  " +
				"	type_id, user_id, amount, date_create)  " +
				" VALUES (?, ?, ?, ?) RETURNING account_id ";
			preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL);
			preparedStatement.setInt(1, account.getType().getId());
			preparedStatement.setLong(2, account.getUserId());
			preparedStatement.setFloat(3, account.getAmount());
			preparedStatement.setTimestamp(4, new Timestamp(account.getDateCreate().getTime()));
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				account.setAccountId(generatedKeys.getLong(1));
			} else {
				throw new DaoException("Creating account failed, no generated key obtained.");
			}
			return  account.getAccountId();
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
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public Account find(long accountId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Account account = null;
		try {
			connection = dataSource.getConnection();
			String SELECT_ACCOUNT_SQL = "SELECT type_id, user_id, amount, date_create, blockage " +
				"	FROM account " +
				" WHERE account_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_SQL);
			preparedStatement.setLong(1, accountId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				account = new Account((int)accountId);
				account.setType(accountTypeDictionary.getItem(result.getInt("type_id")));
				account.setUserId(result.getLong("user_id"));
				account.setAmount(result.getFloat("amount"));
				account.setDateCreate(result.getTimestamp("date_create"));
				account.setBlockage(result.getBoolean("blockage"));
			} else {
				throw new SQLException("Selecting account failed, no rows affected.");
			}
			return account;
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
	public List<Account> findAccounts(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		List<Account> accounts = new ArrayList<Account>();
		try {
			connection = dataSource.getConnection();
			String SELECT_ACCOUNT_SQL = "SELECT account_id, type_id, user_id, amount, date_create " +
				"	FROM account " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_ACCOUNT_SQL);
			while (result.next()) {
				Account account = new Account(result.getLong("account_id"));
				account.setType(accountTypeDictionary.getItem(result.getInt("type_id")));
				account.setUserId(result.getLong("user_id"));
				account.setAmount(result.getFloat("amount"));
				account.setDateCreate(result.getTimestamp("date_create"));
				accounts.add(account);
			}
			return accounts;
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
	public void changeAmount(long accountId, float amount) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE account " +
				" SET amount = amount + ? " +
				" WHERE account_id = ? ");
			preparedStatement.setFloat(1, amount);
			preparedStatement.setLong(2, accountId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
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


	@Override
	public void disable(long accountId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE account " +
				" SET blockage = true " +
				" WHERE account_id = ? " );
			preparedStatement.setLong(1, accountId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
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

	@Override
	public void enable(long accountId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE account " +
				" SET blockage = false " +
				" WHERE account_id = ? " );
			preparedStatement.setLong(1, accountId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
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
