package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.Car;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 27.05.14.
 */
@Local
public interface CarDao extends  Dao<Car>
{
	Car findCar(String number) throws DaoException;
	List<Car> findCars(String sql) throws DaoException;
	void merge(Car car) throws DaoException;
	void disable(int carId) throws DaoException;
	void enable(int carId) throws DaoException;
}
