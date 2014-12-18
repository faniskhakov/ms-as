package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 24.11.14.
 */
public class OrderDistribution
{
	long driverId;
	OrderDistributionState distributionState;
	Date createdDate;

	public OrderDistribution(){}

	public OrderDistribution(long driverId)
	{
		this.driverId = driverId;
		this.createdDate = new Date();
	}

	public long getDriverId()
	{
		return driverId;
	}

	public void setDriverId(long driverId)
	{
		this.driverId = driverId;
	}

	public OrderDistributionState getDistributionState()
	{
		return distributionState;
	}

	public void setDistributionState(OrderDistributionState distributionState)
	{
		this.distributionState = distributionState;
	}

	public Date getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

}
