package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.AccountType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class AccountTypeDictionaryDaoBean extends DictionaryDaoBean<AccountType>
{
	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT type_id, \"name\" FROM account_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int accountTypeId = result.getInt("type_id");
				AccountType accountType = new AccountType(accountTypeId);
				accountType.setName(result.getString("name"));
				items.put(accountTypeId, accountType);
			}
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

	public int insert(AccountType accountType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ACCOUNT_TYPE_SQL = "INSERT INTO account_type " +
				" (\"name\") " +
				" VALUES (?) RETURNING type_id ";
			preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_TYPE_SQL);
			preparedStatement.setString(1, accountType.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				accountType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting accountType failed, no generated key obtained.");
			}
			items.put(accountType.getId(), accountType);
			return  accountType.getId();
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
}
