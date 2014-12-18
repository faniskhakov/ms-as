package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.RatingRule;
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
public class RatingRuleDictionaryDaoBean extends DictionaryDaoBean<RatingRule>
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
			preparedStatement = connection.prepareStatement("SELECT rule_id, \"name\" FROM rating_rule ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int ratingRuleId = result.getInt("rule_id");
				RatingRule ratingRule = new RatingRule(ratingRuleId);
				ratingRule.setName(result.getString("name"));
				items.put(ratingRuleId, ratingRule);
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

	public int insertRatingRule(RatingRule ratingRule) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_RULE_SQL = "INSERT INTO rating_rule " +
				" (\"name\") " +
				" VALUES (?) RETURNING rule_id ";
			preparedStatement = connection.prepareStatement(INSERT_RULE_SQL);
			preparedStatement.setString(1, ratingRule.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				ratingRule.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting rating rule failed, no generated key obtained.");
			}
			items.put(ratingRule.getId(), ratingRule);
			return  ratingRule.getId();
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
