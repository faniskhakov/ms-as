package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.PriorityRule;
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
public class PriorityRuleDictionaryDaoBean extends DictionaryDaoBean<PriorityRule>
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
			preparedStatement = connection.prepareStatement("SELECT rule_id, \"name\" FROM priority_rule ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int priorityRuleId = result.getInt("rule_id");
				PriorityRule priorityRule = new PriorityRule(priorityRuleId);
				priorityRule.setName(result.getString("name"));
				items.put(priorityRuleId, priorityRule);
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

	public int insertPriorityRule(PriorityRule priorityRule) throws DaoException
	{
		assert dataSource != null;
		int priorityRuleId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_RULE_SQL = "INSERT INTO priority_rule " +
				" (\"name\") " +
				" VALUES (?) RETURNING rule_id ";
			preparedStatement = connection.prepareStatement(INSERT_RULE_SQL);
			preparedStatement.setString(1, priorityRule.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				priorityRule.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting priority rule failed, no generated key obtained.");
			}
			items.put(priorityRule.getId(), priorityRule);
			return  priorityRule.getId();
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
