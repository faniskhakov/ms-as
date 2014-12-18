package ru.taxims.price.selector;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.datamodels.FeatureCalculationElement;
import ru.taxims.domain.exception.PriceException;
import ru.taxims.domain.util.Validator;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Developer_DB on 25.09.14.
 */
@Stateless
@Local
public class FunctionSelectorBean implements FunctionSelector
{
	Logger logger = Logger.getLogger(FunctionSelectorBean.class);

	@Override
	public float getCost(Feature feature, String featureValue, String mainParameter) throws PriceException
	{
		float cost;
		switch (feature.getRule().getId()) {
			case 1: cost = proportionalFunction(feature, featureValue); break;
			case 2: cost = stepProportionalFunction(feature, featureValue); break;
			case 3: cost = constantFunction(feature, featureValue); break;
			case 4: cost = floodProportionalFunction(feature, featureValue, mainParameter); break;
			default: cost = 0; break;
		}
		return cost;
	}

	private float stepProportionalFunction(Feature feature, String value) throws PriceException
	{
		float cost = 0;
		if (!Validator.isNumeric(value)) throw new PriceException();
		for(FeatureCalculationElement element: feature.getCalculationElements()){
			logger.info("CalculationElement  Name = " + element.getName() + " ; FeatureId = " + feature.getId() + " ; Sequence = " + element.getSequence()
				+ " Value " + element.getValue() + " Min " + element.getMin() + " Max " + element.getMax() + " Cost =" + cost);
			if (element.getMax() != 0 && element.getMax() <= Integer.parseInt(value)){
				cost += element.getValue()*(element.getMax() - element.getMin());
				continue;
			}
			if (element.getMin() != 0 && element.getMin() > Integer.parseInt(value)){
				continue;
			}
			cost += element.getValue() * (Integer.parseInt(value) - element.getMin());
			logger.info("CalculationElement  Cost = " + cost);
		}
		return cost;
	}

	private float proportionalFunction(Feature feature, String value) throws PriceException
	{
		float cost = 0;
		if (!Validator.isNumeric(value)) throw new PriceException();
		for(FeatureCalculationElement element: feature.getCalculationElements()){
			if (element.getMax() != 0 && element.getMax() <= Integer.parseInt(value)){
				continue;
			}
			if (element.getMin() != 0 && element.getMin() > Integer.parseInt(value)){
				continue;
			}
			cost = element.getValue() * Integer.parseInt(value);
		}
		return cost;
	}

	private float constantFunction(Feature feature, String value) throws PriceException
	{
		float cost = 0;
		if (!Validator.isNumeric(value)) throw new PriceException();
		for(FeatureCalculationElement element: feature.getCalculationElements()){
			if (element.getMax() != 0 && element.getMax() <= Integer.parseInt(value)){
				continue;
			}
			if (element.getMin() != 0 && element.getMin() > Integer.parseInt(value)){
				continue;
			}
			cost = element.getValue();
		}
		return cost;
	}

	private float floodProportionalFunction(Feature feature, String value, String distance) throws PriceException
	{
		float cost = 0;
		if (!Validator.isNumeric(distance)) throw new PriceException();
		if (!Validator.isValidDate(value)) throw new PriceException();
		Calendar calendar = GregorianCalendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.GERMANY);
		try {
			calendar.setTime(sdf.parse(value.trim()));
		}catch (ParseException ex){
			throw new PriceException();
		}
		int group;
		switch (calendar.get(Calendar.DAY_OF_WEEK)){
			case 1 - 4: group = 1; break;
			case 5: group = 3; break;
			case 6 - 7: group = 2; break;
			default: group = 1; break;
		}
		int minutes = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
		for(FeatureCalculationElement element: feature.getCalculationElements()){
			logger.info("CalculationElement floodProportionalFunction  Name = " + element.getName() + " ; FeatureId = " + feature.getId() + " ; Sequence = " + element.getSequence()
				+ " Value " + element.getValue() + " Min " + element.getMin() + " Max " + element.getMax() + " Cost =" + cost);
			if (element.getGroup() != group) {
				continue;
			}
			if (element.getMax() != 0 && element.getMax() <= minutes) {
				continue;
			}
			if (element.getMin() != 0 && element.getMin() > minutes) {
				continue;
			}
			cost = element.getValue()*Integer.parseInt(distance);
			logger.info("CalculationElement  Cost = " + cost);
		}
		return cost;
	}
}
