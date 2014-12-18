package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.FeatureComparisonRule;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 25.09.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class FeatureComparisonRuleDictionaryDaoBean extends DictionaryDaoBean<FeatureComparisonRule>
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
			preparedStatement = connection.prepareStatement("SELECT comparison_rule_id, name FROM feature_comparison_rule ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int comparisonRuleId = result.getInt("comparison_rule_id");
				FeatureComparisonRule comparisonRule = new FeatureComparisonRule(comparisonRuleId);
				comparisonRule.setName(result.getString("name"));
				items.put(comparisonRuleId, comparisonRule);
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

	public int insertFeatureRule(FeatureComparisonRule comparisonRule) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_RULE_SQL = "INSERT INTO feature_comparison_rule(name) VALUES (?) RETURNING comparison_rule_id ";
			preparedStatement = connection.prepareStatement(INSERT_RULE_SQL);
			preparedStatement.setString(1, comparisonRule.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				comparisonRule.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting feature rule failed, no generated key obtained.");
			}
			items.put(comparisonRule.getId(), comparisonRule);
			return  comparisonRule.getId();
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
