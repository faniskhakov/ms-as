package ru.taxims.domain.datamodels;

public class City extends AbstractDictionary
{
	//Идентификатор	города
	//Название	города
	Country country; //Идентификатор страны

	public City() {}

	public City(int cityId)
	{
		this.id = cityId;
	}

	public Country getCountry()
	{
		return country;
	}

	public void setCountry(Country country)
	{
		this.country = country;
	}

	public boolean isEmpty()
	{
		return !(this.id > 0);
	}

}
