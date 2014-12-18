package ru.taxims.domain.interfaces.core;

import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 25.08.14.
 */
@Local
public interface DriverService
{
	long persist(Driver driver) throws DaoException, CacheException;
	Driver merge(Driver driver) throws DaoException, CacheException;
	Driver find(long driverId) throws DaoException, CacheException;
	Map<Long, Driver> findAll() throws DaoException, CacheException;
	Map<Long, Driver> findByState(int stateId) throws DaoException, CacheException;
	List<Driver> findDrivers(String sql) throws DaoException, CacheException;
	Driver changeState(long driverId, int newState, String comment) throws DaoException, CacheException;
	Driver changeDefaultCar(long driverId, int CarId) throws DaoException, CacheException;
	Driver changeDefaultCommunication(long driverId, long communicationId) throws DaoException, CacheException;
	Driver changeDefaultAccount(long driverId, long accountId) throws DaoException, CacheException;
	Driver changeDefaultDriverTariff(long driverId, long driverTariffId) throws DaoException, CacheException;
	Driver disable(long driverId) throws DaoException, CacheException;
	Driver enable(long driverId) throws DaoException, CacheException;
	Driver changeRadius(long driverId, int radius) throws DaoException, CacheException;
	Driver changeRating(long driverId, int rating) throws DaoException, CacheException;
	Driver changePriority(long driverId, int priority) throws DaoException, CacheException;
	Driver changePosition(long driverId, Point point) throws DaoException, CacheException;
	Driver changePosition(long driverId, int geoObjectId) throws DaoException, CacheException;
	Driver changeLastDateOfRegistration(long driverId, Date lastDateOfRegistration) throws DaoException, CacheException;
	Driver changeAgent(long driverId, int agentId) throws DaoException, CacheException;
	Driver removeDriverDistricts(long driverId, List<District> districts) throws DaoException, CacheException;
	Driver insertDriverDistricts(long driverId, List<District> districts) throws DaoException, CacheException;
	Driver findDriverDistricts(long driverId) throws DaoException, CacheException;
	Driver insertDriverTariff(long driverId, int tariffId, Date startDate) throws DaoException, CacheException;
	Driver removeDriverTariff(long driverId, long driverTariffId) throws DaoException, CacheException;
	DriverTariff findDriverTariff(long driverTariffId) throws DaoException, CacheException;
	List<DriverTariff> findDriverTariffs(long driverId) throws DaoException, CacheException;
	public void removeFromCache(long driverId) throws  CacheException;
	public Driver addToCache(long driverId) throws CacheException, DaoException;
}
