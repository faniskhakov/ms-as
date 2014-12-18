package ru.taxims.domain.datamodels;

import java.util.List;


public class DriverState extends AbstractDictionary
{
	//Идентификатор состояния
	//Название состоянии
	List<Integer> availableStates; //Доступные следующие состояния

	public DriverState()
	{
	}

	public DriverState(int stateId)
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

	public boolean availableState(DriverState driverState)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == driverState.getId())
				return true;
		}
		return false;
	}

	public boolean availableState(int driverState)
	{
		for (Integer stateId : this.availableStates)
		{
			if (stateId == driverState)
				return true;
		}
		return false;
	}

	public boolean isDriverFree()
	{
		return this.id == 1;
	}

	public boolean isDriverBusy()
	{
		return this.id == 2;
	}

	public boolean isDriverEnabled()
	{
		return this.id != 3;
	}

	public boolean isDriverDisabled()
	{
		return this.id == 3;
	}
}
