package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.MessageType;
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
public class MessageTypeDictionaryDaoBean extends DictionaryDaoBean<MessageType>
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
			preparedStatement = connection.prepareStatement("SELECT type_id, \"name\" FROM message_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int messageTypeId = result.getInt("type_id");
				MessageType messageType = new MessageType(messageTypeId);
				messageType.setName(result.getString("name"));
				items.put(messageTypeId, messageType);
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

	public int insertMessageType(MessageType messageType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_SQL = "INSERT INTO message_type " +
				" (\"name\") " +
				" VALUES (?) RETURNING message_id ";
			preparedStatement = connection.prepareStatement(INSERT_SQL);
			preparedStatement.setString(1, messageType.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				messageType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting messageType failed, no generated key obtained.");
			}
			items.put(messageType.getId(), messageType);
			return  messageType.getId();
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
