package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 15.05.14.
 */

public class Transaction extends AbstractEntity
{
	long transactionId;		//Идентификатор трансакции
	long sourceAccountId;		//Идентификатор счета отправителя
	long destinationAccountId;		//Идентификатор счета получателя
	Date dateCreate;		//Время трансакции
	float amount;		//Сумма трансакции
	TransactionState state;		//Идентификатор состояния трансакции
	long orderId;		//Идентификатор заказа

	public Transaction()
	{
	}

	public Transaction(long transactionId)
	{
		this.transactionId = transactionId;
	}

	public long getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(long transactionId)
	{
		this.transactionId = transactionId;
	}

	public long getSourceAccountId()
	{
		return sourceAccountId;
	}

	public void setSourceAccountId(long sourceAccountId)
	{
		this.sourceAccountId = sourceAccountId;
	}

	public long getDestinationAccountId()
	{
		return destinationAccountId;
	}

	public void setDestinationAccountId(long destinationAccountId)
	{
		this.destinationAccountId = destinationAccountId;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	public TransactionState getState()
	{
		return state;
	}

	public void setState(TransactionState state)
	{
		this.state = state;
	}

	public long getOrderId()
	{
		return orderId;
	}

	public void setOrderId(long orderId)
	{
		this.orderId = orderId;
	}
}

