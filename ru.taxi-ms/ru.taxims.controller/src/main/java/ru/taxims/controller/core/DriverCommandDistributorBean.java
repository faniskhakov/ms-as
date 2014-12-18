package ru.taxims.controller.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.datamodels.District;
import ru.taxims.domain.datamodels.Driver;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.core.UserService;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer_DB on 20.11.14.
 */
public class DriverCommandDistributorBean implements DriverCommandDistributor
{
	private static Logger log = Logger.getLogger(DriverCommandDistributorBean.class);
	private Gson gson = new Gson();
	@Inject
	UserService userService;
	@Inject
	DriverService driverService;
	@Inject
	OrderService orderService;

	@Override
	public Wrapper getWrapper(long driverId, JsonObject jsonObject)
	{
		try{
			Wrapper wrapper = new Wrapper();
			switch (jsonObject.get("command").getAsString()) {
			case "setPosition":
				wrapper.setCommand("setPosition");
				wrapper.setContent(driverService.changePosition(driverId, gson.fromJson(jsonObject.get("position").toString(), Point.class)));
				break;
			case "setGeoObject":
				wrapper.setCommand("setGeoObject");
				wrapper.setContent(driverService.changePosition(driverId, jsonObject.get("geoObjectId").getAsInt()));
				break;
			case "acceptOrder":
				Order order;
				if (jsonObject.get("result").getAsBoolean()) {
					order = orderService.assignDriver(jsonObject.get("orderId").getAsLong(), driverId);
				} else {
					order = orderService.orderRefuse(jsonObject.get("orderId").getAsLong(), driverId);
				}
				wrapper.setCommand("acceptOrder");
				wrapper.setContent(order);
				break;
			case "getDriver":
				wrapper.setCommand("getDriver");
				wrapper.setContent(driverService.find(jsonObject.get("driverId").getAsLong()));
				break;
			case "getUser":
				wrapper.setCommand("getUser");
				wrapper.setContent(userService.find(jsonObject.get("userId").getAsLong()));
				break;
			case "changeState":
				wrapper.setCommand("changeState");
				wrapper.setContent(driverService.changeState(driverId, jsonObject.get("driverStateId").getAsInt(), jsonObject.get("comment").getAsString()));
				break;
			case "changeCar":
				wrapper.setCommand("changeCar");
				wrapper.setContent(driverService.changeDefaultCar(driverId,
					jsonObject.get("carId").getAsInt()));
				break;
			case "changeDriverTariff":
				wrapper.setCommand("changeDriverTariff");
				wrapper.setContent(driverService.changeDefaultDriverTariff(driverId, jsonObject.get("driverTariffId").getAsLong()));
				break;
			case "changeCommunication":
				wrapper.setCommand("changeCommunication");
				wrapper.setContent(driverService.changeDefaultCommunication(driverId,
					jsonObject.get("communicationId").getAsLong()));
				break;
			case "changeAccount":
				wrapper.setCommand("changeAccount");
				wrapper.setContent(driverService.changeDefaultAccount(driverId, jsonObject.get("accountId").getAsLong()));
				break;
			case "changeAgent":
				wrapper.setCommand("changeAgent");
				wrapper.setContent(driverService.changeAgent(driverId, jsonObject.get("agentId").getAsInt()));
				break;
			case "changeRadius":
				wrapper.setCommand("changeRadius");
				wrapper.setContent(driverService.changeRadius(driverId,  jsonObject.get("radius").getAsInt()));
				break;
			case "merge":
				wrapper.setCommand("merge");
				wrapper.setContent(driverService.merge(gson.fromJson(jsonObject.get("driver").toString(), Driver.class)));
				break;
			case "findDriverDistricts":
				wrapper.setCommand("findDriverDistricts");
				wrapper.setContent(driverService.findDriverDistricts(driverId));
				break;
			case "insertDriverDistricts":
				wrapper.setCommand("insertDriverDistricts");
				List<District> insertedDistricts = gson.fromJson(jsonObject.get("districts").toString(),
					new TypeToken<List<District>>(){}.getType());
				wrapper.setContent(driverService.insertDriverDistricts(driverId, insertedDistricts));
				break;
			case "removeDriverDistricts":
				wrapper.setCommand("removeDriverDistricts");
				List<District> removedDistricts = gson.fromJson(jsonObject.get("districts").toString(),
					new TypeToken<List<District>>(){}.getType());
				wrapper.setContent(driverService.removeDriverDistricts(driverId, removedDistricts));
				break;
			case "findDriverTariff":
				wrapper.setCommand("findDriverTariff");
				wrapper.setContent(driverService.findDriverTariff(jsonObject.get("driverTariffId").getAsLong()));
				break;
			case "findDriverTariffs":
				wrapper.setCommand("findDriverTariffs");
				wrapper.setContent(driverService.findDriverTariffs(driverId));
				break;
			case "insertDriverTariff":
				wrapper.setCommand("insertDriverTariff");
				wrapper.setContent(driverService.insertDriverTariff(driverId, jsonObject.get("tariffId").getAsInt(),
					new Date(jsonObject.get("tariffId").getAsLong())));
				break;
			case "removeDriverTariff":
				wrapper.setCommand("removeDriverTariff");
				wrapper.setContent(driverService.removeDriverTariff(driverId, jsonObject.get("tariffId").getAsInt()));
				break;
			case "disableDriver":
				wrapper.setCommand("disableDriver");
				wrapper.setContent(driverService.disable(driverId));
				break;
			case "enableDriver":
				wrapper.setCommand("enableDriver");
				wrapper.setContent(driverService.enable(driverId));
				break;
			}
			return wrapper;
		} catch ( CacheException | DaoException e) {
			log.error("onMessage failed userId = " + driverId + "; ", e);
			return null;
		}
	}
}
