package ru.taxims.domain.util;

/**
 * Created by Developer_DB on 29.08.14.
 */
public class Modificator
{
	public static String fragmentModificator(String fragment, int type)
	{
		switch (type) {
		case 1:
			fragment = fragment.replace("улица", "");
			fragment = fragment.replace("ул ", "");
			fragment = fragment.replace("ул.", "");
			fragment = fragment.replace("остановка ", "");
			fragment = fragment.replace("ост ", "");
			fragment = fragment.replace("ост.", "");
			break;
		case 2:
			fragment = fragment.replace("дом", "");
			fragment = fragment.replace("д.", "");
			fragment = fragment.replace("д ", "");
			fragment = fragment.replace("кв", "");
			break;
		default:
			break;
		}
		return fragment.trim();
	}
}
