package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.City;
import ru.taxims.domain.datamodels.District;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("CityDictionaryDaoBean")
public class DistrictDictionaryDaoBean extends DictionaryDaoBean<District>
{
	@EJB(beanName = "CityDictionaryDaoBean")
	private Dictionary<City> cityDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT district_id, \"name\", city_id " +
				" FROM district ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int districtId = result.getInt("model_id");
				District district = new District(districtId);
				district.setName(result.getString("name"));
				district.setCity(cityDictionary.getItem(result.getInt("city_id")));
				district.setPoints(getPoints(districtId, connection));
				items.put(districtId, district);
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

	private Map<Integer, Point> getPoints(int districtId, Connection connection) throws SQLException
	{
		Map<Integer, Point> points = new HashMap<Integer, Point>();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT " +
			"	district_id, latitude, longitude, sequence " +
			" FROM  district_point " +
			" WHERE district_id = ?" +
			" ORDER BY sequence ASC ");
		preparedStatement.setInt(1, districtId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			Point point = new Point(result.getFloat("latitude"), result.getFloat("longitude"));
			points.put(result.getInt("sequence"), point);
		}
		result.close();
		preparedStatement.close();
		return points;
	}

	public int insertDistrict(District district) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_DISTRICT_SQL = "INSERT INTO district " +
				" (\"name\", city_id) " +
				" VALUES (?, ?) RETURNING district_id ";
			preparedStatement = connection.prepareStatement(INSERT_DISTRICT_SQL);
			preparedStatement.setString(1, district.getName());
			preparedStatement.setInt(1, district.getCity().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				district.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting order state failed, no generated key obtained.");
			}

			String INSERT_POINTS_SQL = "INSERT INTO district_point(\n " +
				"	district_id, latitude, longitude, sequence)\n " +
				" VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_POINTS_SQL);
			for (Point point : district.getPoints().values()) {
				preparedStatement.setInt(1, district.getId());
				preparedStatement.setFloat(2, point.getLatitude());
				preparedStatement.setFloat(3, point.getLongitude());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			items.put(district.getId(), district);
			return  district.getId();
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
