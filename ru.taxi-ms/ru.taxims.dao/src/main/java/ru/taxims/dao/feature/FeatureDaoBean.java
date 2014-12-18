package ru.taxims.dao.feature;

import org.apache.log4j.Logger;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.FeatureDao;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 13.11.14.
 */
@Stateless
public class FeatureDaoBean implements FeatureDao
{
	private Logger logger = Logger.getLogger(FeatureDaoBean.class);
	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;

	@Override
	public void persist(long entityId, Map<Integer, String> features, int roleTypeId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_FEATURE_SQL =
				"INSERT INTO entity_feature(entity_id, feature_id, value, role_type_id) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			for (Map.Entry<Integer, String> entry : features.entrySet()) {
				preparedStatement.setLong(1, entityId);
				preparedStatement.setInt(2, entry.getKey());
				preparedStatement.setString(3, entry.getValue());
				preparedStatement.setInt(4, roleTypeId);
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
	public Map<Integer, String> find(long entityId, int roleTypeId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Map<Integer, String> features = new HashMap<Integer, String>();
		try {
			connection = dataSource.getConnection();
			String SELECT_FEATURES_SQL = "SELECT feature_id, value " +
				" FROM entity_feature " +
				" WHERE entity_id = ? AND role_type_id = ?";
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
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public Map<Integer, String> find(long entityId, int roleTypeId, Connection connection) throws DaoException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Map<Integer, String> features = new HashMap<Integer, String>();
		try {
			String SELECT_FEATURES_SQL = "SELECT feature_id, value " +
				" FROM entity_feature " +
				" WHERE entity_id = ? AND role_type_id = ?";
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

	@Override
	public void enable(long entityId, Map<Integer, String> features, int roleTypeId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE entity_feature SET enabled = true " +
				" WHERE entity_id = ? AND feature_id = ? AND roleTypeId = ?");
			for (Map.Entry<Integer, String> entry : features.entrySet()) {
				preparedStatement.setLong(1, entityId);
				preparedStatement.setInt(2, entry.getKey());
				preparedStatement.setInt(3, roleTypeId);
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
	public void disable(long entityId, Map<Integer, String> features, int roleTypeId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE entity_feature SET enabled = false  " +
				" WHERE entity_id = ? AND feature_id = ? AND roleTypeId = ?");
			for (Map.Entry<Integer, String> entry : features.entrySet()) {
				preparedStatement.setLong(1, entityId);
				preparedStatement.setInt(2, entry.getKey());
				preparedStatement.setInt(3, roleTypeId);
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
}
