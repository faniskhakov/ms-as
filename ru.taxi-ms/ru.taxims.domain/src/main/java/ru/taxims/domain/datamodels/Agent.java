package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */
public class Agent extends AbstractDictionary
{
	City city;		//Идентификатор города

	public Agent(){}

	public Agent(int agentId)
	{
		this.id = agentId;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}
}
