package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.TariffRule;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 05.06.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class TariffRuleDictionaryDaoBean extends DictionaryDaoBean<TariffRule>
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
			preparedStatement = connection.prepareStatement("SELECT rule_id, \"name\" FROM tariff_rule ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int ruleId = result.getInt("rule_id");
				TariffRule tariffRule = new TariffRule(ruleId);
				tariffRule.setName(result.getString("name"));
				items.put(ruleId, tariffRule);
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

	public int insertTariffRule(TariffRule tariffRule) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TYPE_SQL = "INSERT INTO tariff_rule " +
				" (\"name\") " +
				" VALUES (?) RETURNING rule_id ";
			preparedStatement = connection.prepareStatement(INSERT_TYPE_SQL);
			preparedStatement.setString(1, tariffRule.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				tariffRule.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting tariff rule failed, no generated key obtained.");
			}
			items.put(tariffRule.getId(), tariffRule);
			return  tariffRule.getId();
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
