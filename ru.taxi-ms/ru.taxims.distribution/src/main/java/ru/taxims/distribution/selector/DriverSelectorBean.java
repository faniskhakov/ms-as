package ru.taxims.distribution.selector;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * Created by Developer_DB on 24.09.14.
 */
@Stateless
@Local
public class DriverSelectorBean implements DriverSelector
{
	Logger logger = Logger.getLogger(DriverSelectorBean.class);

	@EJB(beanName = "SortedFeatureDictionaryDaoBean")
	Dictionary<Feature> sortedFeatureDictionary;

	@Override
	public boolean isDriverSatisfy(Order order, Driver driver) throws DaoException
	{
		logger.info("   ");
		logger.info(" -  DriverSelector.isDriverSatisfy(Order order, Driver driver)  - ");
		for (Map.Entry<Integer, String> orderFeatureEntry : order.getFeatures().entrySet()) {
			Feature feature = sortedFeatureDictionary.getItem(orderFeatureEntry.getKey());
			if(compareFeature(feature, driver, orderFeatureEntry)){
				logger.info(" Driver Feature is Satisfy Name " + feature.getName() + " orderValue = " + orderFeatureEntry.getValue()
					+ " driverValue = " + driver.getFeatures().get(orderFeatureEntry.getKey()) + " ComparisonRule = " + feature.getComparisonRule().getId());
			} else {
				logger.info(" Driver Feature is NOT SATISFY Name " + feature.getName() + " orderValue = " + orderFeatureEntry.getValue()
					+ " driverValue = " + driver.getFeatures().get(orderFeatureEntry.getKey()) + " ComparisonRule = " + feature.getComparisonRule().getId());
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDriverRepeat(Order order, Driver driver) throws DaoException
	{
		logger.info(" -  DriverSelector.isDriverRepeat(Order order, Driver driver)  - ");
		for (OrderDistribution orderDistribution: order.getDistributions()){
			logger.info(" DriverSelector.isDriverRepeat(Order order, Driver driver)  orderId " + order.getOrderId() +
				" driverId " + orderDistribution.getDriverId() + " state" + orderDistribution.getDistributionState().getName());
			if (orderDistribution.getDriverId() == driver.getDriverId() && orderDistribution.getDistributionState().isOrderRefused()){
				logger.info(" -  DriverSelector.isDriverRepeat(Order order, Driver driver)  2 ");
				return true;
			}
			if (orderDistribution.getDistributionState().isOrderOffered()){
				logger.info(" -  DriverSelector.isDriverRepeat(Order order, Driver driver)  3 ");
				return true;
			}
		}
		return false;
	}

	private boolean compareFeature(Feature feature, Driver driver, Map.Entry<Integer, String> orderFeatureEntry)
	{
		String featureValue = driver.getFeatures().get(orderFeatureEntry.getKey());
		if (featureValue == null) {
			featureValue = driver.getCar().getFeatures().get(orderFeatureEntry.getKey());
			if (featureValue == null) return false;
		}
		switch (feature.getComparisonRule().getId()) {
			case 1: return existenceValues(orderFeatureEntry.getValue(), featureValue);
			case 2: return compareValues(orderFeatureEntry.getValue(), featureValue);
			case 3: return compareCarBodyType(orderFeatureEntry.getValue(), driver.getCar().getBodyType());
			case 4: return isDriverWoman(orderFeatureEntry.getValue(), driver.getGender());
			case 5: return isDriverMan(orderFeatureEntry.getValue(), driver.getGender());
			case 6: return compareTime(orderFeatureEntry.getValue());
			default: return false;
		}
	}

	private boolean compareValues(String orderValue, String driverValue)
	{
		return Integer.parseInt(driverValue) >= Integer.parseInt(orderValue);
	}

	private boolean existenceValues(String orderValue, String driverValue)
	{
		return Integer.parseInt(driverValue) > 0 && Integer.parseInt(orderValue) > 0;
	}

	private boolean compareCarBodyType(String orderValue, CarBodyType carBodyType)
	{
		return carBodyType.getId() == Integer.parseInt(orderValue);
	}

	private boolean isDriverMan(String orderValue, char gender)
	{
		return gender == 'M';
	}

	private boolean isDriverWoman(String orderValue, char gender)
	{
		return gender == 'F';
	}

	private boolean compareTime(String orderValue)
	{
		return true;
	}
}
