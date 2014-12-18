package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Agent;
import ru.taxims.domain.datamodels.Shift;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;

/**
 * Created by Developer_DB on 30.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("AgentDictionaryDaoBean")
public class ShiftDictionaryDaoBean extends DictionaryDaoBean<Shift>
{
	@EJB(beanName = "AgentDictionaryDaoBean")
	protected Dictionary<Agent> agentDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT shift_id, name, agent_id FROM shift ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int shiftId = result.getInt("shift_id");
				Shift shift = new Shift(shiftId);
				shift.setName(result.getString("name"));
				shift.setAgent(agentDictionary.getItem(result.getInt("agent_id")));
				items.put(shiftId, shift);
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

	public int insertShift(Shift shift) throws DaoException
	{
		assert dataSource != null;
		int shiftId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_SHIFT_SQL = "INSERT INTO shift " +
				" (\"name\", agent_id) " +
				" VALUES (?, ?) RETURNING shift_id ";
			preparedStatement = connection.prepareStatement(INSERT_SHIFT_SQL);
			preparedStatement.setString(1, shift.getName());
			preparedStatement.setInt(2, shift.getAgent().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				shift.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting shift failed, no generated key obtained.");
			}
			items.put(shift.getId(), shift);
			return  shift.getId();
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
