package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 21.05.14.
 */

public class Source extends AbstractDictionary
{
	//Идентификатор источника информации
	//Название источника

	public Source()
	{
	}

	public Source(int sourceId)
	{
		this.id = sourceId;
	}

	public boolean isEmpty()
	{
		return this.id <= 0;
	}
}
