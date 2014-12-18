package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 13.05.14.
 */
public class DriverContract extends AbstractEntity
{
	int contractId;		//Идентификатор договора
	int driverId;		//Идентификатор водителя
	Date dateCreate;		//Дата заключения договора

	public DriverContract()
	{
	}

	public DriverContract(int contractId)
	{
		this.contractId = contractId;
	}

	public int getContractId()
	{
		return contractId;
	}

	public void setContractId(int contractId)
	{
		this.contractId = contractId;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}
}
