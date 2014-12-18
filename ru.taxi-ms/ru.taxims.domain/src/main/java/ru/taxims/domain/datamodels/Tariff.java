package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 04.06.14.
 */

public class Tariff extends AbstractDictionary
{
	//Идентификатор тарифа
	//Название тарифа
	long duration;		//Продолжительность действия тарифа в миллисекундах
	TariffRule rule;		//Идентификатор правила расчета
	boolean blockage;		//Блокировка тарифа

	public Tariff()
	{
	}

	public Tariff(int tariffId)
	{
		this.id = tariffId;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

	public TariffRule getRule()
	{
		return rule;
	}

	public void setRule(TariffRule rule)
	{
		this.rule = rule;
	}

	public boolean isBlockage()
	{
		return blockage;
	}

	public void setBlockage(boolean blockage)
	{
		this.blockage = blockage;
	}
}
