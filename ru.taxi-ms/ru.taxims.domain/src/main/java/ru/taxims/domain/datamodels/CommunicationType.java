package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class CommunicationType extends AbstractDictionary
{
	//Идетификатор типа связи
	//Название типа связи

	public CommunicationType()
	{
	}

	public CommunicationType(int communication_type_id)
	{
		this.id = communication_type_id;
	}

}
