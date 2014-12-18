package ru.taxims.dao.driver;

import org.apache.log4j.Logger;
import ru.taxims.dao.main.AbstractDaoBean;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.DriverDao;
import ru.taxims.domain.interfaces.dao.UserDao;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by Developer_DB on 26.05.14.
 */
@Stateless
public class DriverDaoBean extends AbstractDaoBean<Driver>	implements DriverDao
{
	private Logger logger = Logger.getLogger(DriverDaoBean.class);
	@EJB(beanName = "SourceDictionaryDaoBean")
	protected Dictionary<Source> sourceDictionary;
	@EJB(beanName = "LanguageDictionaryDaoBean")
	protected Dictionary<Language> languageDictionary;
	@EJB(beanName = "AgentDictionaryDaoBean")
	protected Dictionary<Agent> agentDictionary;
	@EJB(beanName = "DriverStateDictionaryDaoBean")
	protected Dictionary<DriverState> stateDictionary;
	@EJB(beanName = "FeatureDictionaryDaoBean")
	protected Dictionary<Feature> featureDictionary;
	@EJB(beanName = "CityDictionaryDaoBean")
	protected Dictionary<City> cityDictionary;
	@EJB(beanName = "DistrictDictionaryDaoBean")
	protected Dictionary<District> districtDictionary;
	@EJB(beanName = "TariffDictionaryDaoBean")
	protected Dictionary<Tariff> tariffDictionary;
	@EJB(beanName = "CommunicationTypeDictionaryDaoBean")
	protected Dictionary<CommunicationType> communicationTypeDictionary;
	@EJB(beanName = "AccountTypeDictionaryDaoBean")
	protected Dictionary<AccountType> accountTypeDictionary;
	@EJB(beanName = "RoleTypeDictionaryDaoBean")
	protected Dictionary<RoleType> roleTypeDictionary;
	@EJB(beanName = "CarModelDictionaryDaoBean")
	protected Dictionary<CarModel> carModelDictionary;
	@EJB(beanName = "ColorDictionaryDaoBean")
	protected Dictionary<Color> colorDictionary;
	@EJB(beanName = "CarBodyTypeDictionaryDaoBean")
	protected Dictionary<CarBodyType> carBodyTypeDictionary;
	@EJB
	UserDao userDao;

	@Override
	public boolean verify(Driver driver)
	{
		return false;
	}

	@Override
	public long persist(Driver driver) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			userDao.persist(driver);
			connection = dataSource.getConnection();
			String INSERT_DRIVER_SQL = "INSERT INTO driver(\n " +
				" 	driver_license, driver_id, state_id, contract_id, agent_id,  radius_work,  \n " +
				" 	passport, registration_address, inhabitation_address, medical_examination, \n " +
				"	date_medical_examination, birthday, inn, pension_certificate, \n " +
				"	date_passport_issue, place_passport_issue, last_date_of_registration)\n " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
				" 	?, ?, ?, ?, ?, ?, ?, ?); " ;
			preparedStatement = connection.prepareStatement(INSERT_DRIVER_SQL);
			preparedStatement.setString(1, driver.getDriverLicense());
			preparedStatement.setLong(2, driver.getDriverId());
			preparedStatement.setInt(3, driver.getState().getId());
			preparedStatement.setLong(4, driver.getContract().getContractId());
			preparedStatement.setInt(5, driver.getAgent().getId());
			preparedStatement.setInt(6, driver.getRadiusWork());
			preparedStatement.setLong(7, driver.getPassport());
			preparedStatement.setString(8, driver.getRegistrationAddress());
			preparedStatement.setString(9, driver.getInhabitationAddress());
			preparedStatement.setString(10, driver.getMedicalExamination());
			preparedStatement.setDate(11,  new java.sql.Date(driver.getDateMadicalExamination().getTime()));
			preparedStatement.setDate(12,  new java.sql.Date(driver.getBirthday().getTime()));
			preparedStatement.setLong(13, driver.getInn());
			preparedStatement.setLong(14, driver.getPensionCertificate());
			preparedStatement.setDate(15,  new java.sql.Date(driver.getDatePassportIssue().getTime()));
			preparedStatement.setString(16, driver.getPlacePassportIssue());
			preparedStatement.setTimestamp(17, new Timestamp(new java.util.Date().getTime()));
			preparedStatement.executeUpdate();

			preparedStatement = connection.prepareStatement( "INSERT INTO car(\n " +
				"	user_id, color_id, model_id, \"number\", insurance_id, \n " +
				"	body_type_id, width, height, length, year)\n " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING car_id ");
			preparedStatement.setLong(1, driver.getDriverId());
			preparedStatement.setInt(2, driver.getCar().getColor().getId());
			preparedStatement.setInt(3, driver.getCar().getModel().getId());
			preparedStatement.setString(4, driver.getCar().getNumber());
			preparedStatement.setInt(5, driver.getCar().getInsurance().getInsuranceId());
			preparedStatement.setInt(6, driver.getCar().getBodyType().getId());
			preparedStatement.setInt(7, driver.getCar().getWidth());
			preparedStatement.setInt(8, driver.getCar().getHeight());
			preparedStatement.setInt(9, driver.getCar().getLength());
			preparedStatement.setInt(10, driver.getCar().getYear());
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				driver.getCar().setCarId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Creating car failed, no generated key obtained.");
			}

			String INSERT_FEATURE_SQL =
				"INSERT INTO entity_feature(feature_id, entity_id, value, role_type_id) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			for (Map.Entry<Integer, String> entry : driver.getCar().getFeatures().entrySet()) {
				preparedStatement.setInt(1, entry.getKey());
				preparedStatement.setLong(2, driver.getCar().getCarId());
				preparedStatement.setString(3, entry.getValue());
				preparedStatement.setInt(4, 4);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();

			String INSERT_ROLE_SQL = "INSERT INTO role( " +
				"	user_id, role_type_id, blockage, account_id, communication_id, rating)\n" +
				" VALUES (?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_ROLE_SQL);
			preparedStatement.setLong(1, driver.getDriverId());
			preparedStatement.setInt(2, 2);
			preparedStatement.setBoolean(3, false);
			preparedStatement.setLong(4, driver.getAccount().getAccountId());
			preparedStatement.setLong(5, driver.getCommunication().getCommunicationId());
			preparedStatement.setInt(6, driver.getRating());
			preparedStatement.executeUpdate();
			return driver.getDriverId();
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
	public void merge(Driver driver) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String UPDATE_DRIVER_SQL = " UPDATE driver  " +
				" 	SET driver_license = ?,  contract_id = ?, agent_id = ?,  " +
				" 	passport = ?, registration_address = ?, inhabitation_address = ?, medical_examination = ?, \n " +
				"	date_medical_examination = ?, birthday = ?, inn = ?, pension_certificate = ?, \n " +
				"	date_passport_issue = ?, place_passport_issue  = ? " +
				" WHERE driver_id = ? " ;
			preparedStatement = connection.prepareStatement(UPDATE_DRIVER_SQL);
			preparedStatement.setString(1, driver.getDriverLicense());
			preparedStatement.setLong(2, driver.getContract().getContractId());
			preparedStatement.setInt(3, driver.getAgent().getId());
			preparedStatement.setLong(4, driver.getPassport());
			preparedStatement.setString(5, driver.getRegistrationAddress());
			preparedStatement.setString(6, driver.getInhabitationAddress());
			preparedStatement.setString(7, driver.getMedicalExamination());
			preparedStatement.setDate(8,  new java.sql.Date(driver.getDateMadicalExamination().getTime()));
			preparedStatement.setDate(9,  new java.sql.Date(driver.getBirthday().getTime()));
			preparedStatement.setLong(10, driver.getInn());
			preparedStatement.setLong(11, driver.getPensionCertificate());
			preparedStatement.setDate(12,  new java.sql.Date(driver.getDatePassportIssue().getTime()));
			preparedStatement.setString(13, driver.getPlacePassportIssue());
			preparedStatement.setLong(14, driver.getDriverId());
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
	public Driver find(long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Driver driver = new Driver(driverId);
		try {
			connection = dataSource.getConnection();
			String SELECT_DRIVER_SQL = "SELECT u.email, u.name, u.surname, u.login, u.password,  " +
				"	u.source_id, u.language_id, u.city_id, u.private_key, u.public_key, u.gender,  " +
				"	r.user_id, r.rating, r.communication_id, r.account_id, r.blockage, r.date_create,  " +
				"	c.number, c.type_id AS c_type_id, c.date_create AS c_date_create, c.blockage AS c_blockage ,  " +
				"	a.type_id AS a_type_id, a.amount, a.date_create AS a_date_create, a.blockage AS a_blockage, " +
				"	d.driver_id, d.driver_license,  d.state_id, d.contract_id, " +
				"	d.agent_id, d.priority, d.radius_work, d.car_id, d.passport,  " +
				"	d.registration_address, d.inhabitation_address, d.medical_examination,  " +
				"	d.date_madical_examination, d.birthday, d.inn, d.pension_certificate,  " +
				"	d.date_passport_issue, d.place_passport_issue, d.last_date_of_registration, d.driver_tariff_id, " +
				"  	ca.color_id, ca.model_id, ca.number AS car_number, ca.insurance_id, " +
				" 	ca.blockage AS car_blockage, ca.body_type_id, ca.width, ca.height, ca.length, ca.year " +
				" FROM driver d " +
				" JOIN \"user\" u ON (d.driver_id = u.user_id) " +
				" JOIN role r USING(user_id) " +
				" JOIN communication c USING(communication_id) " +
				" JOIN account a USING(account_id) " +
				" JOIN car ca USING(car_id) " +
				" WHERE d.driver_id = ? AND r.role_type_id = 2";
			preparedStatement = connection.prepareStatement(SELECT_DRIVER_SQL);
			preparedStatement.setLong(1, driverId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				driver.setEmail(result.getString("email"));
				driver.setName(result.getString("name"));
				driver.setSurname(result.getString("surname"));
				driver.setLogin(result.getString("login"));
				driver.setPassword(result.getString("password"));
				driver.setSource(sourceDictionary.getItem(result.getInt("source_id")));
				driver.setLanguage(languageDictionary.getItem(result.getInt("language_id")));
				driver.setCity(cityDictionary.getItem(result.getInt("city_id")));
				driver.setPrivateKey(result.getString("private_key"));
				driver.setPublicKey(result.getString("public_key"));
				driver.setGender(result.getString("gender").charAt(0));
				driver.setBlockage(result.getBoolean("blockage"));
				driver.setRating(result.getInt("rating"));
				driver.setDateCreate(result.getTimestamp("date_create"));
					Communication communication = new Communication(result.getLong("communication_id"));
					communication.setUserId(result.getLong("user_id"));
					communication.setNumber(result.getString("number"));
					communication.setDateCreate(result.getTimestamp("c_date_create"));
					communication.setType(communicationTypeDictionary.getItem(result.getInt("c_type_id")));
					communication.setBlockage(result.getBoolean("c_blockage"));
				driver.setCommunication(communication);
					Account account = new Account(result.getLong("account_id"));
					account.setType(accountTypeDictionary.getItem(result.getInt("a_type_id")));
					account.setUserId(result.getLong("user_id"));
					account.setAmount(result.getFloat("amount"));
					account.setDateCreate(result.getTimestamp("a_date_create"));
					account.setBlockage(result.getBoolean("a_blockage"));
				driver.setAccount(account);
				driver.setFeatures(findFeatures(driver.getUserId(), 2, connection));
				driver.setRoles(getRoles(driver.getUserId(), connection));
				driver.setDriverLicense(result.getString("driver_license"));
				driver.setState(stateDictionary.getItem(result.getInt("state_id")));
				driver.setContract(new DriverContract(result.getInt("contract_id")));
				driver.setAgent(agentDictionary.getItem(result.getInt("agent_id")));
				driver.setPriority(result.getInt("priority"));
				driver.setRadiusWork(result.getInt("radius_work"));
				driver.setPassport(result.getLong("passport"));
				driver.setRegistrationAddress(result.getString("registration_address"));
				driver.setInhabitationAddress(result.getString("inhabitation_address"));
				driver.setDateMadicalExamination(result.getDate("date_madical_examination"));
				driver.setBirthday(result.getDate("birthday"));
				driver.setInn(result.getLong("inn"));
				driver.setPensionCertificate(result.getLong("pension_certificate"));
				driver.setDatePassportIssue(result.getDate("date_passport_issue"));
				driver.setPlacePassportIssue(result.getString("place_passport_issue"));
				driver.setLastDateOfRegistration(result.getDate("last_date_of_registration"));
				driver.setDriverTariff(new DriverTariff(result.getLong("driver_tariff_id")));
					Car car = new Car(result.getInt("car_id"));
					car.setUserId(result.getLong("user_id"));
					car.setColor(colorDictionary.getItem(result.getInt("color_id")));
					car.setModel(carModelDictionary.getItem(result.getInt("model_id")));
					car.setBodyType(carBodyTypeDictionary.getItem(result.getInt("body_type_id")));
					car.setNumber(result.getString("car_number"));
					car.setBlockage(result.getBoolean("car_blockage"));
					car.setWidth(result.getInt("width"));
					car.setHeight(result.getInt("height"));
					car.setLength(result.getInt("length"));
					car.setYear(result.getInt("year"));
					car.setFeatures(findFeatures(car.getCarId(), 4, connection));
				driver.setCar(car);
			} else {
				throw new DaoException("Selecting driver failed, no rows affected.");
			}
			return driver;
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
	public List<Driver> findDrivers(String sql) throws DaoException
	{
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		List<Driver> drivers = new ArrayList<>();
		try {
			connection = dataSource.getConnection();
			String SELECT_DRIVER_SQL = "SELECT u.email, u.name, u.surname, u.login, u.password,  " +
				"	u.source_id, u.language_id, u.city_id, u.private_key, u.public_key, u.gender,  " +
				"	r.user_id, r.rating, r.communication_id, r.account_id, r.blockage, r.date_create,  " +
				"	c.number, c.type_id AS c_type_id, c.date_create AS c_date_create, c.blockage AS c_blockage ,  " +
				"	a.type_id AS a_type_id, a.amount, a.date_create AS a_date_create, a.blockage AS a_blockage, " +
				"	d.driver_id, d.driver_license,  d.state_id, d.contract_id, " +
				"	d.agent_id, d.priority, d.radius_work, d.car_id, d.passport,  " +
				"	d.registration_address, d.inhabitation_address, d.medical_examination,  " +
				"	d.date_madical_examination, d.birthday, d.inn, d.pension_certificate,  " +
				"	d.date_passport_issue, d.place_passport_issue, d.last_date_of_registration, d.driver_tariff_id, " +
				"  	ca.color_id, ca.model_id, ca.number AS car_number, ca.insurance_id, " +
				" 	ca.blockage AS car_blockage, ca.body_type_id, ca.width, ca.height, ca.length, ca.year " +
				" FROM driver d " +
				" JOIN \"user\" u ON (d.driver_id = u.user_id) " +
				" JOIN role r USING(user_id) " +
				" JOIN communication c USING(communication_id) " +
				" JOIN account a USING(account_id) " +
				" JOIN car ca USING(car_id) " + sql;
			statement = connection.createStatement();
			result = statement.executeQuery(SELECT_DRIVER_SQL);
			while (result.next()) {
				Driver driver = new Driver(result.getLong("driver_id"));
				driver.setEmail(result.getString("email"));
				driver.setName(result.getString("name"));
				driver.setSurname(result.getString("surname"));
				driver.setLogin(result.getString("login"));
				driver.setPassword(result.getString("password"));
				driver.setSource(sourceDictionary.getItem(result.getInt("source_id")));
				driver.setLanguage(languageDictionary.getItem(result.getInt("language_id")));
				driver.setCity(cityDictionary.getItem(result.getInt("city_id")));
				driver.setPrivateKey(result.getString("private_key"));
				driver.setPublicKey(result.getString("public_key"));
				driver.setGender(result.getString("gender").charAt(0));
				driver.setBlockage(result.getBoolean("blockage"));
				driver.setRating(result.getInt("rating"));
				driver.setDateCreate(result.getTimestamp("date_create"));
					Communication communication = new Communication(result.getLong("communication_id"));
					communication.setUserId(result.getLong("user_id"));
					communication.setNumber(result.getString("number"));
					communication.setDateCreate(result.getTimestamp("c_date_create"));
					communication.setType(communicationTypeDictionary.getItem(result.getInt("c_type_id")));
					communication.setBlockage(result.getBoolean("c_blockage"));
				driver.setCommunication(communication);
					Account account = new Account(result.getLong("account_id"));
					account.setType(accountTypeDictionary.getItem(result.getInt("a_type_id")));
					account.setUserId(result.getLong("user_id"));
					account.setAmount(result.getFloat("amount"));
					account.setDateCreate(result.getTimestamp("a_date_create"));
					account.setBlockage(result.getBoolean("a_blockage"));
				driver.setAccount(account);
				driver.setFeatures(findFeatures(driver.getUserId(), 2, connection));
				driver.setRoles(getRoles(driver.getUserId(), connection));
				driver.setDriverLicense(result.getString("driver_license"));
				driver.setState(stateDictionary.getItem(result.getInt("state_id")));
				driver.setContract(new DriverContract(result.getInt("contract_id")));
				driver.setAgent(agentDictionary.getItem(result.getInt("agent_id")));
				driver.setPriority(result.getInt("priority"));
				driver.setRadiusWork(result.getInt("radius_work"));
				driver.setPassport(result.getLong("passport"));
				driver.setRegistrationAddress(result.getString("registration_address"));
				driver.setInhabitationAddress(result.getString("inhabitation_address"));
				driver.setDateMadicalExamination(result.getDate("date_madical_examination"));
				driver.setBirthday(result.getDate("birthday"));
				driver.setInn(result.getLong("inn"));
				driver.setPensionCertificate(result.getLong("pension_certificate"));
				driver.setDatePassportIssue(result.getDate("date_passport_issue"));
				driver.setPlacePassportIssue(result.getString("place_passport_issue"));
				driver.setLastDateOfRegistration(result.getDate("last_date_of_registration"));
				driver.setDriverTariff(new DriverTariff(result.getLong("driver_tariff_id")));
					Car car = new Car(result.getInt("car_id"));
					car.setUserId(result.getLong("user_id"));
					car.setColor(colorDictionary.getItem(result.getInt("color_id")));
					car.setModel(carModelDictionary.getItem(result.getInt("model_id")));
					car.setBodyType(carBodyTypeDictionary.getItem(result.getInt("body_type_id")));
					car.setNumber(result.getString("car_number"));
					car.setBlockage(result.getBoolean("car_blockage"));
					car.setWidth(result.getInt("width"));
					car.setHeight(result.getInt("height"));
					car.setLength(result.getInt("length"));
					car.setYear(result.getInt("year"));
					car.setFeatures(findFeatures(car.getCarId(), 4, connection));
				driver.setCar(car);
				drivers.add(driver);
			}
			return drivers;
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
	public void changeDefaultCar(long driverId, int carId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE driver " +
				" SET car_id = ? " +
				" WHERE driver_id = ?");
			preparedStatement.setLong(1, carId);
			preparedStatement.setLong(2, driverId);
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
	public void changeDefaultCommunication(long driverId, long communicationId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE role " +
				" SET communication_id = ? " +
				" WHERE user_id = ? AND role_type_id = 2");
			preparedStatement.setLong(1, communicationId);
			preparedStatement.setLong(2, driverId);
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
	public void changeDefaultDriverTariff(long driverId, long driverTariffId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE driver " +
				" SET driver_tariff_id = ? " +
				" WHERE user_id = ? ");
			preparedStatement.setLong(1, driverTariffId);
			preparedStatement.setLong(2, driverId);
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
	public void changeDefaultAccount(long driverId, long accountId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("UPDATE role " +
				" SET account_id = ? " +
				" WHERE user_id = ? AND role_type_id = 2");
			preparedStatement.setLong(1, accountId);
			preparedStatement.setLong(2, driverId);
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
	public void disable(long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE role " +
				" SET blockage = true " +
				" WHERE user_id = ? AND role_type_id = 2");
			preparedStatement.setLong(1, driverId);
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
	public void enable(long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE role " +
				" SET blockage = false " +
				" WHERE user_id = ? AND role_type_id = 2");
			preparedStatement.setLong(1, driverId);
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
	public void changeRadius(long driverId, int radius) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE driver " +
				" SET radius_work = ? " +
				" WHERE driver_id = ? " );
			preparedStatement.setInt(1, radius);
			preparedStatement.setLong(2, driverId);
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
	public void changeAgent(long driverId, int agentId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE driver " +
				" SET agent_id = ? " +
				" WHERE driver_id = ? " );
			preparedStatement.setInt(1, agentId);
			preparedStatement.setLong(2, driverId);
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
	public void changeRating(long driverId, int rating) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE role " +
				" SET rating = rating + ? " +
				" WHERE user_id = ? AND role_type_id = 2");
			preparedStatement.setInt(1, rating);
			preparedStatement.setLong(2, driverId);
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
	public void changePriority(long driverId, int priority) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE driver " +
				" SET priority = priority + ? " +
				" WHERE driver_id = ? " );
			preparedStatement.setInt(1, priority);
			preparedStatement.setLong(2, driverId);
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
	public void changeLastDateOfRegistration(long driverId, java.util.Date lastDateOfRegistration) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "UPDATE driver " +
				" SET last_date_of_registration = ? " +
				" WHERE driver_id = ? " );
			preparedStatement.setDate(1, new Date(lastDateOfRegistration.getTime()));
			preparedStatement.setLong(2, driverId);
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
	public void changePosition(long driverId, Point point) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement( "INSERT INTO driver_position " +
				"	(driver_id, latitude, longitude) " +
				"    VALUES (?, ?, ?) " );
			preparedStatement.setLong(1, driverId);
			preparedStatement.setFloat(2, point.getLatitude());
			preparedStatement.setFloat(3, point.getLongitude());
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
	public void changeDriverState(Driver driver, int newState, String comment) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_DRIVER_CHANGE_SQL = "INSERT INTO driver_state_change " +
				" (driver, order_id, state_id, old_state_id, comment) " +
				" VALUES (?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(INSERT_DRIVER_CHANGE_SQL);
			preparedStatement.setLong(1, driver.getDriverId());
			preparedStatement.setLong(2, driver.getOrder().getOrderId());
			preparedStatement.setLong(3, newState);
			preparedStatement.setInt(4, driver.getState().getId());
			preparedStatement.setString(5, comment);
			preparedStatement.executeUpdate();

			String UPDATE_DRIVER_SQL = "UPDATE driver " +
				" SET state_id = ? " +
				" WHERE driver_id = ? ";
			preparedStatement = connection.prepareStatement(UPDATE_DRIVER_SQL);
			preparedStatement.setInt(1, newState);
			preparedStatement.setLong(2, driver.getDriverId());
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
	public void removeDriverDistricts(long driverId, List<District> districts) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("DELETE FROM district_work " +
				" WHERE driver_id = ? AND district_id = ? ");
			for (District district : districts)
			{
				preparedStatement.setLong(1, driverId);
				preparedStatement.setInt(2, district.getId());
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
	public void insertDriverDistricts(long driverId, List<District> districts) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("INSERT INTO district_work " +
				"(driver_id, district_id) VALUES (?, ?) ");
			for (District district : districts)
			{
				preparedStatement.setLong(1, driverId);
				preparedStatement.setInt(2, district.getId());
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
	public List<District> findDriverDistricts(long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		List<District> districts = new ArrayList<District>();
		try {
			connection = dataSource.getConnection();
			String SELECT_FEATURES_SQL = "SELECT district_id " +
				" FROM driver_district " +
				" WHERE driver_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_FEATURES_SQL);
			preparedStatement.setLong(1, driverId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				districts.add(districtDictionary.getItem(result.getInt("district_id")));
			}
			return districts;
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
	public long insertDriverTariff(long driverId, Tariff tariff, java.util.Date startDate) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		long driverTariffId;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("INSERT INTO driver_tariff " +
				" (driver_id, tariff_id, start_date, end_date) VALUES (?, ?, ?, ?) RETURNING driver_tariff_id");
			preparedStatement.setLong(1, driverId);
			preparedStatement.setInt(2, tariff.getId());
			preparedStatement.setTimestamp(3, new Timestamp(startDate.getTime()));
			preparedStatement.setTimestamp(4, new Timestamp(startDate.getTime() + tariff.getDuration()));
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				driverTariffId = generatedKeys.getLong(1);
			} else {
				throw new DaoException("Creating tariff for driver failed, no generated key obtained.");
			}
			return  driverTariffId;
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
	public void removeDriverTariff(long driverTariffId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("DELETE FROM driver_tariff " +
				" WHERE driver_tariff_id = ? ");
			preparedStatement.setLong(1, driverTariffId);
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
	public DriverTariff findDriverTariff(long driverTariffId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		DriverTariff driverTariff;
		try {
			connection = dataSource.getConnection();
			String SELECT_DRIVER_TARIFF_SQL = "SELECT driver_id, tariff_id, start_date, end_date " +
				" FROM driver_tariff " +
				" WHERE driver_tariff_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_DRIVER_TARIFF_SQL);
			preparedStatement.setLong(1, driverTariffId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				driverTariff = new DriverTariff(result.getLong("driver_tariff_id"));
				driverTariff.setTariff(tariffDictionary.getItem(result.getInt("tariff_id")));
				driverTariff.setStartDate(new Timestamp(result.getTimestamp("start_date").getTime()));
				driverTariff.setEndDate(new Timestamp(result.getTimestamp("end_date").getTime()));
			} else {
				throw new DaoException("Selecting tariff for driver failed, no rows affected.");
			}
			return driverTariff;
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
	public List<DriverTariff> findDriverTariffs(long driverId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		List<DriverTariff> driverTariffs = new ArrayList<>();
		try {
			connection = dataSource.getConnection();
			String SELECT_DRIVER_TARIFF_SQL = "SELECT driver_id, tariff_id, start_date, end_date " +
				" FROM driver_tariff " +
				" WHERE driver_id = ? AND end_date > now()";
			preparedStatement = connection.prepareStatement(SELECT_DRIVER_TARIFF_SQL);
			preparedStatement.setLong(1, driverId);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				DriverTariff driverTariff = new DriverTariff(result.getLong("driver_tariff_id"));
				driverTariff.setTariff(tariffDictionary.getItem(result.getInt("tariff_id")));
				driverTariff.setStartDate(new Timestamp(result.getTimestamp("start_date").getTime()));
				driverTariff.setEndDate(new Timestamp(result.getTimestamp("end_date").getTime()));
				driverTariffs.add(driverTariff);
			}
			return driverTariffs;
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
}
