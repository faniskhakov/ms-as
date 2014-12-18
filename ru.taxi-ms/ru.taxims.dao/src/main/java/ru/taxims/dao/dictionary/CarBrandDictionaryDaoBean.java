package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.CarBrand;
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
public class CarBrandDictionaryDaoBean extends DictionaryDaoBean<CarBrand>
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
			preparedStatement = connection.prepareStatement("SELECT brand_id, \"name\" FROM car_brand ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int carBrandId = result.getInt("brand_id");
				CarBrand carBrand = new CarBrand(carBrandId);
				carBrand.setName(result.getString("name"));
				items.put(carBrandId, carBrand);
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

	public int insertCarBrand(CarBrand carBrand) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_BRAND_SQL = "INSERT INTO carBrand " +
				" (\"name\") " +
				" VALUES (?) RETURNING brand_id ";
			preparedStatement = connection.prepareStatement(INSERT_BRAND_SQL);
			preparedStatement.setString(1, carBrand.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				carBrand.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting carBrand failed, no generated key obtained.");
			}
			items.put(carBrand.getId(), carBrand);
			return  carBrand.getId();
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
