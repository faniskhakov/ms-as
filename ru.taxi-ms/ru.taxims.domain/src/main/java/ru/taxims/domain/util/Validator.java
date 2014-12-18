package ru.taxims.domain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Developer_DB on 29.08.14.
 */
public class Validator
{
	public static boolean isNumeric(String str)
	{
		try
		{
			int numeric = Integer.parseInt(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public static boolean isValidDate(String str)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(str.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static boolean isFloat(String str)
	{
		try
		{
			float aFloat = Float.parseFloat(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
