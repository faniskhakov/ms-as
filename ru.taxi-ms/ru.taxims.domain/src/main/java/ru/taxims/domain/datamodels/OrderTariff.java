package ru.taxims.domain.datamodels;

import java.util.Map;

/**
 * Created by Developer_DB on 25.09.14.
 */


public class OrderTariff extends AbstractDictionary
{
	//Идентификатор тарифа
	//Название тарифа
	boolean blockage;		//Блокировка тарифа
	Map<Integer, Feature> features;

	public OrderTariff()
	{
	}

	public OrderTariff(int orderTariffId){
		this.id = orderTariffId;
	}

	public boolean isBlockage()
	{
		return blockage;
	}

	public void setBlockage(boolean blockage)
	{
		this.blockage = blockage;
	}

	public Map<Integer, Feature> getFeatures()
	{
		return features;
	}

	public void setFeatures(Map<Integer, Feature> features)
	{
		this.features = features;
	}
}
