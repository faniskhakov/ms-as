package ru.taxims.domain.datamodels;

public class CarModel extends AbstractDictionary
{
	//Идентификатор модели
	CarBrand brand;		//Марка автомобиля
	//Название модели автомобиля
	//	class_id;		//Класс автомобиля

	public CarModel()
	{
	}

	public CarModel(int modelId)
	{
		this.id = modelId;
	}

	public CarBrand getBrand()
	{
		return brand;
	}

	public void setBrand(CarBrand brand)
	{
		this.brand = brand;
	}

}
