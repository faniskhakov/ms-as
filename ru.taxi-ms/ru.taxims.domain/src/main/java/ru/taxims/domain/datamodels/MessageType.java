package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */


public class MessageType extends AbstractDictionary
{
	//Идентификатор типа сообщения
	//Название типа сообщения

	public MessageType()
	{
	}

	public MessageType(int messageTypeId)
	{
		this.id = messageTypeId;
	}

}
