package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 02.09.14.
 */


public class GeoObjectDistance
{
	int startSectionId;
	int endSectionId;
	int distance;

	public GeoObjectDistance()
	{
	}

	public int getStartSectionId()
	{
		return startSectionId;
	}

	public void setStartSectionId(int startSectionId)
	{
		this.startSectionId = startSectionId;
	}

	public int getEndSectionId()
	{
		return endSectionId;
	}

	public void setEndSectionId(int endSectionId)
	{
		this.endSectionId = endSectionId;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}
}
