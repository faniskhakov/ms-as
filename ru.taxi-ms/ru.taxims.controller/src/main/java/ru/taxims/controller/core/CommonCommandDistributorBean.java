package ru.taxims.controller.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.datamodels.GeoAddress;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.datamodels.Order;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.exception.PriceException;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.interfaces.gis.*;
import ru.taxims.domain.interfaces.price.PriceCalculator;
import ru.taxims.domain.interfaces.price.PriceCalculatorMain;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 25.11.14.
 */
public class CommonCommandDistributorBean implements CommonCommandDistributor
{
	private static Logger log = Logger.getLogger(CommonCommandDistributorBean.class);
	private Gson gson = new Gson();
	@Inject @ObjectFinderCache
	ObjectFinder objectFinder;
	@Inject @RouterDB
	Router router;
	@Inject @GeocoderMain
	Geocoder geocoder;
	@Inject @PriceCalculatorMain
	PriceCalculator priceCalculator;
	@Inject
	OrderService orderService;


	@Override
	public Wrapper getWrapper(JsonObject jsonObject)
	{
		try{
			Wrapper wrapper = new Wrapper();
			switch (jsonObject.get("command").getAsString()) {
			case "getGeoObjects":
				wrapper.setCommand("getGeoObjects");
				wrapper.setContent(objectFinder.getObjects(jsonObject.get("fragment").getAsString()));
				break;
			case "getGeoObjectsByType":
				wrapper.setCommand("getGeoObjectsByType");
				wrapper.setContent(objectFinder.getObjectsByType(jsonObject.get("fragment").getAsString(), jsonObject.get("typeId").getAsInt()));
				break;
			case "getGeoObjectsByParent":
				wrapper.setCommand("getGeoObjectsByParent");
				wrapper.setContent(objectFinder.getObjectsByParent(jsonObject.get("fragment").getAsString(), jsonObject.get("parentId").getAsInt()));
				break;
			case "getAddress":
				wrapper.setCommand("getAddress");
				wrapper.setContent(geocoder.geocode(gson.fromJson(jsonObject.get("point").toString(), Point.class)));
				break;
			case "getPoint":
				wrapper.setCommand("getPoint");
				wrapper.setContent(geocoder.inverseGeocode(gson.fromJson(jsonObject.get("geoAddress").toString(), GeoAddress.class)));
				break;
			case "calculatePrice":
				Map<Integer, String> calculatedFeatures = gson.fromJson(jsonObject.get("features").toString(),
					new TypeToken<Map<Integer, String>>(){}.getType());
				wrapper.setCommand("calculatePrice");
				wrapper.setContent(priceCalculator.calculatePrice(calculatedFeatures));
				break;
			case "calculateRoute":
				List<GeoObject> geoObjects = gson.fromJson(jsonObject.get("geoObjects").toString(),
					new TypeToken<List<GeoObject>>(){}.getType());
				wrapper.setCommand("calculateRouteGeoObjects");
				wrapper.setContent(router.getDistanceGeoObjects(geoObjects));
				break;
			case "calculateRouteFromPoints":
				List<Point> points = gson.fromJson(jsonObject.get("points").toString(),
					new TypeToken<List<Point>>(){}.getType());
				wrapper.setCommand("calculateRoutePoints");
				wrapper.setContent(router.getDistancePoints(points));
				break;
			case "getOrder":
				wrapper.setCommand("getOrder");
				wrapper.setContent(orderService.find(jsonObject.get("orderId").getAsLong()));
				break;
			case "persistOrder":
				wrapper.setCommand("persistOrder");
				wrapper.setContent(orderService.find(orderService.persist(gson.fromJson(jsonObject.get("order").toString(), Order.class))));
				break;
			case "changeOrderState":
				orderService.changeState(jsonObject.get("orderId").getAsLong(), jsonObject.get("newStateId").getAsInt(), jsonObject.get("comment").getAsString());
				wrapper.setCommand("changeOrderState");
				wrapper.setContent(orderService.find(jsonObject.get("orderId").getAsLong()));
				break;
			case "insertAmount":
				orderService.insertAmount(jsonObject.get("orderId").getAsLong(), jsonObject.get("amount").getAsFloat());
				wrapper.setCommand("insertAmount");
				wrapper.setContent(orderService.find(jsonObject.get("orderId").getAsLong()));
				break;
			case "insertWaypoints":
				Map<Integer, GeoObject> waypoints = gson.fromJson(jsonObject.get("waypoints").toString(),
					new TypeToken<Map<Integer, GeoObject>>(){}.getType());
				orderService.insertWaypoints(jsonObject.get("orderId").getAsLong(), waypoints);
				wrapper.setCommand("insertWaypoints");
				wrapper.setContent(orderService.find(jsonObject.get("orderId").getAsLong()));
				break;
			case "insertFeatures":
				Map<Integer, String> insertedFeatures = gson.fromJson(jsonObject.get("features").toString(),
					new TypeToken<Map<Integer, String>>(){}.getType());
				orderService.insertFeatures(jsonObject.get("orderId").getAsLong(), insertedFeatures);
				wrapper.setCommand("insertAmount");
				wrapper.setContent(orderService.find(jsonObject.get("orderId").getAsLong()));
				break;
			}
			return wrapper;
		} catch (SQLException  | DaoException e) {
			log.error(" CommonCommandDistributorBean.getWrapper. Error ", e);
			return null;
		} catch (GISException | PriceException e) {
			e.printStackTrace();
			return null;
		} catch (CacheException e) {
			e.printStackTrace();
			return null;
		}
	}
}
