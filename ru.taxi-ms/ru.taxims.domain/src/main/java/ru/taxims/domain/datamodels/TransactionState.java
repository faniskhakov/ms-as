package ru.taxims.domain.datamodels;

import java.util.List;

/**
 * Created by Developer_DB on 15.05.14.
 */

public class TransactionState extends AbstractDictionary
{
	//Идентификатор состояния трансакции
	//Название состояния трансакции

	List<Integer> availableStates; //Доступные следующие состояния

	public TransactionState()
	{
	}

	public TransactionState(int stateId)
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

	public boolean availableState(TransactionState state)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == state.getId())
				return true;
		}
		return false;
	}

	public boolean availableState(int state)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == state)
				return true;
		}
		return false;
	}
}
