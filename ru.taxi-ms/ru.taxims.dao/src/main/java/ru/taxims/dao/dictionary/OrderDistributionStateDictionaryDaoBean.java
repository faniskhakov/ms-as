package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.OrderDistributionState;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 11.09.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class OrderDistributionStateDictionaryDaoBean extends DictionaryDaoBean<OrderDistributionState>
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
			preparedStatement = connection.prepareStatement("SELECT state_id, name FROM order_distribution_state ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				OrderDistributionState state = new OrderDistributionState();
				state.setId(result.getInt("state_id"));
				state.setName(result.getString("name"));
				items.put(state.getId(), state);
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

	public int insertOrderState(OrderDistributionState state) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_STATE_SQL = "INSERT INTO order_distribution_state(name) VALUES (?) RETURNING state_id ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_STATE_SQL);
			preparedStatement.setString(1, state.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				state.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting order state failed, no generated key obtained.");
			}
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
