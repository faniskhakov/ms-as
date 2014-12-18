package ru.taxims.controller.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.taxims.controller.core.CommonCommandDistributor;
import ru.taxims.controller.core.DictionaryCommandDistributor;
import ru.taxims.controller.core.UserCommandDistributor;
import ru.taxims.controller.wrapper.Wrapper;
import ru.taxims.domain.datamodels.CarBrand;
import ru.taxims.domain.event.CarBrandEvent;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.core.UserService;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by Developer_DB on 06.11.14.
 */
@ServerEndpoint
	(value = UserWebSocket.SOCKET_PATH)
public class UserWebSocket {
	private static Logger log = Logger.getLogger(UserWebSocket.class);
	public static final String SOCKET_PATH = "/user/{userId}/{cityId}";
	private Gson gson = new Gson();

	@Inject
	UserService userService;
	@Inject
	UserCommandDistributor userCommandDistributor;
	@Inject
	DictionaryCommandDistributor dictionaryCommandDistributor;
	@Inject
	CommonCommandDistributor commonCommandDistributor;

	private static Map<Long, Session> openSessions = Collections.synchronizedMap(new HashMap<Long, Session>());

	@OnOpen
	public void onOpenConnection(@PathParam("userId") long userId, Session session) 
		throws CacheException, DaoException
	{
		session.getUserProperties().put("userId", userId);
		//log.info(" user s.getUserPrincipal().getName() " + session.getUserPrincipal().getName());
		userService.addToCache(userId);
		openSessions.put(userId, session);
		log.info("UserWebSocket. New user " + userId + " got connected");
	}

	@OnMessage
	public void onMessage(@PathParam("userId") final long userId, Session session, String message) throws CacheException, DaoException
	{
		try {
			JsonObject jsonObject = (new JsonParser().parse(message)).getAsJsonObject();
			Wrapper wrapper = commonCommandDistributor.getWrapper(jsonObject);
			log.info("UserWebSocket. 1. Message received: " + wrapper.getCommand());
			if (wrapper.getCommand() == null){
				wrapper = userCommandDistributor.getWrapper(userId, jsonObject);
				log.info("UserWebSocket. 1. Message received: " + wrapper.getCommand());
			}
			if (wrapper.getCommand() == null){
				wrapper = dictionaryCommandDistributor.getWrapper(jsonObject);
				log.info("UserWebSocket. 1. Message received: " + wrapper.getCommand());
			}
			session.getBasicRemote().sendText(gson.toJson(wrapper));
			log.info("UserWebSocket. Message received: " + message);
		} catch (IOException e) {
			log.error("UserWebSocket. onMessage failed userId = " + userId + "; ", e);
		}
	}

	@OnClose
	public void onCloseConnection(@PathParam("userId") final long userId, Session session)
		throws CacheException, IOException
	{
		userService.removeFromCache(userId);
		openSessions.remove(session);
		log.info("UserWebSocket. User " + userId +  " got disconnected");
	}

	@OnError
	public void onError(@PathParam("userId") final long userId,
		Session session, Throwable throwable) throws CacheException, IOException
	{
		userService.removeFromCache(userId);
		openSessions.remove(session);
		log.info("UserWebSocket. Error. User " + userId +  " are living");
	}

	public void broadcastArticle(@Observes @CarBrandEvent CarBrand carBrand) {
		for (Session s : openSessions.values()) {
			if (s.isOpen()) {
				try {
					//log.info(" uesr s.getUserPrincipal().getName() " + s.getUserPrincipal().getName());
					s.getBasicRemote().sendText(gson.toJson(carBrand));
				} catch (IOException  e) {
					log.error("onMessage failed Car brand name ", e);
				}
			}
		}
	}
}