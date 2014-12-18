package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 04.09.14.
 */
public class FeatureCalculationElement extends AbstractEntity
{
	int sequence;    //Порядковый номер элеметнта расчета стоимости для конкретной особенности
	float value;	 //Значение особенности (процент, надбавка)
	float min;		 //Ограничение (минимальное значение)
	float max;		 //Ограничение (максимальное значение)
	String name;	 //Название элемента
	int group;		 //Группа элемента (используется например для разделения схемы расчета надбавки за поток на )

	public FeatureCalculationElement()
	{
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		this.sequence = sequence;
	}

	public float getValue()
	{
		return value;
	}

	public void setValue(float value)
	{
		this.value = value;
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getGroup()
	{
		return group;
	}

	public void setGroup(int group)
	{
		this.group = group;
	}
}
