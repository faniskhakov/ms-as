package ru.taxims.domain.interfaces.dictionary;

import ru.taxims.domain.datamodels.AbstractDictionary;
import ru.taxims.domain.exception.DaoException;

import java.util.Map;

/**
 * Created by Developer_DB on 28.05.14.
 */

public interface Dictionary<T extends AbstractDictionary>
{
	T getItem(int itemId) throws DaoException;
	Map<Integer, T> getItems() throws DaoException;
}
