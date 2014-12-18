package ru.taxims.gis.geocoder;

import ru.taxims.domain.datamodels.GeoAddress;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.GISException;
import ru.taxims.domain.interfaces.gis.Geocoder;
import ru.taxims.domain.interfaces.gis.GeocoderMain;

import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * Created by Developer_DB on 02.09.14.
 */
@GeocoderMain
@Stateless
@Local
public class GeocoderBean implements Geocoder
{
	@Override
	public GeoAddress geocode(Point point) throws GISException
	{
		return null;
	}

	@Override
	public Point inverseGeocode(GeoAddress geoAddress) throws GISException
	{
		return null;
	}
}
