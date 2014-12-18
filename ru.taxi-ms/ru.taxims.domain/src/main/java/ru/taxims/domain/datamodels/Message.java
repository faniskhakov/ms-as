package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class Message extends AbstractEntity
{
	long messageId;		//Идентификатор сообщения
	User sender;		//Идентификатор отправителя
	User addressee;		//Идентификатор получателя
	Date dateCreate = new Date();		//Время создания сообщения
	String messageText;		//Текст сообщения
	int priority;		//Приоритет сообщения
	MessageType messageType;		//Идентификатор типа сообщения.
	Date lifetime;		//Продолжительность жизни сообщения
	Order order;		//Идентификатор заказа

	public Message()
	{
	}

	public Message(long messageId)
	{
		this.messageId = messageId;
	}

	public long getMessageId()
	{
		return messageId;
	}

	public void setMessageId(long messageId)
	{
		this.messageId = messageId;
	}

	public User getSender()
	{
		return sender;
	}

	public void setSender(User sender)
	{
		this.sender = sender;
	}

	public User getAddressee()
	{
		return addressee;
	}

	public void setAddressee(User addressee)
	{
		this.addressee = addressee;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public String getMessageText()
	{
		return messageText;
	}

	public void setMessageText(String messageText)
	{
		this.messageText = messageText;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public void setMessageType(MessageType messageType)
	{
		this.messageType = messageType;
	}

	public Date getLifetime()
	{
		return lifetime;
	}

	public void setLifetime(Date lifetime)
	{
		this.lifetime = lifetime;
	}

	public Order getOrder()
	{
		return order;
	}

	public void setOrder(Order order)
	{
		this.order = order;
	}
}
