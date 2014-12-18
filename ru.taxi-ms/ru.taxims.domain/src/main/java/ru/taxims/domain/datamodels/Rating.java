package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 15.05.14.
 */


public class Rating extends AbstractDictionary
{
	//Идентификатор варианта изменения рейтинга
	//Название варианта изменения рейтинга
	float value;		//Значение варианта изменения рейтинга
	RatingRule rule;		//Идентификатор правила рассчета рейтинга
	float min;		//Ограничение (минимальное значение)
	float max;		//Ограничение (максимальное значение)
	int group;		//Группа однородных вариантов
	boolean active;		//Состояние (актуальность)
	boolean bindOperator;		//Рейтинг оператора
	boolean bindDriver;		//Рейтинг водителя

	public Rating()
	{
	}

	public Rating(int ratingId)
	{
		this.id = ratingId;
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		this.value = value;
	}

	public RatingRule getRule()
	{
		return rule;
	}

	public void setRule(RatingRule rule)
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

	public int getGroup()
	{
		return group;
	}

	public void setGroup(int group)
	{
		this.group = group;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isBindOperator()
	{
		return bindOperator;
	}

	public void setBindOperator(boolean bindOperator)
	{
		this.bindOperator = bindOperator;
	}

	public boolean isBindDriver()
	{
		return bindDriver;
	}

	public void setBindDriver(boolean bindDriver)
	{
		this.bindDriver = bindDriver;
	}
}

