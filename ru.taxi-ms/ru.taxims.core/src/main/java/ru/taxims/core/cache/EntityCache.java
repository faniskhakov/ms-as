package ru.taxims.core.cache;

import ru.taxims.domain.datamodels.AbstractEntity;
import ru.taxims.domain.exception.CacheException;

import java.util.Map;

/**
 * Created by Developer_DB on 07.08.14.
 */

public interface EntityCache<T extends AbstractEntity>
{
	T getItem(long itemId) throws CacheException;
	Map<Long, T> getItems() throws CacheException;
	void putItem(T entity) throws CacheException;
	void removeItem(long itemId) throws CacheException;
}
