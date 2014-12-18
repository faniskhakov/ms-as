package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 15.05.14.
 */


public class Priority extends AbstractDictionary
{
	//Идентификатор приоритета
	//Название приоритета
	float value;		//Значение приоритета
	PriorityRule rule;		//Идентификатор правила рассчета приоритета
	float min;		//Ограничение (минимальное значение)
	float max;		//Ограничение (максимальное значение)
	boolean active;		//Состояние особенности (актуальность)
	boolean bindDriver;		//Индикатор приоритета водителя
	boolean bindOrder;		//Индикатор приоритета заказа

	public Priority()
	{
	}

	public Priority(int priorityId)
	{
		this.id = priorityId;
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		this.value = value;
	}

	public PriorityRule getRule()
	{
		return rule;
	}

	public void setRule(PriorityRule rule)
	{
		this.rule = rule;
	}

	public float getMin()
	{
		return min;
	}

	public void setMin(float min)
	{
		this.min = min;
	}

	public float getMax()
	{
		return max;
	}

	public void setMax(float max)
	{
		this.max = max;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isBindDriver()
	{
		return bindDriver;
	}

	public void setBindDriver(boolean bindDriver)
	{
		this.bindDriver = bindDriver;
	}

	public boolean isBindOrder()
	{
		return bindOrder;
	}

	public void setBindOrder(boolean bindOrder)
	{
		this.bindOrder = bindOrder;
	}
}

