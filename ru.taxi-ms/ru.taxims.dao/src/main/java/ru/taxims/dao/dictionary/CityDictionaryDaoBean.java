package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.City;
import ru.taxims.domain.datamodels.Country;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("CountryDictionaryDaoBean")
public class CityDictionaryDaoBean extends DictionaryDaoBean<City>
{
	@EJB(beanName = "CountryDictionaryDaoBean")
	protected Dictionary<Country> countryDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT city_id, \"name\", country_id FROM city ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int cityId = result.getInt("city_id");
				City city = new City(cityId);
				city.setName(result.getString("name"));
				city.setCountry(countryDictionary.getItem(result.getInt("country_id")));
				items.put(cityId, city);
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

	public int insertCity(City city) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_CITY_SQL = "INSERT INTO city " +
				" (\"name\", country_id) " +
				" VALUES (?, ?) RETURNING city_id ";
			preparedStatement = connection.prepareStatement(INSERT_CITY_SQL);
			preparedStatement.setString(1, city.getName());
			preparedStatement.setInt(2, city.getCountry().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				city.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting city failed, no generated key obtained.");
			}
			items.put(city.getId(), city);
			return  city.getId();
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
