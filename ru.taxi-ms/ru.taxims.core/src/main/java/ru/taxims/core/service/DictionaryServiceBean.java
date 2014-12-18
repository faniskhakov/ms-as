package ru.taxims.core.service;

import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DictionaryService;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.ejb.EJB;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 19.11.14.
 */
public class DictionaryServiceBean implements DictionaryService
{
	@EJB(beanName = "SourceDictionaryDaoBean")
	protected Dictionary<Source> sourceDictionary;
	@EJB(beanName = "LanguageDictionaryDaoBean")
	protected Dictionary<Language> languageDictionary;
	@EJB(beanName = "AgentDictionaryDaoBean")
	protected Dictionary<Agent> agentDictionary;
	@EJB(beanName = "DriverStateDictionaryDaoBean")
	protected Dictionary<DriverState> driverStateDictionary;
	@EJB(beanName = "FeatureDictionaryDaoBean")
	protected Dictionary<Feature> featureDictionary;
	@EJB(beanName = "CityDictionaryDaoBean")
	protected Dictionary<City> cityDictionary;
	@EJB(beanName = "DistrictDictionaryDaoBean")
	protected Dictionary<District> districtDictionary;
	@EJB(beanName = "TariffDictionaryDaoBean")
	protected Dictionary<Tariff> tariffDictionary;
	@EJB(beanName = "OrderTariffDictionaryDaoBean")
	protected Dictionary<OrderTariff> orderTariffDictionary;
	@EJB(beanName = "CommunicationTypeDictionaryDaoBean")
	protected Dictionary<CommunicationType> communicationTypeDictionary;
	@EJB(beanName = "AccountTypeDictionaryDaoBean")
	protected Dictionary<AccountType> accountTypeDictionary;
	@EJB(beanName = "RoleTypeDictionaryDaoBean")
	protected Dictionary<RoleType> roleTypeDictionary;
	@EJB(beanName = "CarBrandDictionaryDaoBean")
	protected Dictionary<CarBrand> carBrandDictionary;
	@EJB(beanName = "CarModelDictionaryDaoBean")
	protected Dictionary<CarModel> carModelDictionary;
	@EJB(beanName = "ColorDictionaryDaoBean")
	protected Dictionary<Color> colorDictionary;
	@EJB(beanName = "CarBodyTypeDictionaryDaoBean")
	protected Dictionary<CarBodyType> carBodyTypeDictionary;
	@EJB(beanName = "OrderStateDictionaryDaoBean")
	protected Dictionary<OrderState> orderStateDictionary;
	@EJB(beanName = "GeoObjectTypeDictionaryDaoBean")
	protected Dictionary<GeoObjectType> geoObjectTypeDictionary;
	@EJB(beanName = "GeoObjectDescriptionDictionaryDaoBean")
	protected  Dictionary<GeoObjectDescription> geoObjectDescriptionDictionary;
	@EJB(beanName = "TransactionStateDictionaryDaoBean")
	protected Dictionary<TransactionState> transactionStateDictionary;
	@EJB(beanName = "PriorityDictionaryDaoBean")
	protected Dictionary<Priority> priorityDictionary;
	@EJB(beanName = "RatingDictionaryDaoBean")
	protected Dictionary<Rating> ratingDictionary;
	@EJB(beanName = "ShiftDictionaryDaoBean")
	protected Dictionary<Shift> shiftDictionary;


	@Override
	public Map<Integer, Feature> getActiveFeatures() throws DaoException
	{
		Map<Integer, Feature> returnItems = new HashMap<>();
		for (Map.Entry<Integer, Feature> entry: featureDictionary.getItems().entrySet()){
			if (entry.getValue().isEnebled()) {
				returnItems.put(entry.getKey(), entry.getValue());
			}
		}
		return returnItems;
	}

	@Override
	public Map<Integer, Feature> getClientFeatures() throws DaoException
	{
		Map<Integer, Feature> returnItems = new HashMap<>();
		for (Map.Entry<Integer, Feature> entry: featureDictionary.getItems().entrySet()){
			if (entry.getValue().isEnebled() && (entry.getValue().isBindCar() || entry.getValue().isBindDriver())) {
				returnItems.put(entry.getKey(), entry.getValue());
			}
		}
		return returnItems;
	}

	@Override
	public Map<Integer, Feature> getDriverFeatures() throws DaoException
	{
		Map<Integer, Feature> returnItems = new HashMap<>();
		for (Map.Entry<Integer, Feature> entry: featureDictionary.getItems().entrySet()){
			if (entry.getValue().isEnebled() && entry.getValue().isBindDriver() && !entry.getValue().isAutoCalc()) {
				returnItems.put(entry.getKey(), entry.getValue());
			}
		}
		return returnItems;
	}

	@Override
	public Map<Integer, Feature> getCarFeatures() throws DaoException
	{
		Map<Integer, Feature> returnItems = new HashMap<>();
		for (Map.Entry<Integer, Feature> entry: featureDictionary.getItems().entrySet()){
			if (entry.getValue().isEnebled() && entry.getValue().isBindCar() && !entry.getValue().isAutoCalc()) {
				returnItems.put(entry.getKey(), entry.getValue());
			}
		}
		return returnItems;
	}

	@Override
	public Map<Integer, AccountType> getAccountTypes() throws DaoException
	{
		return accountTypeDictionary.getItems();
	}

	@Override
	public Map<Integer, Agent> getAgents() throws DaoException
	{
		return agentDictionary.getItems();
	}

	@Override
	public Map<Integer, CarBrand> getCarBrands() throws DaoException
	{
		return carBrandDictionary.getItems();
	}

	@Override
	public Map<Integer, CarBodyType> getCarBodyTypes() throws DaoException
	{
		return carBodyTypeDictionary.getItems();
	}

	@Override
	public Map<Integer, CarModel> getCarModels(int carBrandId) throws DaoException
	{
		Map<Integer, CarModel> result = new HashMap<>();
		for (Map.Entry<Integer, CarModel> entry: carModelDictionary.getItems().entrySet()){
			if(entry.getValue().getBrand().getId() == carBrandId)
				result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public Map<Integer, City> getCites() throws DaoException
	{
		return cityDictionary.getItems();
	}

	@Override
	public Map<Integer, Color> getColors() throws DaoException
	{
		return colorDictionary.getItems();
	}

	@Override
	public Map<Integer, CommunicationType> getCommunicationTypes() throws DaoException
	{
		return communicationTypeDictionary.getItems();
	}

	@Override
	public Map<Integer, District> getDistricts() throws DaoException
	{
		return districtDictionary.getItems();
	}

	@Override
	public Map<Integer, GeoObjectType> getGeoObjectTypes() throws DaoException
	{
		return geoObjectTypeDictionary.getItems();
	}

	@Override
	public Map<Integer, GeoObjectDescription> getGeoObjectDescriptions() throws DaoException
	{
		return geoObjectDescriptionDictionary.getItems();
	}

	@Override
	public Map<Integer, Language> getLanguages() throws DaoException
	{
		return languageDictionary.getItems();
	}

	@Override
	public Map<Integer, DriverState> getDriverStates() throws DaoException
	{
		return driverStateDictionary.getItems();
	}

	@Override
	public Map<Integer, OrderState> getOrderStates() throws DaoException
	{
		return orderStateDictionary.getItems();
	}

	@Override
	public Map<Integer, TransactionState> getTransactionStates() throws DaoException
	{
		return transactionStateDictionary.getItems();
	}

	@Override
	public Map<Integer, OrderTariff> getOrderTariffs() throws DaoException
	{
		return orderTariffDictionary.getItems();
	}

	@Override
	public Map<Integer, Tariff> getTariffs() throws DaoException
	{
		return tariffDictionary.getItems();
	}

	@Override
	public Map<Integer, Priority> getPriorities() throws DaoException
	{
		return priorityDictionary.getItems();
	}

	@Override
	public Map<Integer, Rating> getRatings() throws DaoException
	{
		return ratingDictionary.getItems();
	}

	@Override
	public Map<Integer, RoleType> getRoleTypes() throws DaoException
	{
		return roleTypeDictionary.getItems();
	}

	@Override
	public Map<Integer, Shift> getShifts() throws DaoException
	{
		return shiftDictionary.getItems();
	}

	@Override
	public Map<Integer, Source> getSources() throws DaoException
	{
		return sourceDictionary.getItems();
	}
}
