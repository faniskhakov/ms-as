package service;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.CarBrand;
import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.datamodels.GeoObject;
import ru.taxims.domain.event.CarBrandEvent;
import ru.taxims.domain.exception.CacheException;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.exception.PriceException;
import ru.taxims.domain.interfaces.core.DriverService;
import ru.taxims.domain.interfaces.dictionary.Dictionary;
import ru.taxims.domain.interfaces.gis.ObjectFinder;
import ru.taxims.domain.interfaces.price.PriceCalculator;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer_DB on 20.10.14.
 */
@Path("/entity")
@Produces("text/html; charset=UTF-8")
//@Stateless
public class CarModelRestServiceBean
{
	Logger logger = Logger.getLogger(CarModelRestServiceBean.class);
	Gson gson = new Gson();

	@Inject @CarBrandEvent
	private Event<CarBrand> carBrandEvent;

	@EJB(beanName = "CarBrandDictionaryDaoBean")
	Dictionary<CarBrand> carBrandDictionary;

	@EJB
	DriverService driverService;

	@EJB(beanName = "FeatureDictionaryDaoBean")
	Dictionary<Feature> featureDictionary;

	@EJB
	PriceCalculator priceCalculator;


	@EJB(beanName = "ObjectFinderCacheBean")
	ObjectFinder objectFinder;

	@GET
	@Path("/car/brand/{carBrandId : \\d+}")
	public Response getCarModel(@PathParam("carBrandId") int carBrandId) throws DaoException, PriceException
	{
		logger.info(" ****.***** price ******.***** " );
		logger.info("service.CarModelRestServiceBean  carBrandId = " + carBrandId);
		carBrandEvent.fire(carBrandDictionary.getItem(carBrandId));
		return Response.status(200).entity(gson.toJson(carBrandDictionary.getItem(carBrandId))).build();
	}

	@GET
	@Path("/price")
	public Response getPrice() throws DaoException, PriceException
	{
		logger.info(" ****.***** price ******.***** " );
		Map<Integer, String> features = new HashMap<Integer, String>();
		features.put(1, "11500");
		features.put(8, "11");
		features.put(11, "1");
		features.put(9, "2");
		features.put(16, "1");
		features.put(7, "7");
		features.put(51, "1");
		features.put(6, "2014-09-09 16:35:54");
		logger.info(" ");
		return Response.status(200).entity(gson.toJson(priceCalculator.calculatePrice(features))).build();
	}

	@GET
	@Path("/driver/{driverId : \\d+}")
	public Response getDriver(@PathParam("driverId") int driverId) throws DaoException, CacheException
	{
		logger.info(" ****.***** getDriver ******.***** " );
		logger.info("service.CarModelRestServiceBean  driverId = " + driverId);
		return Response.status(200).entity(gson.toJson(driverService.find(driverId))).build();
	}

	@GET
	@Path("/feature/{featureId : \\d+}")
	public Response getFeature(@PathParam("featureId") int featureId) throws DaoException, CacheException
	{
		logger.info(" ****.***** getFeature ******.***** " );
		logger.info("service.CarModelRestServiceBean  featureId = " + featureId);
		return Response.status(200).entity(gson.toJson(featureDictionary.getItem(featureId))).build();
	}

	@GET
	@Path("/geoobject/{fragment}")
	public Response getGeoObject(@PathParam("fragment") String fragment) throws DaoException, GISException, SQLException
	{
		logger.info(" ****.***** getObject******.***** " );
		logger.info("service.CarModelRestServiceBean  fragment = " + fragment);
		List<GeoObject> geoObjects = objectFinder.getObjects(fragment);
		logger.info("service.CarModelRestServiceBean  size = " + geoObjects.size());
		return Response.status(200).entity(gson.toJson(objectFinder.getObjects(fragment))).build();
	}
}
