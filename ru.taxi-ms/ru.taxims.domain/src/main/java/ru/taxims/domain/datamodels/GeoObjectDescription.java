package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */


public class GeoObjectDescription extends AbstractDictionary
{

	String description;		//Описание

	public GeoObjectDescription()
	{
	}

	public GeoObjectDescription(int descriptionId)
	{
		this.id = descriptionId;
	}

	public GeoObjectDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
