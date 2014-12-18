package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */


public class Language extends AbstractDictionary
{
	//Идентификатор языка
	//Название языка

	public Language()
	{
	}

	public Language(int languageId)
	{
		this.id = languageId;
	}

	public boolean isEmpty()
	{
		return !(this.id > 0);
	}
}
