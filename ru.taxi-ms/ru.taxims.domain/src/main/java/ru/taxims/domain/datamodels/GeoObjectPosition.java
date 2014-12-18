package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 18.09.14.
 */


public class GeoObjectPosition extends Point
{
	int sectionId;

	public GeoObjectPosition(){}

	public GeoObjectPosition(Point point){
		super(point.latitude, point.longitude);
	}

	public GeoObjectPosition(float latitude, float longitude){
		super(latitude, longitude);
	}

	public int getSectionId()
	{
		return sectionId;
	}

	public void setSectionId(int sectionId)
	{
		this.sectionId = sectionId;
	}
}
