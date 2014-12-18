package ru.taxims.core.cache;

import org.apache.log4j.Logger;
import ru.taxims.domain.criteria.DriverSearchCriteria;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dao.*;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.List;

/**
 * Created by Developer_DB on 08.08.14.
 */
@Singleton
@Startup
@Local(EntityCache.class)
@DependsOn({"OrderCacheBean"})
public class DriverCacheBean extends EntityCacheBean<Driver>
{
	Logger logger = Logger.getLogger(OrderCacheBean.class);

	@EJB
	CarDao carDao;
	@EJB
	DriverDao driverDao;

	@Override
	public void putItem(Driver driver) throws CacheException
	{
		super.putItem(driver);


		items.put(driver.getDriverId(), driver);
	}

	@PostConstruct
	protected void init() throws DaoException, CacheException
	{
		List<Driver> drivers = driverDao.findDrivers(DriverSearchCriteria.blockageCriteria(false));//todo
		for(Driver driver :drivers){
			//driver = (Driver)userDao.find(driver);
			driver.setCar(carDao.find(driver.getCar().getCarId()));
			putItem(driver);
			logger.info(" Driver  id ="  +	driver.getDriverId() + " Name = " + driver.getName());
			logger.info(" Driver  State ="  + driver.getState() + " Car Number = " + driver.getCar().getNumber());
		}
	}
}
