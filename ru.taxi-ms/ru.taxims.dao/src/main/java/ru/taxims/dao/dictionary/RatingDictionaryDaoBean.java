package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Rating;
import ru.taxims.domain.datamodels.RatingRule;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("RatingRuleDictionaryDaoBean")
public class RatingDictionaryDaoBean extends DictionaryDaoBean<Rating>
{
	@EJB(beanName = "RatingRuleDictionaryDaoBean")
	protected Dictionary<RatingRule> ratingRuleDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT rating_id, name, value, rule_id, min, max, " +
				" 	\"group\", active, bind_operator, bind_driver " +
				" FROM rating; ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int ratingId = result.getInt("rating_id");
				Rating rating = new Rating(ratingId);
				rating.setName(result.getString("name"));
				rating.setValue(result.getFloat("value"));
				rating.setRule(ratingRuleDictionary.getItem(result.getInt("rule_id")));
				rating.setMin(result.getFloat("min"));
				rating.setMax(result.getFloat("max"));
				rating.setGroup(result.getInt("group"));
				rating.setActive(result.getBoolean("active"));
				rating.setBindOperator(result.getBoolean("bind_operator"));
				rating.setBindDriver(result.getBoolean("bind_driver"));
				items.put(ratingId, rating);
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

	public int insertRating(Rating rating) throws DaoException
	{
		assert dataSource != null;
		int ratingId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_RATING_SQL = "INSERT INTO rating( " +
				"	name, value, rule_id, min, max, \"group\", active, bind_operator, bind_driver) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING rating_id ";
			preparedStatement = connection.prepareStatement(INSERT_RATING_SQL);
			preparedStatement.setString(1, rating.getName());
			preparedStatement.setFloat(2, rating.getValue());
			preparedStatement.setInt(3, rating.getRule().getId());
			preparedStatement.setFloat(4, rating.getMin());
			preparedStatement.setFloat(5, rating.getMax());
			preparedStatement.setInt(6, rating.getGroup());
			preparedStatement.setBoolean(7, rating.isActive());
			preparedStatement.setBoolean(8, rating.isBindOperator());
			preparedStatement.setBoolean(9, rating.isBindDriver());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				rating.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting rating failed, no generated key obtained.");
			}
			items.put(rating.getId(), rating);
			return  rating.getId();
		} catch (SQLException e) {
			logger.error(e + " Transaction was rolled back");
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
