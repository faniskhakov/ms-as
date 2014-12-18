package ru.taxims.domain.interfaces.price;

import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.PriceException;

import java.util.Map;

/**
 * Created by Developer_DB on 03.09.14.
 */
public interface PriceCalculator
{
	public float calculatePrice(Map<Integer, String> features) throws DaoException, PriceException;
	public float calculatePriceAlternative(Map<Integer, String> features) throws DaoException, PriceException;
}
