package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class Color extends AbstractDictionary
{
	//Идентификатор цвета
	//Название цвета
	String englishName;		//Название цвета на английском

	public Color()
	{
	}

	public Color(int colorId)
	{
		this.id = colorId;
	}

	public String getEnglishName()
	{
		return englishName;
	}

	public void setEnglishName(String englishName)
	{
		this.englishName = englishName;
	}
}
