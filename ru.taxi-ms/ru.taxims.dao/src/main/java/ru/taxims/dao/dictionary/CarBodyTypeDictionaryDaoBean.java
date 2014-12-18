package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.CarBodyType;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;


/**
 * Created by Developer_DB on 28.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class CarBodyTypeDictionaryDaoBean extends DictionaryDaoBean<CarBodyType>
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
			preparedStatement = connection.prepareStatement("SELECT body_type_id, \"name\" FROM car_body_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int bodyTypeId = result.getInt("body_type_id");
				CarBodyType bodyType = new CarBodyType(bodyTypeId);
				bodyType.setName(result.getString("name"));
				logger.info("bodyType.getName() = " + bodyType.getName());
				items.put(bodyTypeId, bodyType);
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

	public int insertCarBodyType(CarBodyType carBodyType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_CAR_BODY_TYPE_SQL = "INSERT INTO car_body_type " +
				" (\"name\") " +
				" VALUES (?)  RETURNING body_type_id ";
			preparedStatement = connection.prepareStatement(INSERT_CAR_BODY_TYPE_SQL);
			preparedStatement.setString(1, carBodyType.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				carBodyType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting car body type failed, no generated key obtained.");
			}
			items.put(carBodyType.getId(), carBodyType);
			return carBodyType.getId();
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
