package ru.taxims.domain.exception;

/**
 * Created by Developer_DB on 26.08.14.
 */
public class GISException extends TaxiMsException
{
	//private static final long serialVersionUID = 6308361853511245691L;

	public GISException()
	{
		super();
	}
	public GISException(String msg)
	{
		super(msg);
	}
	public GISException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	public GISException(Throwable cause)
	{
		super(cause);
	}
}