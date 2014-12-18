package ru.taxims.core.cache;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.AbstractEntity;
import ru.taxims.domain.exception.CacheException;

import javax.ejb.Lock;
import javax.ejb.LockType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 07.08.14.
 */
public class EntityCacheBean<T extends AbstractEntity> implements EntityCache<T>
{
	protected Map<Long , T> items = new HashMap<Long, T>();
	Logger logger = Logger.getLogger(EntityCacheBean.class);

	@Override
	public T getItem(long itemId) throws CacheException
	{
		if (!items.containsKey(itemId)) {
			logger.info("Entity not found! Items class " + items.getClass()
				+ " itemId " + itemId);
			return null;
		}
		return items.get(itemId);
	}

	@Lock(LockType.READ)
	@Override
	public Map<Long, T> getItems() throws CacheException
	{
		return items;
	}

	@Override
	public void putItem(T entity) throws CacheException
	{
		if (entity == null)
		{
			logger.error("Entity is null! Setting entity into cache was unsuccessful");
			throw new CacheException("Entity is null! Setting entity into cache was unsuccessful");
		}
	}

	@Override
	public void removeItem(long itemId) throws CacheException
	{
		if (!items.containsKey(itemId)) {
			logger.info("Entity not found! Items class " + items.getClass()
				+ " itemId " + itemId);
			throw new CacheException("Entity not found! Items class " + items.getClass()
				+ " itemId " + itemId);
		}
		items.remove(itemId);
	}
}

