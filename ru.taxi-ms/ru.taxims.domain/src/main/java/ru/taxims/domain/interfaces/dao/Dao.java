package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.AbstractEntity;
import ru.taxims.domain.exception.DaoException;

public interface Dao<E extends AbstractEntity> {

	boolean verify(E instance) throws DaoException;
	long persist(E instance) throws DaoException;
	E find(long id) throws DaoException;
}
