package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */


public class Point extends AbstractEntity
{
	float latitude; //Широта
	float longitude; //Долгота

	public Point (){}

	public Point (float latitude, float longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public float getLatitude()
	{
		return latitude;
	}

	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}

	public float getLongitude()
	{
		return longitude;
	}

	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}
}
