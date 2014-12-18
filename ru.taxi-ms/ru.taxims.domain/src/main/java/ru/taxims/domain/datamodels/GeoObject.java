package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 13.05.14.
 */

public class GeoObject extends AbstractDictionary
{
	String address;  //Почтовый адрес
	GeoObjectPosition position;		//Идентификатор географического положения
	GeoObjectType type;		//Идентификатор типа объекта
	GeoObjectDescription description;		//Идентификатор класса объекта или организации
	GeoObject parent;		//Идентификаторы объекта-родителя ??????

	public GeoObject()
	{
	}

	public GeoObject getParent()
	{
		return parent;
	}

	public void setParent(GeoObject parent)
	{
		this.parent = parent;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public GeoObjectType getType()
	{
		return type;
	}

	public void setType(GeoObjectType type)
	{
		this.type = type;
	}

	public GeoObjectPosition getPosition()
	{
		return position;
	}

	public void setPosition(GeoObjectPosition position)
	{
		this.position = position;
	}

	public GeoObjectDescription getDescription()
	{
		return description;
	}

	public void setDescription(GeoObjectDescription description)
	{
		this.description = description;
	}

	@Override
	public String toString()
	{
		return "GeoObject{" +
			"objectId=" + id +
			", parentId=" + parent.getName() +
			", name='" + name + '\'' +
			", address='" + address + '\'' +
			", type=" + type +
			", point=" + position +
			", description=" + description +
			'}';
	}
}
