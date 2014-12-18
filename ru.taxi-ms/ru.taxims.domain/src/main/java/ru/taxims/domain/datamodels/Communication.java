package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 22.05.14.
 */

public class Communication extends AbstractEntity
{
	long userId;		//Идентификатор пользователя
	String number;		//Номер средства связи пользователя
	CommunicationType type;		//Тип средства связи
	Date dateCreate;		//Время создания записи в таблице
	long communicationId;       //Идентификатор средства связи
	boolean blockage;

	public Communication()
	{
	}

	public Communication(long communicationId)
	{
		this.communicationId = communicationId;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public CommunicationType getType()
	{
		return type;
	}

	public void setType(CommunicationType type)
	{
		this.type = type;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public long getCommunicationId()
	{
		return communicationId;
	}

	public void setCommunicationId(long communicationId)
	{
		this.communicationId = communicationId;
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
