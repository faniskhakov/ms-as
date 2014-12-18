package ru.taxims.controller.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.datamodels.User;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.core.UserService;

import javax.inject.Inject;

/**
 * Created by Developer_DB on 20.11.14.
 */
public class UserCommandDistributorBean implements UserCommandDistributor
{
	private static Logger log = Logger.getLogger(DictionaryCommandDistributorBean.class);
	private Gson gson = new Gson();
	@Inject
	UserService userService;
	@Inject
	DriverService driverService;

	@Override
	public Wrapper getWrapper(long userId, JsonObject jsonObject)
	{
		try{
			Wrapper wrapper = new Wrapper();
			switch (jsonObject.get("command").getAsString()) {
			case "getUser":
				wrapper.setCommand("getUser");
				wrapper.setContent(userService.find(jsonObject.get("userId").getAsLong()));
				break;
			case "getDriver":
				wrapper.setCommand("getDriver");
				wrapper.setContent(driverService.find(jsonObject.get("driverId").getAsLong()));
				break;
			case "persistUser":
				wrapper.setCommand("persistUser");
				wrapper.setContent(userService.find(userService.persist(gson.fromJson(jsonObject.get("user").toString(), User.class))));
				break;
			case "changeCommunication":
				wrapper.setCommand("insertFeatures");
				wrapper.setContent(userService.changeDefaultCommunication(userId,
					jsonObject.get("communicationId").getAsLong()));
				break;
			case "changeAccount":
				wrapper.setCommand("changeAccount");
				wrapper.setContent(userService.changeDefaultAccount(userId, jsonObject.get("accountId").getAsLong()));
				break;
			case "disableUser":
				wrapper.setCommand("disableUser");
				wrapper.setContent(userService.disable(userId));
				break;
			case "enableUser":
				wrapper.setCommand("enableUser");
				wrapper.setContent(userService.enable(userId));
				break;
			}
			return wrapper;
		} catch (CacheException | DaoException e) {
			log.error("onMessage failed userId = " + userId + "; ", e);
			return null;
		}
	}
}
