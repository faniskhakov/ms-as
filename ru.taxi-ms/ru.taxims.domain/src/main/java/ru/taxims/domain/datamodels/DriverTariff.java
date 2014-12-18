package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 05.06.14.
 */
public class DriverTariff extends AbstractEntity
{
	long driverTariffId;		//Идентификатор тарифа водителя
	Tariff tariff;		//Идентификатор тарифа
	Date startDate;		//Время начала активации тарифа
	Date endDate;		//Время истечения действия тарифа

	public DriverTariff()
	{
	}

	public DriverTariff(long driverTariffId)
	{
		this.driverTariffId = driverTariffId;
	}

	public long getDriverTariffId()
	{
		return driverTariffId;
	}

	public void setDriverTariffId(long driverTariffId)
	{
		this.driverTariffId = driverTariffId;
	}

	public Tariff getTariff()
	{
		return tariff;
	}

	public void setTariff(Tariff tariff)
	{
		this.tariff = tariff;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
}
