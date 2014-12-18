package ru.taxims.dao.car;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.Car;
import ru.taxims.domain.datamodels.CarBodyType;
import ru.taxims.domain.datamodels.CarModel;
import ru.taxims.domain.datamodels.Color;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.CarDao;
import ru.taxims.domain.interfaces.dao.FeatureDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer_DB on 27.05.14.
 */
@Stateless
public class CarDaoBean extends AbstractDaoBean<Car> implements CarDao
{
	private Logger logger = Logger.getLogger(CarDaoBean.class);
	@EJB(beanName = "CarModelDictionaryDaoBean")
	protected Dictionary<CarModel> carModelDictionary;
	@EJB(beanName = "ColorDictionaryDaoBean")
	protected Dictionary<Color> colorDictionary;
	@EJB(beanName = "CarBodyTypeDictionaryDaoBean")
	protected Dictionary<CarBodyType> carBodyTypeDictionary;
	@EJB
	protected FeatureDao featureDao;

	@Override
	public boolean verify(Car car)
	{
		if ((car == null) ||
			(car.getCarId() == 0) ||
			(car.getUserId() == 0) ||
			(car.getNumber() == null) ||
			(car.getBodyType() != null) ||
			(car.getColor() == null) ||
			(car.getModel() == null) ||
			(car.getInsurance() == null) ||
			(car.getHeight() == 0) ||
			(car.getLength() == 0) ||
			(car.getWidth() == 0) ||
			(car.getYear() == 0)
			)
		{
			return false;
		} else {
			return true;
		}
	}

	@Override
	public long persist(Car car) throws DaoException
	{
		if (! verify(car)) {
			throw new DaoException("Verify exception. Car is not full");
		}
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "INSERT INTO car(\n " +
				"	user_id, color_id, model_id, \"number\", insurance_id, \n " +
				"	body_type_id, width, height, length, year)\n " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING car_id ");
			preparedStatement.setLong(1, car.getUserId());
			preparedStatement.setInt(2, car.getColor().getId());
			preparedStatement.setInt(3, car.getModel().getId());
			preparedStatement.setString(4, car.getNumber());
			preparedStatement.setInt(5, car.getInsurance().getInsuranceId());
			preparedStatement.setInt(6, car.getBodyType().getId());
			preparedStatement.setInt(7, car.getWidth());
			preparedStatement.setInt(8, car.getHeight());
			preparedStatement.setInt(9, car.getLength());
			preparedStatement.setInt(10, car.getYear());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				car.setCarId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Creating car failed, no generated key obtained.");
			}
			featureDao.persist(car.getCarId(), car.getFeatures(), 4);
			return  car.getCarId();
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
	public Car find(long carId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Car car;
		try {
			connection = dataSource.getConnection();
			String SELECT_CAR_SQL = " SELECT car_id, user_id, color_id, model_id, \"number\", insurance_id, " +
				" 	blockage, body_type_id, width, height, length, year " +
				" FROM car " +
				" WHERE car_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_CAR_SQL);
			preparedStatement.setLong(1, carId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				car = new Car(result.getInt("car_id"));
				car.setUserId(result.getLong("user_id"));
				car.setColor(colorDictionary.getItem(result.getInt("color_id")));
				car.setModel(carModelDictionary.getItem(result.getInt("model_id")));
				car.setBodyType(carBodyTypeDictionary.getItem(result.getInt("body_type_id")));
				car.setNumber(result.getString("number"));
				car.setBlockage(result.getBoolean("blockage"));
				car.setWidth(result.getInt("width"));
				car.setHeight(result.getInt("height"));
				car.setLength(result.getInt("length"));
				car.setYear(result.getInt("year"));
				car.setFeatures(featureDao.find(car.getCarId(), 4, connection));
			} else {
				throw new SQLException("Selecting car failed, no rows affected.");
			}
			return car;
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
	public Car findCar(String number) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Car car;
		try {
			connection = dataSource.getConnection();
			String SELECT_CAR_SQL = " SELECT car_id, user_id, color_id, model_id, \"number\", insurance_id, " +
				" 	blockage, body_type_id, width, height, length, year " +
				" FROM car " +
				" WHERE \"number\" ILIKE ? ";
			preparedStatement = connection.prepareStatement(SELECT_CAR_SQL);
			preparedStatement.setString(1, number);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				car = new Car(result.getInt("car_id"));
				car.setUserId(result.getLong("user_id"));
				car.setColor(colorDictionary.getItem(result.getInt("color_id")));
				car.setModel(carModelDictionary.getItem(result.getInt("model_id")));
				car.setBodyType(carBodyTypeDictionary.getItem(result.getInt("body_type_id")));
				car.setNumber(result.getString("number"));
				car.setBlockage(result.getBoolean("blockage"));
				car.setWidth(result.getInt("width"));
				car.setHeight(result.getInt("height"));
				car.setLength(result.getInt("length"));
				car.setYear(result.getInt("year"));
				car.setFeatures(featureDao.find(car.getCarId(), 4, connection));
			} else {
				throw new SQLException("Selecting car failed, no rows affected.");
			}
			return car;
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
	public void merge(Car car) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE car " +
				"	SET user_id = ?, color_id = ?, \"number\" = ?, insurance_id = ? \n " +
				" WHERE car_id = ? " );
			preparedStatement.setLong(1, car.getUserId());
			preparedStatement.setInt(2, car.getColor().getId());
			preparedStatement.setString(3, car.getNumber());
			preparedStatement.setInt(4, car.getInsurance().getInsuranceId());
			preparedStatement.setInt(5, car.getCarId());
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
	public List<Car> findCars(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		Car car;
		List<Car> cars = new ArrayList<Car>();
		try {
			connection = dataSource.getConnection();
			String SELECT_CARS_SQL = " SELECT car_id, user_id, color_id, model_id, \"number\", insurance_id, " +
				" 	blockage, body_type_id, width, height, length, year " +
				" FROM car " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_CARS_SQL);
			while (result.next()) {
				car = new Car(result.getInt("car_id"));
				car.setUserId(result.getLong("user_id"));
				car.setColor(colorDictionary.getItem(result.getInt("color_id")));
				car.setModel(carModelDictionary.getItem(result.getInt("model_id")));
				car.setBodyType(carBodyTypeDictionary.getItem(result.getInt("body_type_id")));
				car.setNumber(result.getString("number"));
				car.setBlockage(result.getBoolean("blockage"));
				car.setWidth(result.getInt("width"));
				car.setHeight(result.getInt("height"));
				car.setLength(result.getInt("length"));
				car.setYear(result.getInt("year"));
				car.setFeatures(featureDao.find(car.getCarId(), 4, connection));
				cars.add(car);
			}
			return cars;
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

	@Override
	public void disable(int carId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE car " +
				"	SET blockage = true " +
				" WHERE car_id = ? " );
			preparedStatement.setInt(1, carId);
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
	public void enable(int carId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE car " +
				"	SET blockage = false " +
				" WHERE car_id = ? " );
			preparedStatement.setInt(1, carId);
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
