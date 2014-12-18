package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer_DB on 23.05.14.
 */
@Local
public interface DriverDao extends Dao<Driver>
{
	List<Driver> findDrivers(String sql) throws DaoException;
	void merge(Driver driver) throws DaoException;
	void changeDefaultCar(long driverId, int carId) throws DaoException;
	void changeDefaultCommunication(long driverId, long communicationId) throws DaoException;
	void changeDefaultDriverTariff(long driverId, long driverTariffId) throws DaoException;
	void changeDefaultAccount(long driverId, long accountId) throws DaoException;
	void disable(long driverId) throws DaoException;
	void enable(long driverId) throws DaoException;
	void changeDriverState(Driver driver, int newState, String comment) throws DaoException;
	void changeRadius(long driverId, int radius) throws DaoException;
	void changeRating(long driverId, int rating) throws DaoException;
	void changePriority(long driverId, int priority) throws DaoException;
	void changePosition(long driverId, Point point) throws DaoException;
	void changeLastDateOfRegistration(long driverId, Date lastDateOfRegistration) throws DaoException;
	void changeAgent(long driverId, int agentId) throws DaoException;
	void removeDriverDistricts(long driverId, List<District> districts) throws DaoException;
	void insertDriverDistricts(long driverId, List<District> districts) throws DaoException;
	List<District> findDriverDistricts(long driverId) throws DaoException;
	long insertDriverTariff(long driverId, Tariff tariff, Date startDate) throws DaoException;
	void removeDriverTariff(long driverTariffId) throws DaoException;
	DriverTariff findDriverTariff(long driverTariffId) throws DaoException;
	List<DriverTariff> findDriverTariffs(long driverId) throws DaoException;
}
