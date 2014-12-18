package ru.taxims.domain.datamodels;

import java.util.Map;

/**
 * Created by Developer_DB on 13.05.14.
 */
public class Car extends AbstractEntity
{
	int carId;		//Идентификатор автомобиля
	long userId;		//Владелец автомобиля
	Color color;		//Цвет автомобиля
	CarModel model;		//Модель автомобиля
	String number;		//Государственный номер
	Insurance insurance;		//Данные по страховке
	boolean blockage;		//Блокировка автомобиля
	CarBodyType bodyType;		//Тип кузова
	int width;		//Ширина грузового отсека
	int height;		//Высота грузового отсека
	int length;		//Длина грузового отсека
	int year;		//Год выпуска
	Map<Integer, String> features; //Особенности автомобиля

	public Car()
	{
	}

	public Car(int carId)
	{
		this.carId = carId;
	}

	public int getCarId()
	{
		return carId;
	}

	public void setCarId(int carId)
	{
		this.carId = carId;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public CarModel getModel()
	{
		return model;
	}

	public void setModel(CarModel model)
	{
		this.model = model;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public boolean isBlockage()
	{
		return blockage;
	}

	public void setBlockage(boolean blockage)
	{
		this.blockage = blockage;
	}

	public CarBodyType getBodyType()
	{
		return bodyType;
	}

	public void setBodyType(CarBodyType bodyType)
	{
		this.bodyType = bodyType;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public Insurance getInsurance()
	{
		return insurance;
	}

	public void setInsurance(Insurance insurance)
	{
		this.insurance = insurance;
	}

	public Map<Integer, String> getFeatures()
	{
		return features;
	}

	public void setFeatures(Map<Integer, String> features)
	{
		this.features = features;
	}
}


