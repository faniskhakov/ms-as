package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.datamodels.OrderTariff;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 25.09.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("FeatureDictionaryDaoBean")
public class OrderTariffDictionaryDaoBean extends DictionaryDaoBean<OrderTariff>
{
	@EJB(beanName = "FeatureDictionaryDaoBean")
	protected Dictionary<Feature> featureDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(" SELECT " +
				"    order_tariff_id, " +
				"    name, " +
				"	 blockage  " +
				"  FROM order_tariff ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				OrderTariff orderTariff = new OrderTariff(result.getInt("order_tariff_id"));
				orderTariff.setName(result.getString("name"));
				orderTariff.setBlockage(result.getBoolean("blockage"));
				orderTariff.setFeatures(new HashMap<Integer, Feature>());
				items.put(orderTariff.getId(), orderTariff);
			}
			preparedStatement = connection.prepareStatement(" SELECT " +
				"	    order_tariff_id, " +
				"		feature_id " +
				"  FROM order_tariff_features ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				Feature feature = featureDictionary.getItem(result.getInt("feature_id"));
				items.get(result.getInt("order_tariff_id")).getFeatures().put(feature.getId(), feature);
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

	public int insertOrderTariff(OrderTariff orderTariff) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_ORDER_TARIFF_SQL = "INSERT INTO order_tariff(name, blockage) " +
				"    VALUES (?, ?) RETURNING order_tariff_id ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_TARIFF_SQL);
			preparedStatement.setString(1, orderTariff.getName());
			preparedStatement.setBoolean(2, orderTariff.isBlockage());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				orderTariff.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting order tariff failed, no generated key obtained.");
			}
			INSERT_ORDER_TARIFF_SQL = " INSERT INTO order_tariff_features(order_tariff_id, feature_id) " +
				"    VALUES (?, ?); ";
			preparedStatement = connection.prepareStatement(INSERT_ORDER_TARIFF_SQL);
			for (Map.Entry<Integer, Feature> entry: orderTariff.getFeatures().entrySet()) {
				preparedStatement.setInt(1, orderTariff.getId());
				preparedStatement.setFloat(2, entry.getValue().getId());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			items.put(orderTariff.getId(), orderTariff);
			return  orderTariff.getId();
		} catch (SQLException e) {
			logger.error(e + " Transaction was rolled back");
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
