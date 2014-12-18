package ru.taxims.dao.communication;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.Communication;
import ru.taxims.domain.datamodels.CommunicationType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.CommunicationDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer_DB on 03.06.14.
 */
@Stateless
public class CommunicationDaoBean extends AbstractDaoBean<Communication> implements CommunicationDao
{
	private Logger logger = Logger.getLogger(CommunicationDaoBean.class);
	@EJB(beanName = "CommunicationTypeDictionaryDaoBean")
	protected Dictionary<CommunicationType> communicationTypeDictionary;

	@Override
	public boolean verify(Communication communication)
	{
		return !((communication == null) ||
			(communication.getNumber() == null) ||
			(communication.getType().getId() != 0) ||
			(communication.getUserId() == 0));
	}

	@Override
	public long persist(Communication communication) throws DaoException
	{
		if (!verify(communication)) {
			throw new DaoException("Verify exception. Communication is not full");
		}
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("INSERT INTO communication " +
				"(user_id, number, type_id) VALUES (?, ?, ?) RETURNING communication_id ");
			preparedStatement.setLong(1, communication.getUserId());
			preparedStatement.setString(2, communication.getNumber());
			preparedStatement.setInt(3, communication.getType().getId());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				communication.setCommunicationId(generatedKeys.getLong(1));
			} else {
				throw new DaoException("Creating communication failed, no generated key obtained.");
			}
			return communication.getCommunicationId();
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
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}


	@Override
	public Communication find(long communicationId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Communication communication;
		try {
			connection = dataSource.getConnection();
			String SELECT_COMMUNICATION_SQL = "SELECT communication_id, user_id, \"number\", " +
				" 	type_id, date_create " +
				" FROM communication " +
				" WHERE communication_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_COMMUNICATION_SQL);
			preparedStatement.setLong(1, communicationId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				communication = new Communication(result.getLong("communication_id"));
				communication.setUserId(result.getLong("user_id"));
				communication.setNumber(result.getString("number"));
				communication.setDateCreate(result.getTimestamp("date_create"));
				communication.setType(communicationTypeDictionary.getItem(result.getInt("type_id")));
			} else {
				throw new DaoException("Selecting communication failed, no rows affected.");
			}
			return communication;
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

	@Override
	public Communication findCommunication(String number) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Communication communication;
		try {
			connection = dataSource.getConnection();
			String SELECT_COMMUNICATION_SQL = "SELECT communication_id, user_id, \"number\", " +
				" 	type_id, date_create " +
				" FROM communication " +
				" WHERE \"number\" ILIKE ? ";
			preparedStatement = connection.prepareStatement(SELECT_COMMUNICATION_SQL);
			preparedStatement.setString(1, number);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				communication = new Communication(result.getLong("communication_id"));
				communication.setUserId(result.getLong("user_id"));
				communication.setNumber(result.getString("number"));
				communication.setDateCreate(result.getTimestamp("date_create"));
				communication.setType(communicationTypeDictionary.getItem(result.getInt("type_id")));
			} else {
				throw new DaoException("Selecting communication failed, no rows affected.");
			}
			return communication;
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

	@Override
	public List<Communication> findCommunications(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		Communication communication;
		List<Communication> communications = new ArrayList<Communication>();
		try {
			connection = dataSource.getConnection();
			String SELECT_COMMUNICATION_SQL = "SELECT communication_id, user_id, \"number\", " +
				" 	type_id, date_create " +
				" FROM communication " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_COMMUNICATION_SQL);
			while (result.next()) {
				communication = new Communication(result.getLong("communication_id"));
				communication.setUserId(result.getLong("user_id"));
				communication.setNumber(result.getString("number"));
				communication.setDateCreate(result.getTimestamp("date_create"));
				communication.setType(communicationTypeDictionary.getItem(result.getInt("type_id")));
				communications.add(communication);
			}
			return communications;
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void disable(long communicationId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE communication " +
				" SET blockage = true " +
				" WHERE communication_id = ? " );
			preparedStatement.setLong(1, communicationId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
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

	@Override
	public void enable(long communicationId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE communication " +
				" SET blockage = false " +
				" WHERE communication_id = ? " );
			preparedStatement.setLong(1, communicationId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
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
}
