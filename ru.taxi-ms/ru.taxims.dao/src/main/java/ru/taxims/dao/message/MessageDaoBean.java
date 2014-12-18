package ru.taxims.dao.message;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.Message;
import ru.taxims.domain.datamodels.MessageType;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.MessageDao;
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
public class MessageDaoBean extends AbstractDaoBean<Message> implements MessageDao
{
	private Logger logger = Logger.getLogger(MessageDaoBean.class);
	@EJB(beanName = "MessageTypeDictionaryDaoBean")
	protected Dictionary<MessageType> messageTypeDictionary;


	@Override
	public boolean verify(Message message)
	{
		return false; // todo
	}

	@Override
	public long persist(Message message) throws DaoException
	{
		if (! verify(message)) {
			throw new DaoException("Verify exception. Message is not full");
		}
		long messageId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "INSERT INTO message( " +
				"	sender, addressee, date_create, message_text, priority, type_id, " +
				"	lifetime, order_id) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING message_id ");
			preparedStatement.setLong(1, message.getSender().getUserId());
			preparedStatement.setLong(2, message.getAddressee().getUserId());
			preparedStatement.setTimestamp(3, new Timestamp(message.getDateCreate().getTime()));
			preparedStatement.setString(4, message.getMessageText());
			preparedStatement.setInt(5, message.getPriority());
			preparedStatement.setInt(6, message.getMessageType().getId());
			preparedStatement.setTimestamp(7, new Timestamp(message.getLifetime().getTime()));
			preparedStatement.setLong(8, message.getOrder().getOrderId());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				messageId = generatedKeys.getLong(1);
			} else {
				throw new DaoException("Creating message failed, no generated key obtained.");
			}
			return  messageId;
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
	public Message find(long messageId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Message message;
		try {
			connection = dataSource.getConnection();
			String SELECT_MESSAGE_SQL = " SELECT sender, addressee, date_create, message_text, priority, type_id, " +
				"       lifetime, message_id, order_id " +
				" FROM message " +
				" WHERE message_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_MESSAGE_SQL);
			preparedStatement.setLong(1, messageId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				message = new Message(result.getLong("message_id"));
				message.setSender(new User(result.getLong("sender")));
				message.setAddressee(new User(result.getLong("addressee")));
				message.setDateCreate(result.getTimestamp("date_create"));
				message.setMessageText(result.getString("message_text"));
				message.setPriority(result.getInt("priority"));
				message.setMessageType(messageTypeDictionary.getItem(result.getInt("type_id")));
				message.setLifetime(result.getTimestamp("lifetime"));
				message.setOrder(new Order(result.getLong("order_id")));
			} else {
				throw new SQLException("Selecting car failed, no rows affected.");
			}
			return message;
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
	public List<Message> findOrderMessages(long orderId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		List<Message> messages = new ArrayList<Message>();
		try {
			connection = dataSource.getConnection();
			String SELECT_MESSAGE_SQL = " SELECT sender, addressee, date_create, message_text, priority, type_id, " +
				"       lifetime, message_id, order_id " +
				" FROM message " +
				" WHERE order_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_MESSAGE_SQL);
			preparedStatement.setLong(1, orderId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				Message message = new Message(result.getLong("message_id"));
				message.setSender(new User(result.getLong("sender")));
				message.setAddressee(new User(result.getLong("addressee")));
				message.setDateCreate(result.getTimestamp("date_create"));
				message.setMessageText(result.getString("message_text"));
				message.setPriority(result.getInt("priority"));
				message.setMessageType(messageTypeDictionary.getItem(result.getInt("type_id")));
				message.setLifetime(result.getTimestamp("lifetime"));
				message.setOrder(new Order(result.getLong("order_id")));
				messages.add(message);
			}
			return messages;
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
	public List<Message> findMessages(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		List<Message> messages = new ArrayList<Message>();
		try {
			connection = dataSource.getConnection();
			String SELECT_MESSAGE_SQL = " SELECT sender, addressee, date_create, message_text, priority, type_id, " +
				"       lifetime, message_id, order_id " +
				" FROM message " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_MESSAGE_SQL);
			while (result.next()) {
				Message message = new Message(result.getLong("message_id"));
				message.setSender(new User(result.getLong("sender")));
				message.setAddressee(new User(result.getLong("addressee")));
				message.setDateCreate(result.getTimestamp("date_create"));
				message.setMessageText(result.getString("message_text"));
				message.setPriority(result.getInt("priority"));
				message.setMessageType(messageTypeDictionary.getItem(result.getInt("type_id")));
				message.setLifetime(result.getTimestamp("lifetime"));
				message.setOrder(new Order(result.getLong("order_id")));
				messages.add(message);
			}
			return messages;
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
}
