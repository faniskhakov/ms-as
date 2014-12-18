package ru.taxims.domain.interfaces.dao;

import ru.taxims.domain.datamodels.Message;
import ru.taxims.domain.exception.DaoException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Developer_DB on 30.05.14.
 */
@Local
public interface MessageDao extends Dao<Message>
{
	List<Message> findOrderMessages(long orderId) throws DaoException;
	List<Message> findMessages(String sql) throws DaoException;
}
