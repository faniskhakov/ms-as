package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.Communication;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 03.06.14.
 */
@Local
public interface CommunicationDao extends Dao<Communication>
{
	Communication findCommunication(String number) throws DaoException;
	List<Communication> findCommunications(String sql) throws DaoException;
	void disable(long communicationId) throws DaoException;
	void enable(long communicationId) throws DaoException;
}
