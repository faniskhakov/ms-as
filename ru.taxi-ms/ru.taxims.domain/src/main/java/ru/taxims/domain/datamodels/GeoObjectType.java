package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class GeoObjectType extends AbstractDictionary
{
	int level;		//Место в иерархичской структуре типа объекта

	public GeoObjectType()
	{
	}

	public GeoObjectType(int geoObjectTypeId)
	{
		this.id = geoObjectTypeId;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
}
