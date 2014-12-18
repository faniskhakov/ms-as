package ru.taxims.domain.datamodels;

import java.util.Map;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class District extends AbstractDictionary
{
	//Идентификатор района(полигона)
	//Название района
	Map<Integer, Point> points;		//ГеоТочки(Широта, Долгота)
	City city;          //Идентификатор города

	public District()
	{
	}

	public District(int districtId)
	{
		this.id = districtId;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public Map<Integer, Point> getPoints()
	{
		return points;
	}

	public void setPoints(Map<Integer, Point> points)
	{
		this.points = points;
	}
}
