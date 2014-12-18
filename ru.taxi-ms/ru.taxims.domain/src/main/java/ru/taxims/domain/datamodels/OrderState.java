package ru.taxims.domain.datamodels;

import java.util.List;


public class OrderState extends AbstractDictionary
{
	//Идентификатор состояния
	//Название состояния

	List<Integer> availableStates; //Доступные следующие состояния

	public  OrderState() {}

	public OrderState(int stateId)
	{
		this.id = stateId;
	}


	public List<Integer> getAvailableStates()
	{
		return availableStates;
	}

	public void setAvailableStates(List<Integer> availableStates)
	{
		this.availableStates = availableStates;
	}

	public boolean availableState(OrderState orderState)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == orderState.getId())
				return true;
		}
		return false;
	}

	public boolean availableState(int orderState)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == orderState)
				return true;
		}
		return false;
	}

	public boolean isOrderFree()
	{
		return this.id == 1;
	}

	public boolean isDriverInWay()
	{
		return this.id == 3;
	}

	public boolean isDriverInPlace()
	{
		return this.id == 4;
	}

	public boolean isDriverWithClient()
	{
		return this.id == 5;
	}

	public boolean isOrderExecuted()
	{
		return this.id == 6;
	}

	public boolean isOrderCanceled()
	{
		return this.id == 7;
	}

	public boolean isFinalState()
	{
		return this.id == 6 || this.id == 7;
	}
}

