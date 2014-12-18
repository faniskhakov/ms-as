package ru.taxims.dao.order;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.OrderDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Developer_DB on 16.05.14.
 */
@Stateless
//@Name(OrderDao.NAME)
//@JndiName(OrderDao.JNDI_NAME)
//@Local(OrderDao.class)
public class OrderDaoBean extends AbstractDaoBean<Order> implements OrderDao
{

	private Logger logger = Logger.getLogger(OrderDaoBean.class);
	@EJB(beanName = "OrderStateDictionaryDaoBean")
	protected Dictionary<OrderState> orderStateDictionary;
	@EJB(beanName = "CityDictionaryDaoBean")
	protected Dictionary<City> cityDictionary;
	@EJB(beanName = "FeatureRuleDictionaryDaoBean")
	protected Dictionary<FeatureRule> featureRuleDictionary;
	@EJB(beanName = "GeoObjectTypeDictionaryDaoBean")
	protected Dictionary<GeoObjectType> geoObjectType;
	@EJB(beanName = "GeoObjectDescriptionDictionaryDaoBean")
	protected  Dictionary<GeoObjectDescription> geoObjectDescription;
	@EJB(beanName = "GeoObjectDictionaryDaoBean")
	protected  Dictionary<GeoObject> geoObject;
	@EJB(beanName = "OrderDistributionStateDictionaryDaoBean")
	protected  Dictionary<OrderDistributionState> distributionStateDictionary;


	private Pattern pattern = Pattern.compile("^\\d+$");
	public boolean isPhoneNumberValid(String phoneNumber){
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches();
	}

	@Override
	public boolean verify(Order order)
	{
		if ((order == null)
//			||
//			(order.getUser() == null) ||
//			(order.getDriver() != null) ||
//			(order.getCreator() == null) ||
//			(order.getCity() == null) ||
//			(order.getState() == null) ||
//			(order.getDateStart() == null) ||
//			(order.getPhone() == null) ||
//			(isPhoneNumberValid(order.getPhone())) ||
//			(order.getPhoneClient() == null) ||
//			(isPhoneNumberValid(order.getPhoneClient())) ||
//			(order.getPriority() < 0) ||
//			(order.getFeatures() == null) ||
//			(order.getWaypoints() == null)
			)
		{
			return false;
		} else {
			return true;
		}
	}

	@Override
	public long persist(Order order) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			logger.info("         persist(Order order) 1  ");
			String INSERT_ORDER_SQL = "INSERT INTO \"order\" " +
				" (date_start, user_id, state_id, phone, phone_client, priority, creator_id, city_id, " +
				" comment, amount,  parent_id, date_create) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING order_id";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_SQL);
			preparedStatement.setTimestamp(1, new Timestamp(order.getDateStart().getTime()));
			preparedStatement.setLong(2, order.getUser().getUserId());
			preparedStatement.setInt(3, order.getState().getId());
			preparedStatement.setString(4, order.getPhone());
			preparedStatement.setString(5, order.getPhoneClient());
			preparedStatement.setInt(6, order.getPriority());
			preparedStatement.setLong(7, order.getCreator().getUserId());
			preparedStatement.setInt(8, order.getCity().getId());
			preparedStatement.setString(9, order.getComment());
			preparedStatement.setFloat(10, order.getAmount());
			if (order.getParents() != null && order.getParents().get(1) != null ){
				preparedStatement.setLong(11, order.getParents().get(1).getOrderId());
			}else {
				preparedStatement.setNull(11, Types.BIGINT);
			}
			preparedStatement.setTimestamp(12, new Timestamp(order.getDateCreate().getTime()));
			preparedStatement.executeQuery();
			resultSet = preparedStatement.getResultSet();
			if (resultSet.next()) {
				order.setOrderId(resultSet.getLong(1));
			} else {
				throw new DaoException("Creating order failed, no generated key obtained.");
			}

			String INSERT_WAYPOINTS_SQL =
				"INSERT INTO order_waypoint (object_id, order_id, sequence, latitude, longitude, address) " +
					" VALUES (?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_WAYPOINTS_SQL);
			for (Map.Entry<Integer, GeoObject> entry : order.getWaypoints().entrySet()) {
				if (entry.getValue().getId() != 0){
					preparedStatement.setInt(1, entry.getValue().getId());
				}else {
					preparedStatement.setNull(1, Types.INTEGER);
				}
				preparedStatement.setLong(2, order.getOrderId());
				preparedStatement.setInt(3, entry.getKey());
				preparedStatement.setFloat(4, entry.getValue().getPosition().getLatitude());
				preparedStatement.setFloat(5, entry.getValue().getPosition().getLongitude());
				preparedStatement.setString(6, entry.getValue().getAddress());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();

			String INSERT_FEATURE_SQL =
				"INSERT INTO order_feature (feature_id, order_id, value) VALUES (?, ?, ?) ";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			for (Map.Entry<Integer, String> entry : order.getFeatures().entrySet()) {
				preparedStatement.setInt(1, entry.getKey());
				preparedStatement.setLong(2, order.getOrderId());
				preparedStatement.setString(3, entry.getValue());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			return  order.getOrderId();
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
				if (resultSet != null) {
					resultSet.close();
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

	@Override
	public void insertAmount(long orderId, float amount) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_CHANGE_SQL = "UPDATE \"order\" " +
				" SET amount = ? " +
				" WHERE order_id = ? ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_CHANGE_SQL);
			preparedStatement.setFloat(1, amount);
			preparedStatement.setLong(2, orderId);
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
	public void changeState(long orderId, int oldState, int newState, String comment) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_CHANGE_SQL = "INSERT INTO order_state_change " +
				" (order_id, state_id, old_state_id, comment) " +
				" VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_CHANGE_SQL);
			preparedStatement.setLong(1, orderId);
			preparedStatement.setInt(2, newState);
			preparedStatement.setInt(3, oldState);
			preparedStatement.setString(4, comment);
			preparedStatement.executeUpdate();
			String UPDATE_ORDER_SQL = "UPDATE \"order\" " +
				" SET state_id = ? " +
				" WHERE order_id = ? ";
			preparedStatement = connection.prepareStatement(UPDATE_ORDER_SQL);
			preparedStatement.setInt(1, newState);
			preparedStatement.setLong(2, orderId);
			preparedStatement.executeUpdate();
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
	public void assignDriver(long orderId, long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String UPDATE_ORDER_SQL = "UPDATE \"order\" " +
				" SET driver_id = ? " +
				" WHERE order_id = ? ";
			preparedStatement = connection.prepareStatement(UPDATE_ORDER_SQL);
			preparedStatement.setLong(1, driverId);
			preparedStatement.setLong(2, orderId);
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
	public Order find(long orderId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Order order = new Order(orderId);
		try {
			connection = dataSource.getConnection();
			String SELECT_ORDER_SQL = "SELECT date_create, date_start, user_id, state_id, phone, \n" +
				"	phone_client, parent_id, priority, driver_id, creator_id, city_id, \n" +
				"	comment, amount " +
				" FROM \"order\" " +
				" WHERE order_id = ?";
			preparedStatement = connection.prepareStatement(SELECT_ORDER_SQL);
			preparedStatement.setLong(1, orderId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				order.setDateCreate(result.getTimestamp("date_create"));
				order.setDateStart(result.getTimestamp("date_start"));
				order.setUser(new User(result.getLong("user_id")));
				order.setState(orderStateDictionary.getItem(result.getInt("state_id")));
				order.setPhone(result.getString("phone"));
				order.setPhoneClient(result.getString("phone_client"));
				order.setParents(getParents(orderId, result.getLong("parent_id"), connection));
				order.setPriority(result.getInt("priority"));
				order.setDriver(new Driver(result.getLong("driver_id")));
				order.setCreator(new User(result.getLong("creator_id")));
				order.setCity(cityDictionary.getItem(result.getInt("city_id")));
				order.setComment(result.getString("comment"));
				order.setAmount(result.getFloat("amount"));
				order.setFeatures(getFeatures(orderId, connection));
				order.setWaypoints(getWaypointsCache(orderId, connection));
				order.setDistributions(getOrderDistributions(orderId, connection));
			} else {
				throw new SQLException("Selecting order failed, no rows affected.");
			}
			return order;
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
	public List<Order> findOrders(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		Order order;
		List<Order> orders = new LinkedList<>();
		try {
			connection = dataSource.getConnection();
			String SELECT_ORDER_SQL = "SELECT order_id, date_create, date_start, user_id, state_id, phone,   " +
				"       phone_client, parent_id, priority, driver_id, creator_id, city_id,  " +
				"       comment, amount  " +
				"  FROM \"order\" "  + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_ORDER_SQL);
			while (result.next()) {
				order = new Order(result.getLong("order_id"));
				order.setDateCreate(result.getTimestamp("date_create"));
				order.setDateStart(result.getTimestamp("date_start"));
				order.setUser(new User(result.getLong("user_id")));
				order.setState(orderStateDictionary.getItem(result.getInt("state_id")));
				order.setPhone(result.getString("phone"));
				order.setPhoneClient(result.getString("phone_client"));
				order.setParents(getParents(order.getOrderId(), result.getLong("parent_id"), connection));
				order.setPriority(result.getInt("priority"));
				order.setDriver(new Driver(result.getLong("driver_id")));
				order.setCreator(new User(result.getLong("creator_id")));
				order.setCity(cityDictionary.getItem(result.getInt("city_id")));
				order.setComment(result.getString("comment"));
				order.setAmount(result.getFloat("amount"));
				order.setFeatures(getFeatures(order.getOrderId(), connection));//TODO
				order.setWaypoints(getWaypointsCache(order.getOrderId(), connection));//TODO
				order.setDistributions(getOrderDistributions(order.getOrderId(), connection));
				orders.add(order);
			}
			return orders;
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

	private Map<Integer, GeoObject> getWaypoints(long orderId, Connection connection) throws SQLException, DaoException
	{
		Map<Integer, GeoObject> waypoints = new HashMap<>();
		GeoObject tempWaypoint = null;
		String address = "";
		int tempKey = 1;
		String SELECT_WAYPOINTS_SQL = " SELECT DISTINCT " +
			"  w.sequence, \n" +
			"  w.address, \n" +
			"  o.object_id, \n" +
			"  o.name, \n" +
			"  o.object_type_id, \n" +
			"  o.description_id, \n" +
			"  p.latitude, \n" +
			"  p.longitude, \n" +
			"  p.section_id, \n" +
			"  t.level  " +
			" FROM order_waypoint w " +
			" JOIN object_link l ON (l.object_id = w.object_id) \n" +
			" JOIN \"object\" o ON (o.object_id = l.parent_id OR o.object_id = l.object_id) \n" +
			" JOIN object_position p ON (p.position_id = o.position_id) \n" +
			" JOIN object_type t ON (t.object_type_id = o.object_type_id) \n" +
			" WHERE w.order_id = ? \n" +
			" ORDER BY w.sequence ASC, t.level ASC ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_WAYPOINTS_SQL);
		preparedStatement.setLong(1, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			GeoObject waypoint = new GeoObject();
			waypoint.setId(result.getInt("object_id"));
			GeoObjectPosition position = new GeoObjectPosition(result.getFloat("latitude"), result.getFloat("longitude"));
			position.setSectionId(result.getInt("section_id"));
			waypoint.setPosition(position);
			waypoint.setName(result.getString("name"));
			waypoint.setType(geoObjectType.getItem(result.getInt("object_type_id")));
			waypoint.setDescription(geoObjectDescription.getItem(result.getInt("description_id")));
			int key = result.getInt("sequence");
			if (key != tempKey && tempWaypoint != null) {
				tempWaypoint.setAddress(address);
				waypoints.put(tempKey, tempWaypoint);
				tempWaypoint = null;
			}
			waypoint.setParent(tempWaypoint);
			tempWaypoint = waypoint;
			tempKey = key;
			address = result.getString("address");
		}
		tempWaypoint.setAddress(address);
		waypoints.put(tempKey, tempWaypoint);
		result.close();
		preparedStatement.close();
		return  waypoints;
	}

	private Map<Integer, GeoObject> getOnlyWaypoints(long orderId, Connection connection) throws SQLException, DaoException
	{
		Map<Integer, GeoObject> waypoints = new HashMap<Integer, GeoObject>();
		String SELECT_WAYPOINTS_SQL = "SELECT object_id, " +
			" order_id, " +
			" sequence, " +
			" latitude, " +
			" longitude, " +
			" address " +
			" FROM order_waypoint " +
			" WHERE order_id = ? " +
			" ORDER BY sequence ASC ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_WAYPOINTS_SQL);
		preparedStatement.setLong(1, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			GeoObject waypoint = new GeoObject();
			waypoint.setId(result.getInt("object_id"));
			waypoint.setPosition(new GeoObjectPosition(result.getFloat("latitude"), result.getFloat("longitude")));
			waypoint.setAddress(result.getString("address"));
			waypoints.put(result.getInt("sequence"), waypoint);
		}
		result.close();
		preparedStatement.close();
		return  waypoints;
	}

	private Map<Integer, GeoObject> getWaypointsCache(long orderId, Connection connection) throws SQLException, DaoException
	{
		Map<Integer, GeoObject> waypoints = new HashMap<Integer, GeoObject>();
		String SELECT_WAYPOINTS_SQL = "SELECT object_id, " +
			" 	order_id, " +
			" 	sequence, " +
			" 	latitude, " +
			" 	longitude, " +
			" 	address " +
			" FROM order_waypoint " +
			" WHERE order_id = ? " +
			" ORDER BY sequence ASC ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_WAYPOINTS_SQL);
		preparedStatement.setLong(1, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			GeoObject waypoint = geoObject.getItem(result.getInt("object_id"));
			if (waypoint != null) {
				waypoints.put(result.getInt("sequence"), waypoint);
			}
		}
		if (waypoints.isEmpty()){
			waypoints = getOnlyWaypoints(orderId, connection);
			//waypoints = getWaypoints(orderId, connection);
		}
		result.close();
		preparedStatement.close();
		return  waypoints;
	}

	private Map<Integer, String> getFeatures(long orderId, Connection connection) throws SQLException, DaoException
	{
		Map<Integer, String> features = new HashMap<>();
		String SELECT_FEATURES_SQL = " SELECT feature_id, order_id, value " +
			"  FROM order_feature " +
			"  WHERE order_id = ? ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FEATURES_SQL);
		preparedStatement.setLong(1, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			features.put(result.getInt("feature_id"), result.getString("value"));
		}
		result.close();
		preparedStatement.close();
		return features;
	}

	private List<OrderDistribution> getOrderDistributions(long orderId, Connection connection) throws SQLException, DaoException
	{
		List<OrderDistribution> orderDistributions = new ArrayList<>();
		String SELECT_FEATURES_SQL = "SELECT DISTINCT driver_id, state_id, date_create " +
			"  FROM order_distribution " +
			"  WHERE order_id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FEATURES_SQL);
		preparedStatement.setLong(1, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			OrderDistribution orderDistribution = new OrderDistribution();
			orderDistribution.setDriverId(result.getLong("driver_id"));
			orderDistribution.setDistributionState(distributionStateDictionary.getItem(result.getInt("state_id")));
			orderDistribution.setCreatedDate(result.getTimestamp("date_create"));
			orderDistributions.add(orderDistribution);
		}
		result.close();
		preparedStatement.close();
		return orderDistributions;
	}

	private Map<Integer, Order> getParents(long orderId, long parentId, Connection connection) throws SQLException
	{
		Map<Integer, Order> parents = new HashMap<>();
		String SELECT_PARENTS_SQL = "SELECT order_id " +
			" FROM \"order\" " +
			" WHERE parent_id = ? " +
			" 	AND order_id < ? " +
			" ORDER BY order_id ASC ";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PARENTS_SQL);
		preparedStatement.setLong(1, parentId);
		preparedStatement.setLong(2, orderId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			parents.put(result.getRow(), new Order(result.getLong("order_id")));
		}
		result.close();
		preparedStatement.close();
		return parents;
	}

	@Override
	public void insertOrderFeatures(long orderId, Map<Integer, String> features) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_WAYPOINT_SQL = "INSERT INTO order_feature (feature_id, order_id) " +
				" VALUES( ?, ?) ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_WAYPOINT_SQL);
			for (Map.Entry<Integer, String> entry: features.entrySet()) {
				preparedStatement.setInt(1, entry.getKey());
				preparedStatement.setLong(2, orderId);
				preparedStatement.setString(3, entry.getValue());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
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
	public void insertOrderWaypoints(long orderId, Map<Integer, GeoObject> waypoints) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_WAYPOINTS_SQL =
				"INSERT INTO order_waypoint (object_id, order_id, sequence, latitude, longitude, address) " +
					" VALUES (?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_WAYPOINTS_SQL);
			for (Map.Entry<Integer, GeoObject> entry : waypoints.entrySet()) {
				if (entry.getValue().getId() == 0){
					preparedStatement.setInt(1, entry.getValue().getId());
				}else {
					preparedStatement.setNull(1, Types.INTEGER);
				}
				preparedStatement.setLong(2, orderId);
				preparedStatement.setInt(3, entry.getKey());
				preparedStatement.setFloat(4, entry.getValue().getPosition().getLatitude());
				preparedStatement.setFloat(5, entry.getValue().getPosition().getLongitude());
				preparedStatement.setString(6, entry.getValue().getAddress());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
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
	public void insertOrderDistribution(long orderId, OrderDistribution orderDistribution) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_CHANGE_SQL = "INSERT INTO order_distribution(order_id, driver_id, state_id, date_create) " +
				" VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_CHANGE_SQL);
			preparedStatement.setLong(1, orderId);
			preparedStatement.setLong(2, orderDistribution.getDriverId());
			preparedStatement.setInt(3, orderDistribution.getDistributionState().getId());
			preparedStatement.setTimestamp(4, new java.sql.Timestamp(orderDistribution.getCreatedDate().getTime()));
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
