package ru.taxims.price.selector;

import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.exception.PriceException;

/**
 * Created by Developer_DB on 25.09.14.
 */
public interface FunctionSelector
{
	public float getCost(Feature feature, String value, String mainParameter) throws PriceException;
}
