package ru.taxims.domain.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class TaxiMsException extends Exception
{
	private static final long serialVersionUID = -8699378355001745L;

	public TaxiMsException()
	{
		super();
	}
	public TaxiMsException(String msg)
	{
		super(msg);
	}
	public TaxiMsException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	public TaxiMsException(Throwable cause)
	{
		super(cause);
	}
}
