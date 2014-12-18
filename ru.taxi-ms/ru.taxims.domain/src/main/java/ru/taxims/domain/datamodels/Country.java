package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class Country extends AbstractDictionary
{
	//Идентификатор страны
	//Название страны
	Language language; //Язык по умолчанию в стране

	public Country()
	{
	}

	public Country(int countryId)
	{
		this.id = countryId;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage(Language language)
	{
		this.language = language;
	}
}
