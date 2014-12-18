package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 13.05.14.
 */


public class RoleType extends AbstractDictionary
{
	//Идентификатор типа роли
	//Название роли
	Date dateCreate;    //Время создания роли

	public RoleType()
	{
	}

	public RoleType(int roleTypeId)
	{
		this.id = roleTypeId;
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
