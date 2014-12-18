package ru.taxims.dao.user;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.UserDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 21.05.14.
 */
@Stateless
public class UserDaoBean extends AbstractDaoBean<User> implements UserDao
{
	private Logger logger = Logger.getLogger(UserDaoBean.class);
	@EJB(beanName = "SourceDictionaryDaoBean")
	protected Dictionary<Source> sourceDictionary;
	@EJB(beanName = "LanguageDictionaryDaoBean")
	protected Dictionary<Language> languageDictionary;
	@EJB(beanName = "CityDictionaryDaoBean")
	protected Dictionary<City> cityDictionary;
	@EJB(beanName = "CommunicationTypeDictionaryDaoBean")
	protected Dictionary<CommunicationType> communicationTypeDictionary;
	@EJB(beanName = "AccountTypeDictionaryDaoBean")
	protected Dictionary<AccountType> accountTypeDictionary;
	@EJB(beanName = "RoleTypeDictionaryDaoBean")
	protected Dictionary<RoleType> roleTypeDictionary;


	@Override
	public boolean verify(User instance) throws DaoException
	{
		return false;
	}

	@Override
	public long persist(User user) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_USER_SQL = "INSERT INTO \"user\" " +
				" 	(email, \"name\", surname, login, password, source_id, language_id, " +
				" 	city_id, private_key, public_key, gender) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id";
			preparedStatement = connection.prepareStatement(INSERT_USER_SQL);
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getName());
			preparedStatement.setString(3, user.getSurname());
			preparedStatement.setString(4, user.getLogin());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setInt(6, user.getSource().getId());
			preparedStatement.setInt(7, user.getLanguage().getId());
			preparedStatement.setInt(8, user.getCity().getId());
			preparedStatement.setString(9, user.getPrivateKey());
			preparedStatement.setString(10, user.getPublicKey());
			preparedStatement.setString(11, String.valueOf(user.getGender()));
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				user.setUserId(generatedKeys.getLong(1));
			} else {
				throw new DaoException("Creating user failed, no generated key obtained.");
			}

			String INSERT_FEATURE_SQL =
				"INSERT INTO entity_feature(feature_id, entity_id, value, role_type_id) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			for (Map.Entry<Integer, String> entry : user.getFeatures().entrySet()) {
				preparedStatement.setInt(1, entry.getKey());
				preparedStatement.setLong(2, user.getUserId());
				preparedStatement.setString(3, entry.getValue());
				preparedStatement.setInt(4, (user instanceof Driver) ? 2 : 1);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();

			String INSERT_ACCOUNT_SQL = "INSERT INTO account(  " +
				"	type_id, user_id, amount)  " +
				" VALUES (?, ?, ?) RETURNING account_id ";
			preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL);
			preparedStatement.setInt(1, user.getAccount().getType().getId());
			preparedStatement.setLong(2, user.getUserId());
			preparedStatement.setFloat(3, user.getAccount().getAmount());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				user.getAccount().setAccountId(generatedKeys.getLong(1));
			} else {
				throw new DaoException("Creating account failed, no generated key obtained.");
			}

			String INSERT_COMMUNICATION_SQL = "INSERT INTO communication " +
				"(user_id, number, type_id) VALUES (?, ?, ?) RETURNING communication_id";
			preparedStatement = connection.prepareStatement(INSERT_COMMUNICATION_SQL);
			preparedStatement.setLong(1, user.getUserId());
			preparedStatement.setString(2, user.getCommunication().getNumber());
			preparedStatement.setInt(3, user.getCommunication().getType().getId());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				user.getCommunication().setCommunicationId(generatedKeys.getLong(1));
			} else {
				throw new DaoException("Creating communication failed, no generated key obtained.");
			}

			String INSERT_ROLE_SQL = "INSERT INTO role( " +
				"	user_id, role_type_id, account_id, communication_id, rating)\n" +
				" VALUES (?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_ROLE_SQL);
			preparedStatement.setLong(1, user.getUserId());
			preparedStatement.setInt(2, (user instanceof Driver) ? 2 : 1);
			preparedStatement.setLong(3, user.getAccount().getAccountId());
			preparedStatement.setLong(4, user.getCommunication().getCommunicationId());
			preparedStatement.setInt(5, user.getRating());
			preparedStatement.executeUpdate();
			return user.getUserId();
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
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void merge(User user) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String UPDATE_USER_SQL = "UPDATE \"user\" " +
				" 	SET email = ?, \"name\" = ?, surname = ?, login = ?, password = ?, source_id = ?, language_id = ?, " +
				" 	city_id = ?, gender = ?) " +
				" WHERE user_id = ? ";
			preparedStatement = connection.prepareStatement(UPDATE_USER_SQL);
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getName());
			preparedStatement.setString(3, user.getSurname());
			preparedStatement.setString(4, user.getLogin());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setInt(6, user.getSource().getId());
			preparedStatement.setInt(7, user.getLanguage().getId());
			preparedStatement.setInt(8, user.getCity().getId());
			preparedStatement.setString(9, String.valueOf(user.getGender()));
			preparedStatement.setLong(10, user.getUserId());
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
	public User find(long id) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		User user;
		try {
			connection = dataSource.getConnection();
			String SELECT_USER_SQL = "SELECT email, name, surname, login, password, " +
				" 	source_id, language_id, city_id, private_key, public_key, gender, " +
				"	r.user_id, r.rating, r.communication_id, r.account_id, r.blockage, r.date_create, " +
				"	c.number, c.type_id AS c_type_id, c.date_create AS c_date_create, c.blockage AS c_blockage ,  " +
				"	a.type_id AS a_type_id, a.amount, a.date_create AS a_date_create, a.blockage AS a_blockage " +
				" FROM \"user\" " +
				" JOIN role r USING(user_id) " +
				" JOIN communication c USING(communication_id) " +
				" JOIN account a USING(account_id) " +
				" WHERE r.user_id = ? AND role_type_id = 1";
			preparedStatement = connection.prepareStatement(SELECT_USER_SQL);
			preparedStatement.setLong(1, id);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				user = new User(result.getLong("user_id"));
				user.setEmail(result.getString("email"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
				user.setLogin(result.getString("login"));
				user.setPassword(result.getString("password"));
				user.setSource(sourceDictionary.getItem(result.getInt("source_id")));
				user.setLanguage(languageDictionary.getItem(result.getInt("language_id")));
				user.setCity(cityDictionary.getItem(result.getInt("city_id")));
				user.setPrivateKey(result.getString("private_key"));
				user.setPublicKey(result.getString("public_key"));
				user.setGender(result.getString("gender").charAt(0));
				user.setBlockage(result.getBoolean("blockage"));
				user.setRating(result.getInt("rating"));
				user.setDateCreate(result.getTimestamp("date_create"));
					Communication communication = new Communication(result.getLong("communication_id"));
					communication.setUserId(result.getLong("user_id"));
					communication.setNumber(result.getString("number"));
					communication.setDateCreate(result.getTimestamp("c_date_create"));
					communication.setType(communicationTypeDictionary.getItem(result.getInt("c_type_id")));
					communication.setBlockage(result.getBoolean("c_blockage"));
				user.setCommunication(communication);
					Account account = new Account(result.getLong("account_id"));
					account.setType(accountTypeDictionary.getItem(result.getInt("a_type_id")));
					account.setUserId(result.getLong("user_id"));
					account.setAmount(result.getFloat("amount"));
					account.setDateCreate(result.getTimestamp("a_date_create"));
					account.setBlockage(result.getBoolean("a_blockage"));
				user.setAccount(account);
				user.setFeatures(findFeatures(user.getUserId(), 1, connection));
				user.setRoles(getRoles(user.getUserId(), connection));
			} else {
				throw new DaoException("Selecting user failed, no rows affected.");
			}
			return user;
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
	public List<User> findUsers(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		List<User> users = new ArrayList<>();
		try {
			connection = dataSource.getConnection();
			String SELECT_USER_SQL = "SELECT email, name, surname, login, password, " +
				" 	source_id, language_id, city_id, private_key, public_key, gender, " +
				"	r.user_id, r.rating, r.communication_id, r.account_id, r.blockage, r.date_create, " +
				"	c.number, c.type_id AS c_type_id, c.date_create AS c_date_create, c.blockage AS c_blockage ,  " +
				"	a.type_id AS a_type_id, a.amount, a.date_create AS a_date_create, a.blockage AS a_blockage " +
				" FROM \"user\" " +
				" JOIN role r USING(user_id) " +
				" JOIN communication c USING(communication_id) " +
				" JOIN account a USING(account_id) " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_USER_SQL);
			while (result.next()) {
				User user = new User(result.getLong("user_id"));
				user.setEmail(result.getString("email"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
				user.setLogin(result.getString("login"));
				user.setPassword(result.getString("password"));
				user.setSource(sourceDictionary.getItem(result.getInt("source_id")));
				user.setLanguage(languageDictionary.getItem(result.getInt("language_id")));
				user.setCity(cityDictionary.getItem(result.getInt("city_id")));
				user.setPrivateKey(result.getString("private_key"));
				user.setPublicKey(result.getString("public_key"));
				user.setGender(result.getString("gender").charAt(0));
				user.setBlockage(result.getBoolean("blockage"));
				user.setRating(result.getInt("rating"));
				user.setDateCreate(result.getTimestamp("date_create"));
					Communication communication = new Communication(result.getLong("communication_id"));
					communication.setUserId(result.getLong("user_id"));
					communication.setNumber(result.getString("number"));
					communication.setDateCreate(result.getTimestamp("c_date_create"));
					communication.setType(communicationTypeDictionary.getItem(result.getInt("c_type_id")));
					communication.setBlockage(result.getBoolean("c_blockage"));
				user.setCommunication(communication);
					Account account = new Account(result.getLong("account_id"));
					account.setType(accountTypeDictionary.getItem(result.getInt("a_type_id")));
					account.setUserId(result.getLong("user_id"));
					account.setAmount(result.getFloat("amount"));
					account.setDateCreate(result.getTimestamp("a_date_create"));
					account.setBlockage(result.getBoolean("a_blockage"));
				user.setAccount(account);
				user.setFeatures(findFeatures(user.getUserId(), 1, connection));
				user.setRoles(getRoles(user.getUserId(), connection));
				users.add(user);
			}
			return users;
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

	public Map<Integer, String> findFeatures(long entityId, int roleTypeId, Connection connection) throws DaoException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Map<Integer, String> features = new HashMap<>();
		try {
			String SELECT_FEATURES_SQL = "SELECT feature_id, value " +
				" FROM entity_feature " +
				" WHERE entity_id = ?  AND role_type_id = ?";
			preparedStatement = connection.prepareStatement(SELECT_FEATURES_SQL);
			preparedStatement.setLong(1, entityId);
			preparedStatement.setInt(2, roleTypeId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				features.put(result.getInt("feature_id"), result.getString("value"));
			}
			return features;
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
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	private List<RoleType> getRoles(long userId, Connection connection) throws SQLException, DaoException
	{
		List<RoleType> roles = new ArrayList<>();
		String SELECT_ROLES_SQL = "SELECT user_id, role_type_id" +
			" FROM \"role\" " +
			" WHERE user_id = ? ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_SQL);
		preparedStatement.setLong(1, userId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			roles.add(roleTypeDictionary.getItem(result.getInt("role_type_id")));
		}
		result.close();
		preparedStatement.close();
		return roles;
	}

	@Override
	public void changeDefaultCommunication(long userId, long communicationId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE \"role\" " +
				" SET communication_id = ? " +
				" WHERE user_id = ? AND role_type_id = 1");
			preparedStatement.setLong(1, communicationId);
			preparedStatement.setLong(2, userId);
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
	public void changeDefaultAccount(long userId, long accountId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE \"role\" " +
				" SET account_id = ? " +
				" WHERE user_id = ?  AND role_type_id = 1 ");
			preparedStatement.setLong(1, accountId);
			preparedStatement.setLong(2, userId);
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
	public void changeRating(long userId, int addRating) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE \"role\"  " +
				" SET rating = rating + ? " +
				" WHERE user_id = ? AND role_type_id = 1");
			preparedStatement.setInt(1, addRating);
			preparedStatement.setLong(2, userId);
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
	public void enable(long userId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE \"role\"  " +
				" SET blockage = false " +
				" WHERE user_id = ?  AND role_type_id = 1");
			preparedStatement.setLong(1, userId);
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
	public void disable(long userId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE \"role\"  " +
				" SET blockage = true " +
				" WHERE user_id = ? AND role_type_id = 1");
			preparedStatement.setLong(1, userId);
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
