package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 30.05.14.
 */

public class Shift extends AbstractDictionary
{
	//Идентификатор смены
	//Название смены
	Agent agent;		//Идентификатор агента

	public Shift()
	{
	}

	public Shift(int shiftId)
	{
		this.id = shiftId;
	}

	public Agent getAgent()
	{
		return agent;
	}

	public void setAgent(Agent agent)
	{
		this.agent = agent;
	}

}
