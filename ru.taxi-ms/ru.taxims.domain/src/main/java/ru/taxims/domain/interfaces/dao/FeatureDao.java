package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by Developer_DB on 13.11.14.
 */
@Local
public interface FeatureDao
{
	Map<Integer, String> find(long entityId, int roleTypeId, Connection connection) throws DaoException;
	void persist(long id, Map<Integer, String> features, int roleTypeId) throws DaoException;
	Map<Integer, String> find(long id, int roleTypeId) throws DaoException;
	void enable(long id, Map<Integer, String> features, int roleTypeId) throws DaoException;
	void disable(long id, Map<Integer, String> features, int roleTypeId) throws DaoException;
}
