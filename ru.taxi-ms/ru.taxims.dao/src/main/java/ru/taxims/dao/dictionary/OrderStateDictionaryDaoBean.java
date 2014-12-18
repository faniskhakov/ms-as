package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.OrderState;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer_DB on 28.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class OrderStateDictionaryDaoBean extends DictionaryDaoBean<OrderState>
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
			preparedStatement = connection.prepareStatement("SELECT state_id, \"name\" FROM order_state ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int stateId = result.getInt("state_id");
				OrderState state = new OrderState(stateId);
				state.setName(result.getString("name"));
				state.setAvailableStates(getAvailableStates(stateId, connection));
				items.put(stateId, state);
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

	private List<Integer> getAvailableStates(int stateId, Connection connection) throws SQLException
	{
		List<Integer> states = new ArrayList<Integer>();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT state_id, old_state_id " +
			"  FROM order_state_transition_rule " +
			" WHERE old_state_id = ?" +
			" ORDER BY state_id ASC ");
		preparedStatement.setInt(1, stateId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			states.add(result.getInt("state_id"));
		}
		result.close();
		preparedStatement.close();
		return states;
	}

	public int insertOrderState(OrderState state, List<Integer> availableStates) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_STATE_SQL = "INSERT INTO order_state " +
				" (\"name\") " +
				" VALUES (?) RETURNING state_id ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_STATE_SQL);
			preparedStatement.setString(1, state.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				state.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting order state failed, no generated key obtained.");
			}

			String INSERT_ORDER_STATE_CHANGE_RULE_SQL = "INSERT INTO order_state_transition_rule(\n " +
				"	state_id, old_state_id)\n " +
				" VALUES (?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_STATE_CHANGE_RULE_SQL);
			for (Integer availableState : availableStates) {
				preparedStatement.setInt(1, state.getId());
				preparedStatement.setInt(2, availableState);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			items.put(state.getId(), state);
			return  state.getId();
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
