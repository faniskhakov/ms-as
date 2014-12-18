package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 05.06.14.
 */

public class TariffRule extends AbstractDictionary
{
	//Идентификатор правила расчета тарифа
	//Название правила расчета тарифа

	public TariffRule()
	{
	}

	public TariffRule(int ruleId)
	{
		this.id = ruleId;
	}

}
