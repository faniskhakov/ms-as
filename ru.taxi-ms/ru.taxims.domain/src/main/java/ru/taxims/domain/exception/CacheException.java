package ru.taxims.domain.exception;

import javax.ejb.ApplicationException;

/**
 * Created by Developer_DB on 07.08.14.
 */
@ApplicationException(rollback = true)
public class CacheException extends TaxiMsException
{
	//private static final long serialVersionUID = -8699378555001745L;

	public CacheException()
	{
		super();
	}
	public CacheException(String msg)
	{
		super(msg);
	}
	public CacheException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	public CacheException(Throwable cause)
	{
		super(cause);
	}
}
