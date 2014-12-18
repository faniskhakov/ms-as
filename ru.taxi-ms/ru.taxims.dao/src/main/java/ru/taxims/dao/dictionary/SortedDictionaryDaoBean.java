package ru.taxims.dao.dictionary;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.AbstractDictionary;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Developer_DB on 09.09.14.
 */
public class SortedDictionaryDaoBean<T extends AbstractDictionary> implements Dictionary<T>
{
	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;
	protected Logger logger = Logger.getLogger(DictionaryDaoBean.class);
	protected Map<Integer , T> items = new LinkedHashMap<Integer, T>();

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	@Lock(LockType.READ)
	@Override
	public T getItem(int itemId) throws DaoException
	{
		if (!items.containsKey(itemId)) {
			throw new DaoException("!!!");
		}
		return items.get(itemId);
	}

	@Lock(LockType.READ)
	@Override
	public Map<Integer, T> getItems() throws DaoException
	{
		return items;
	}


}
