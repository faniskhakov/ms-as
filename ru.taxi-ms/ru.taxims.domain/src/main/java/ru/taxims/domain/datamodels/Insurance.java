package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 27.05.14.
 */

public class Insurance extends AbstractEntity
{
	int insuranceId;		//Идентификатор страхового договора
	Date date_contract;		//Дата заключения договора
	InsuranceType insuranceType;		//Тип договора

	public Insurance()
	{
	}

	public int getInsuranceId()
	{
		return insuranceId;
	}

	public void setInsuranceId(int insuranceId)
	{
		this.insuranceId = insuranceId;
	}

	public Date getDate_contract()
	{
		return date_contract;
	}

	public void setDate_contract(Date date_contract)
	{
		this.date_contract = date_contract;
	}

	public InsuranceType getInsuranceType()
	{
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType)
	{
		this.insuranceType = insuranceType;
	}
}
