package ru.taxims.domain.interfaces.gis;

import ru.taxims.domain.datamodels.GeoAddress;
import ru.taxims.domain.datamodels.Point;
import ru.taxims.domain.exception.GISException;

/**
 * Created by Developer_DB on 26.08.14.
 */
public interface Geocoder
{
	public GeoAddress geocode(Point point) throws  GISException;
	public Point inverseGeocode(GeoAddress geoAddress) throws GISException;
}
