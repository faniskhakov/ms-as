package ru.taxims.core.service;

import org.apache.log4j.Logger;
import ru.taxims.core.cache.EntityCache;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.dao.*;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 25.08.14.
 */
@Stateless
public class DriverServiceBean implements DriverService
{
	Logger logger = Logger.getLogger(DriverServiceBean.class);

	@EJB
	DriverDao driverDao;
	@EJB
	CarDao carDao;
	@EJB
	AccountDao accountDao;
	@EJB
	CommunicationDao communicationDao;
	@EJB(beanName = "DriverCacheBean")
	EntityCache<Driver> driverEntityCache;
	@EJB(beanName = "GeoObjectDictionaryDaoBean")
	Dictionary<GeoObject> geoObjectDictionary;
	@EJB(beanName = "DriverStateDictionaryDaoBean")
	Dictionary<DriverState> driverStateDictionary;
	@EJB(beanName = "AgentDictionaryDaoBean")
	Dictionary<Agent> agentDictionary;
	@EJB(beanName = "TariffDictionaryDaoBean")
	Dictionary<Tariff> tariffDictionary;


	@Override
	public long persist(Driver driver) throws DaoException, CacheException
	{
		if (! driverDao.verify(driver)) {
			throw new DaoException("Verify exception. Driver is not full");
		}
		if (driverEntityCache.getItem(driver.getDriverId()) != null){
			throw new CacheException("Driver cache persist exception. Order is exists in cache");
		}
		driverDao.persist(driver);
		driverEntityCache.putItem(driver);
		return driver.getDriverId();
	}

	@Override
	public Driver merge(Driver driver) throws DaoException, CacheException
	{
		if (! driverDao.verify(driver)) {
			throw new DaoException("Verify exception. Driver is not full");
		}
		driverDao.merge(driver);
		driverEntityCache.putItem(driver);
		return driver;
	}

	@Override
	public Driver find(long driverId) throws DaoException, CacheException
	{
		Driver driver = driverEntityCache.getItem(driverId);
		if (driver == null) {
			driver = driverDao.find(driverId);
			if (driver == null){
				throw new DaoException("Find exception. Driver is not exists in database");
			}
		}
		return driver;
	}

	@Override
	public Map<Long, Driver> findAll() throws DaoException, CacheException
	{
		return driverEntityCache.getItems();
	}

	@Override
	public Map<Long, Driver> findByState(int stateId) throws DaoException, CacheException
	{
		Map<Long, Driver> drivers = new HashMap<Long, Driver>();
		for (Driver driver: driverEntityCache.getItems().values()){
			if (driver.getState().getId() == stateId) {
				drivers.put(driver.getDriverId(), driver);
			}
		}
		return drivers;
	}

	@Override
	public List<Driver> findDrivers(String sql) throws DaoException, CacheException
	{
		return driverDao.findDrivers(sql);
	}

	@Override
	public Driver changeState(long driverId, int newStateId, String comment) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		if (! driver.getState().availableState(newStateId)) {
			throw new DaoException("Driver state change exception. State is not available");
		}
		DriverState newState = driverStateDictionary.getItem(newStateId);
		driverDao.changeDriverState(driver, newState.getId(), comment);
		driver.setState(newState);
		return driver;
	}

	@Override
	public Driver changeDefaultCar(long driverId, int carId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setCar(carDao.find(carId));
		driverDao.changeDefaultCar(driver.getDriverId(), driver.getCar().getCarId());
		return driver;
	}

	@Override
	public Driver changeDefaultCommunication(long driverId, long communicationId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setCommunication(communicationDao.find(communicationId));
		driverDao.changeDefaultCommunication(driver.getDriverId(), driver.getCommunication().getCommunicationId());
		return driver;
	}

	@Override
	public Driver changeDefaultAccount(long driverId, long accountId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setAccount(accountDao.find(accountId));
		driverDao.changeDefaultAccount(driver.getDriverId(), driver.getAccount().getAccountId());
		return driver;
	}

	@Override
	public Driver changeDefaultDriverTariff(long driverId, long driverTariffId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setDriverTariff(driverDao.findDriverTariff(driverTariffId));
		driverDao.changeDefaultDriverTariff(driver.getDriverId(), driver.getDriverTariff().getDriverTariffId());
		return driver;
	}

	@Override
	public Driver disable(long driverId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setBlockage(true);
		driverDao.disable(driver.getDriverId());
		return driver;
	}

	@Override
	public Driver enable(long driverId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setBlockage(false);
		driverDao.enable(driver.getDriverId());
		return driver;
	}

	@Override
	public Driver changeRadius(long driverId, int radius) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setRadiusWork(radius);
		driverDao.changeRadius(driver.getDriverId(), driver.getRadiusWork());
		return driver;
	}

	@Override
	public Driver changeRating(long driverId, int rating) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setRating(rating);
		driverDao.changeRating(driver.getDriverId(), driver.getRating());
		return driver;
	}

	@Override
	public Driver changePriority(long driverId, int priority) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setPriority(priority);
		driverDao.changePriority(driver.getDriverId(), driver.getPriority());
		return driver;
	}

	@Override
	public Driver changePosition(long driverId, Point point) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		GeoObject geoObject = new GeoObject();
		geoObject.setPosition(new GeoObjectPosition(point));
		driver.setGeoObject(geoObject);
		driverDao.changePosition(driver.getDriverId(), point);
		return driver;
	}

	@Override
	public Driver changePosition(long driverId, int geoObjectId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setGeoObject(geoObjectDictionary.getItem(geoObjectId));
		driverDao.changePosition(driver.getDriverId(), driver.getGeoObject().getPosition());
		return driver;
	}

	@Override
	public Driver changeLastDateOfRegistration(
		long driverId, Date lastDateOfRegistration) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setLastDateOfRegistration(lastDateOfRegistration);
		driverDao.changeLastDateOfRegistration(driver.getDriverId(), driver.getLastDateOfRegistration());
		return driver;
	}

	@Override
	public Driver changeAgent(long driverId, int agentId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setAgent(agentDictionary.getItem(agentId));
		driverDao.changeAgent(driver.getDriverId(),driver.getAgent().getId());
		return driver;
	}

	@Override
	public Driver removeDriverDistricts(
		long driverId, List<District> districts) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driverDao.removeDriverDistricts(driverId, districts);
		for (District district: districts){
			driver.getDistricts().remove(district);
		}
		return driver;
	}

	@Override
	public Driver insertDriverDistricts(long driverId, List<District> districts) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driverDao.insertDriverDistricts(driverId, districts);
		for (District district: districts){
			driver.getDistricts().add(district);
		}
		return driver;
	}

	@Override
	public Driver findDriverDistricts(long driverId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driver.setDistricts(driverDao.findDriverDistricts(driverId));
		return driver;
	}

	@Override
	public Driver insertDriverTariff(long driverId, int tariffId, Date startDate) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		DriverTariff driverTariff = new DriverTariff();
		driverTariff.setTariff(tariffDictionary.getItem(tariffId));
		driverTariff.setDriverTariffId(driverDao.insertDriverTariff(driverId, driverTariff.getTariff(), startDate));
		driverTariff.setStartDate(startDate);
		driverTariff.setEndDate(new Date(startDate.getTime() + driverTariff.getTariff().getDuration()));
		if (driverTariff.getEndDate().before(new Date()) && driverTariff.getEndDate().after(new Date())){
			driver.setDriverTariff(driverTariff);
			driverDao.changeDefaultDriverTariff(driverId, driverTariff.getDriverTariffId());
		}
		return driver;
	}

	@Override
	public Driver removeDriverTariff(long driverId, long driverTariffId) throws DaoException, CacheException
	{
		Driver driver = find(driverId);
		driverDao.removeDriverTariff(driverTariffId);
		if(driverTariffId == driver.getDriverTariff().getDriverTariffId()){
			driver.setDriverTariff(new DriverTariff());
		}
		return driver;
	}

	@Override
	public DriverTariff findDriverTariff(long driverTariffId) throws DaoException, CacheException
	{
		return driverDao.findDriverTariff(driverTariffId);
	}

	@Override
	public List<DriverTariff> findDriverTariffs(long driverId) throws DaoException, CacheException
	{
		return driverDao.findDriverTariffs(driverId);
	}

	@Override
	public void removeFromCache(long driverId) throws CacheException
	{
		if (driverEntityCache.getItem(driverId) != null){
			driverEntityCache.removeItem(driverId);
		}
	}

	@Override
	public Driver addToCache(long driverId) throws CacheException, DaoException
	{
		logger.info("DriverServiceBean. 1. New driver " + driverId + " got connected");
		Driver driver = driverEntityCache.getItem(driverId);
		logger.info("DriverServiceBean. 2. driver is null  " + (driver==null));
		if (driver == null) {
			driver = driverDao.find(driverId);
			if (driver == null){
				throw new DaoException("Find exception. Driver is not exists in database");
			}
			driverEntityCache.putItem(driver);
		}
		return driver;
	}
}
