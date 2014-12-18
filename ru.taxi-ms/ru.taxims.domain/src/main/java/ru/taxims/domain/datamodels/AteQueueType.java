package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class AteQueueType extends AbstractEntity
{
	String guid;
	RoleType roleType;		//Идентификаотр роли
	Agent agent;		//Идентификатор агента

	public AteQueueType()
	{
	}

	public String getGuid()
	{
		return guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public RoleType getRoleType()
	{
		return roleType;
	}

	public void setRoleType(RoleType roleType)
	{
		this.roleType = roleType;
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
