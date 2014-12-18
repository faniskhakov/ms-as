package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.CarBrand;
import ru.taxims.domain.datamodels.CarModel;
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
@DependsOn("CarBrandDictionaryDaoBean")
public class CarModelDictionaryDaoBean extends DictionaryDaoBean<CarModel>
{
	@EJB(beanName = "CarBrandDictionaryDaoBean")
	protected Dictionary<CarBrand> carBrandDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT model_id, \"name\", brand_id FROM car_model ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int carModelId = result.getInt("model_id");
				CarModel carModel = new CarModel(carModelId);
				carModel.setName(result.getString("name"));
				carModel.setBrand(carBrandDictionary.getItem(result.getInt("brand_id")));
				//logger.info("TVTV-TVT  carModel.getBrand().getName()  ---- " + carModel.getBrand().getName());
				items.put(carModelId, carModel);
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

	public int insertCarModel(CarModel carModel) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_MODEL_SQL = "INSERT INTO carModel " +
				" (\"name\", city_id) " +
				" VALUES (?, ?) RETURNING model_id ";
			preparedStatement = connection.prepareStatement(INSERT_MODEL_SQL);
			preparedStatement.setString(1, carModel.getName());
			preparedStatement.setInt(2, carModel.getBrand().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				carModel.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting carModel failed, no generated key obtained.");
			}
			items.put(carModel.getId(), carModel);
			return  carModel.getId();
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
