package ru.taxims.domain.interfaces.dictionary;

import ru.taxims.domain.exception.DaoException;

/**
 * Created by Developer_DB on 03.09.14.
 */
public interface DistanceCalculator
{
	int getDistance(int startSectionId, int endSectionId) throws DaoException;
}
