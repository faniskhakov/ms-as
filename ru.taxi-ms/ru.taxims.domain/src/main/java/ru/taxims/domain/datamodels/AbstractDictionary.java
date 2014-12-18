package ru.taxims.domain.datamodels;

import java.io.Serializable;

/**
 * Created by Developer_DB on 25.07.14.
 */

public abstract class AbstractDictionary implements Serializable
{
	private static final long serialVersionUID = 1L;

	int id;		//Идентификатор
	String name;		//Название

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
