package ru.taxims.controller.driver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.taxims.controller.core.CommonCommandDistributor;
import ru.taxims.controller.core.DictionaryCommandDistributor;
import ru.taxims.controller.core.DriverCommandDistributor;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.datamodels.CarBrand;
import ru.taxims.domain.event.CarBrandEvent;
import ru.taxims.domain.event.OfferOrderEvent;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.core.OrderService;
import ru.taxims.domain.util.Pair;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by Developer_DB on 30.10.14.
 */

@ServerEndpoint
	(value = DriverWebSocket.SOCKET_PATH)
public class DriverWebSocket {
	private static Logger log = Logger.getLogger(DriverWebSocket.class);
	public static final String SOCKET_PATH = "/driver/{driverId}/{cityId}";
	private Gson gson = new Gson();
	@Inject
	DriverCommandDistributor driverCommandDistributor;
	@Inject
	DictionaryCommandDistributor dictionaryCommandDistributor;
	@Inject
	CommonCommandDistributor commonCommandDistributor;
	@Inject
	DriverService driverService;
	@Inject
	OrderService orderService;

	private static Map<Long, Session> openSessions = Collections.synchronizedMap(new HashMap<Long, Session>());

	@OnOpen
	public void onOpenConnection(@PathParam("driverId") long driverId,	Session session) throws CacheException, DaoException
	{
		session.getUserProperties().put("driverId", driverId);
		openSessions.put(driverId, session);
		driverService.addToCache(driverId);
		log.info("DriverWebSocket. New driver " + driverId + " got connected");
	}

	@OnMessage
	public void onMessage(@PathParam("driverId") final long driverId, Session session, String message) throws CacheException, DaoException
	{
		try {
			JsonObject  jsonObject = (new JsonParser().parse(message)).getAsJsonObject();
			Wrapper wrapper = driverCommandDistributor.getWrapper(driverId, jsonObject);
			if (wrapper.getCommand() == null){
				wrapper = commonCommandDistributor.getWrapper(jsonObject);
			}
			if (wrapper.getCommand() == null){
				wrapper = dictionaryCommandDistributor.getWrapper(jsonObject);
			}
			session.getBasicRemote().sendText(gson.toJson(wrapper));
			log.info("DriverWebSocket. Message received: " + message);
		} catch (IOException  e) {
			log.error("DriverWebSocket. onMessage failed driverId = " + driverId + "; ", e);
		}
	}

	@OnClose
	public void onCloseConnection(@PathParam("driverId") final long driverId, Session session)
		throws CacheException, IOException
	{
		driverService.removeFromCache(driverId);
		openSessions.remove(driverId);
		log.info("DriverWebSocket. Driver " + driverId +  " got disconnected");
	}

	@OnError
	public void onError(@PathParam("driverId") final long driverId,
		Session session, Throwable throwable) throws CacheException
	{
		driverService.removeFromCache(driverId);
		openSessions.remove(driverId);
		log.info("DriverWebSocket. Error. DriverId " + driverId +  ",  throwable " + throwable);
	}

	public void broadcastArticle(@Observes @CarBrandEvent CarBrand carBrand) {
		for (Session s : openSessions.values()) {
			if (s.isOpen()) {
				try {
					//log.info(" s.getUserPrincipal().getName() " + s.getUserPrincipal().getName());
					s.getBasicRemote().sendText(gson.toJson(carBrand));
				} catch (IOException  e) {
					log.error("onMessage failed Car brand name ", e);
				}
			}
		}
	}

	public void offerOrder(@Observes @OfferOrderEvent Pair<Long, Long> pair) {
		Session session = openSessions.get(pair.getValue());
		try {
			Wrapper wrapper = new Wrapper();
			wrapper.setCommand("offerOrder");
			wrapper.setContent(orderService.orderOffer(pair.getKey(), pair.getValue()));
			session.getBasicRemote().sendText(gson.toJson(wrapper));
		} catch (IOException | CacheException | DaoException e) {
			e.printStackTrace();
		}
	}
}