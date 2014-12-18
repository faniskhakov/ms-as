package ru.taxims.dao.main;

import ru.taxims.domain.datamodels.AbstractEntity;
import ru.taxims.domain.interfaces.dao.Dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

public abstract class AbstractDaoBean<E extends AbstractEntity> implements	Dao<E>
{
	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
}
