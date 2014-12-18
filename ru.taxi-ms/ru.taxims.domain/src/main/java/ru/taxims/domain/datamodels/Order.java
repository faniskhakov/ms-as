/**
 * Created by Developer_DB on 13.05.14.
 */
package ru.taxims.domain.datamodels;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class Order extends AbstractEntity
{
	long orderId; //Идентификатор заказа
	User user;  //Идентификатор клиента
	Driver driver; //Идентификатор исполнителя (водителя)
	User creator; //Идентификатор источника формирования заказа
	City city; //Идентификатор города
	OrderState state; //Статус заказа
	Date dateCreate = new Date(); //Дата создания заказа
	Date dateStart; //Дата начала исполнения заказа
	String phone; //Телефон заказавшего
	String phoneClient; //Телефон клиента
	int priority; //Приоритет заказа
	float amount; //Сумма заказа
	String comment; //Комментарий к заказу
	List<OrderDistribution> distributions; //Отказавшиеся водители
	Map<Integer, String> features; //Особенности заказа
	Map<Integer, Order> parents; //Идентификаторы заказа-родителя TODO:
	Map<Integer, GeoObject> waypoints; //ГеоТочки маршрута(Широта, Долгота)

	public Order() {}

	public Order(long orderId)
	{
		this.orderId = orderId;
	}

	public long getOrderId()
	{
		return orderId;
	}

	public void setOrderId(long orderId)
	{
		this.orderId = orderId;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Driver getDriver()
	{
		return driver;
	}

	public void setDriver(Driver driver)
	{
		this.driver = driver;
	}

	public User getCreator()
	{
		return creator;
	}

	public void setCreator(User creator)
	{
		this.creator = creator;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public OrderState getState()
	{
		return state;
	}

	public void setState(OrderState state)
	{
		this.state = state;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public Date getDateStart()
	{
		return dateStart;
	}

	public void setDateStart(Date dateStart)
	{
		this.dateStart = dateStart;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getPhoneClient()
	{
		return phoneClient;
	}

	public void setPhoneClient(String phoneClient)
	{
		this.phoneClient = phoneClient;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public List<OrderDistribution> getDistributions()
	{
		return distributions;
	}

	public void setDistributions(List<OrderDistribution> distributions)
	{
		this.distributions = distributions;
	}

	public Map<Integer, String> getFeatures()
	{
		return features;
	}

	public void setFeatures(Map<Integer, String> features)
	{
		this.features = features;
	}

	public Map<Integer, Order> getParents()
	{
		return parents;
	}

	public void setParents(Map<Integer, Order> parents)
	{
		this.parents = parents;
	}

	public Map<Integer, GeoObject> getWaypoints()
	{
		return waypoints;
	}

	public void setWaypoints(Map<Integer, GeoObject> waypoints)
	{
		this.waypoints = waypoints;
	}


}


