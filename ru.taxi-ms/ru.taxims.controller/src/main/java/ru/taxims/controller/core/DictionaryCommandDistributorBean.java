package ru.taxims.controller.core;

import com.google.gson.JsonObject;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DictionaryService;

import javax.inject.Inject;

/**
 * Created by Developer_DB on 20.11.14.
 */
public class DictionaryCommandDistributorBean implements DictionaryCommandDistributor
{
	//private static Logger log = Logger.getLogger(CommandDistributorBean.class);
	@Inject
	DictionaryService dictionaryService;

	@Override
	public Wrapper getWrapper(JsonObject jsonObject)
	{
		try{
			Wrapper wrapper = new Wrapper();
			switch (jsonObject.get("command").getAsString()) {
			case "getActiveFeatures":
				wrapper.setCommand("getActiveFeatures");
				wrapper.setContent(dictionaryService.getActiveFeatures());
				break;
			case "getCarFeatures":
				wrapper.setCommand("getCarFeatures");
				wrapper.setContent(dictionaryService.getCarFeatures());
				break;
			case "getClientFeatures":
				wrapper.setCommand("getClientFeatures");
				wrapper.setContent(dictionaryService.getClientFeatures());
				break;
			case "getDriverFeatures":
				wrapper.setCommand("getDriverFeatures");
				wrapper.setContent(dictionaryService.getDriverFeatures());
				break;
			case "getAccountTypes":
				wrapper.setCommand("getAccountTypes");
				wrapper.setContent(dictionaryService.getAccountTypes());
				break;
			case "getAgents":
				wrapper.setCommand("getAgents");
				wrapper.setContent(dictionaryService.getAgents());
				break;
			case "getCarBrands":
				wrapper.setCommand("getCarBrands");
				wrapper.setContent(dictionaryService.getCarBrands());
				break;
			case "getCarModels":
				wrapper.setCommand("getCarModels");
				wrapper.setContent(dictionaryService.getCarModels(jsonObject.get("carBrandId").getAsInt()));
				break;
			case "getCarBodyTypes":
				wrapper.setCommand("getCarBodyTypes");
				wrapper.setContent(dictionaryService.getCarBodyTypes());
				break;
			case "getCities":
				wrapper.setCommand("getCities");
				wrapper.setContent(dictionaryService.getCites());
				break;
			case "getDistricts":
				wrapper.setCommand("getDistricts");
				wrapper.setContent(dictionaryService.getDistricts());
				break;
			case "getDriverStates":
				wrapper.setCommand("getDriverStates");
				wrapper.setContent(dictionaryService.getDriverStates());
				break;
			case "getColors":
				wrapper.setCommand("getColors");
				wrapper.setContent(dictionaryService.getColors());
				break;
			case "getCommunicationTypes":
				wrapper.setCommand("getCommunicationTypes");
				wrapper.setContent(dictionaryService.getCommunicationTypes());
				break;
			case "getGeoObjectDescriptions":
				wrapper.setCommand("getGeoObjectDescriptions");
				wrapper.setContent(dictionaryService.getGeoObjectDescriptions());
				break;
			case "getGeoObjectTypes":
				wrapper.setCommand("getGeoObjectTypes");
				wrapper.setContent(dictionaryService.getGeoObjectTypes());
				break;
			case "getLanguages":
				wrapper.setCommand("getLanguages");
				wrapper.setContent(dictionaryService.getLanguages());
				break;
			case "getOrderStates":
				wrapper.setCommand("getOrderStates");
				wrapper.setContent(dictionaryService.getOrderStates());
				break;
			case "getOrderTariffs":
				wrapper.setCommand("getOrderTariffs");
				wrapper.setContent(dictionaryService.getOrderTariffs());
				break;
			case "getRoleTypes":
				wrapper.setCommand("getRoleTypes");
				wrapper.setContent(dictionaryService.getRoleTypes());
				break;
			case "getPriorities":
				wrapper.setCommand("getPriorities");
				wrapper.setContent(dictionaryService.getPriorities());
				break;
			case "getRatings":
				wrapper.setCommand("getRatings");
				wrapper.setContent(dictionaryService.getRatings());
				break;
			case "getShifts":
				wrapper.setCommand("getShifts");
				wrapper.setContent(dictionaryService.getShifts());
				break;
			case "getSources":
				wrapper.setCommand("getSources");
				wrapper.setContent(dictionaryService.getSources());
				break;
			case "getTransactionStates":
				wrapper.setCommand("getTransactionStates");
				wrapper.setContent(dictionaryService.getTransactionStates());
				break;
			case "getTariffs":
				wrapper.setCommand("getTariffs");
				wrapper.setContent(dictionaryService.getTariffs());
				break;
			}
			return wrapper;
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}
}
