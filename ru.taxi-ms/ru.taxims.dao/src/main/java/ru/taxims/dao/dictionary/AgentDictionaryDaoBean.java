package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Agent;
import ru.taxims.domain.datamodels.City;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("CityDictionaryDaoBean")
public class AgentDictionaryDaoBean extends DictionaryDaoBean<Agent>
{
	@EJB(beanName = "CityDictionaryDaoBean")
	protected Dictionary<City> cityDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT agent_id, \"name\", city_id FROM agent ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int agentId = result.getInt("agent_id");
				Agent agent = new Agent(agentId);
				agent.setName(result.getString("name"));
				agent.setCity(cityDictionary.getItem(result.getInt("city_id")));
				items.put(agentId, agent);
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

	public int insertAgent(Agent agent) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_AGENT_SQL = "INSERT INTO agent " +
				" (\"name\", city_id) " +
				" VALUES (?, ?) RETURNING agent_id ";
			preparedStatement = connection.prepareStatement(INSERT_AGENT_SQL);
			preparedStatement.setString(1, agent.getName());
			preparedStatement.setInt(2, agent.getCity().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				agent.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting agent failed, no generated key obtained.");
			}
			items.put(agent.getId(), agent);
			return  agent.getId();
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
