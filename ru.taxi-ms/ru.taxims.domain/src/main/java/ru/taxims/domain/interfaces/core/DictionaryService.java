package ru.taxims.domain.interfaces.core;

import ru.taxims.domain.datamodels.*;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.Map;

/**
 * Created by Developer_DB on 19.11.14.
 */
@Local
public interface DictionaryService
{
	public Map<Integer, Feature> getActiveFeatures() throws DaoException;
	public Map<Integer, Feature> getClientFeatures() throws DaoException;
	public Map<Integer, Feature> getDriverFeatures() throws DaoException;
	public Map<Integer, Feature> getCarFeatures() throws DaoException;
	public Map<Integer, AccountType> getAccountTypes() throws DaoException;
	public Map<Integer, Agent> getAgents() throws DaoException;
	public Map<Integer, CarBrand> getCarBrands() throws DaoException;
	public Map<Integer, CarBodyType> getCarBodyTypes() throws DaoException;
	public Map<Integer, CarModel> getCarModels(int carBrandId) throws DaoException;
	public Map<Integer, City> getCites() throws DaoException;
	public Map<Integer, Color> getColors() throws DaoException;
	public Map<Integer, CommunicationType> getCommunicationTypes() throws DaoException;
	public Map<Integer, District> getDistricts() throws DaoException;
	public Map<Integer, GeoObjectType> getGeoObjectTypes() throws DaoException;
	public Map<Integer, GeoObjectDescription> getGeoObjectDescriptions() throws DaoException;
	public Map<Integer, Language> getLanguages() throws DaoException;
	public Map<Integer, DriverState> getDriverStates() throws DaoException;
	public Map<Integer, OrderState> getOrderStates() throws DaoException;
	public Map<Integer, TransactionState> getTransactionStates() throws DaoException;
	public Map<Integer, OrderTariff> getOrderTariffs() throws DaoException;
	public Map<Integer, Tariff> getTariffs() throws DaoException;
	public Map<Integer, Priority> getPriorities() throws DaoException;
	public Map<Integer, Rating> getRatings() throws DaoException;
	public Map<Integer, RoleType> getRoleTypes() throws DaoException;
	public Map<Integer, Shift> getShifts() throws DaoException; 																			//не ругайтесь насчет названия, когда выбирал название мной управлял не совсем вменяемый персонаж
	public Map<Integer, Source> getSources() throws DaoException;
}
