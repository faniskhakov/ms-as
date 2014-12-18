package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Priority;
import ru.taxims.domain.datamodels.PriorityRule;
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
@DependsOn("PriorityRuleDictionaryDaoBean")
public class PriorityDictionaryDaoBean extends DictionaryDaoBean<Priority>
{
	@EJB(beanName = "PriorityRuleDictionaryDaoBean")
	protected Dictionary<PriorityRule> priorityRuleDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT priority_id, name, value, rule_id, min, max, " +
				"	active, bind_driver, bind_order " +
				" FROM priority; ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int priorityId = result.getInt("priority_id");
				Priority priority = new Priority(priorityId);
				priority.setName(result.getString("name"));
				priority.setValue(result.getFloat("value"));
				priority.setRule(priorityRuleDictionary.getItem(result.getInt("rule_id")));
				priority.setMin(result.getFloat("min"));
				priority.setMax(result.getFloat("max"));
				priority.setActive(result.getBoolean("active"));
				priority.setBindDriver(result.getBoolean("bind_driver"));
				priority.setBindOrder(result.getBoolean("bind_order"));
				items.put(priorityId, priority);
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

	public int insertPriority(Priority priority) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_FEATURE_SQL = "INSERT INTO priority( " +
				"	name, value, rule_id, min, max, active, bind_driver, bind_order) " +
				"    VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING priority_id ";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			preparedStatement.setString(1, priority.getName());
			preparedStatement.setFloat(2, priority.getValue());
			preparedStatement.setInt(3, priority.getRule().getId());
			preparedStatement.setFloat(4, priority.getMin());
			preparedStatement.setFloat(5, priority.getMax());
			preparedStatement.setBoolean(6, priority.isActive());
			preparedStatement.setBoolean(7, priority.isBindDriver());
			preparedStatement.setBoolean(8, priority.isBindOrder());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				priority.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting priority failed, no generated key obtained.");
			}
			items.put(priority.getId(), priority);
			return  priority.getId();
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
