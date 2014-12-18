package ru.taxims.domain.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class DaoException extends TaxiMsException
{
	//private static final long serialVersionUID = 6308361853511245691L;

	public DaoException()
	{
		super();
	}
	public DaoException(String msg)
	{
		super(msg);
	}
	public DaoException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	public DaoException(Throwable cause)
	{
		super(cause);
	}
}
