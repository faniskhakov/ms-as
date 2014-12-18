package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 11.09.14.
 */

public class OrderDistributionState extends AbstractDictionary
{
	//Идентификатор состояния
	//Название состояния

	public boolean isOrderOffered()
	{
		return this.id == 1;
	}

	public boolean isOrderAssigned()
	{
		return this.id == 2;
	}

	public boolean isOrderRefused()
	{
		return this.id == 3;
	}
}