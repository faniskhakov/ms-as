package ru.taxims.domain.datamodels;

import java.util.List;

/**
 * Created by Developer_DB on 13.05.14.
 */
public class Feature extends AbstractDictionary
{
	//Идентификатор особенности тарифа
	//Название особенности
	FeatureRule rule;		//Идентификатор правила рассчета особенности
	String unit;            //Единица измерения
	boolean enabled;		//Состояние особенности (актуальность)
	boolean autoCalc;		//Автоматический расчет особенности
	boolean bindCar;		//Индикатор зависимости особенности заказа от особенности автомобиля
	boolean bindDriver;		//Индикатор зависимости особенности заказа от особенности водителя
	int sequence;           //Порядковый номер расчета особенности
	float minimumCost;      //Минимальная цена за особенность
	FeatureComparisonRule comparisonRule; //Идентификатор правила сравнения особенности
	List<FeatureCalculationElement> calculationElements; //'Элементы расчета стоимости

	public Feature()
	{
	}

	public Feature(int featureId)
	{
		this.id = featureId;
	}

	public FeatureRule getRule()
	{
		return rule;
	}

	public void setRule(FeatureRule rule)
	{
		this.rule = rule;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public boolean isEnebled()
	{
		return enabled;
	}

	public void setEnebled(boolean enebled)
	{
		this.enabled = enebled;
	}

	public boolean isAutoCalc()
	{
		return autoCalc;
	}

	public void setAutoCalc(boolean autoCalc)
	{
		this.autoCalc = autoCalc;
	}

	public boolean isBindCar()
	{
		return bindCar;
	}

	public void setBindCar(boolean bindCar)
	{
		this.bindCar = bindCar;
	}

	public boolean isBindDriver()
	{
		return bindDriver;
	}

	public void setBindDriver(boolean bindDriver)
	{
		this.bindDriver = bindDriver;
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		this.sequence = sequence;
	}

	public FeatureComparisonRule getComparisonRule()
	{
		return comparisonRule;
	}

	public void setComparisonRule(FeatureComparisonRule comparisonRule)
	{
		this.comparisonRule = comparisonRule;
	}

	public float getMinimumCost()
	{
		return minimumCost;
	}

	public void setMinimumCost(float minimumCost)
	{
		this.minimumCost = minimumCost;
	}

	public List<FeatureCalculationElement> getCalculationElements()
	{
		return calculationElements;
	}

	public void setCalculationElements(List<FeatureCalculationElement> calculationElements)
	{
		this.calculationElements = calculationElements;
	}
}

