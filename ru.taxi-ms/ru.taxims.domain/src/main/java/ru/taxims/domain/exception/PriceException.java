package ru.taxims.domain.exception;

/**
 * Created by Developer_DB on 05.09.14.
 */
public class PriceException extends TaxiMsException
{
	//private static final long serialVersionUID = 6308361853511245691L;

	public PriceException()
	{
		super();
	}
	public PriceException(String msg)
	{
		super(msg);
	}
	public PriceException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
	public PriceException(Throwable cause)
	{
		super(cause);
	}
}
