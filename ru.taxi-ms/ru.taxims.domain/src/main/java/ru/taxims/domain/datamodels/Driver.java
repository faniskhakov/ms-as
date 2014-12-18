package ru.taxims.domain.datamodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Driver extends User
{
	String driverLicense; //Водительское удостоверение
//	long driverId; //Идентификатор водителя
	DriverState state; //Статус водителя
 	DriverContract contract; //Номер договора
	Agent agent; //Идентификатор агента
	int priority; //Приоритет
	int radiusWork; //Радиус работы водителя
	Car car; //Автомобиль по умолчанию
	long passport; //Номер паспорта
	String registrationAddress;	//Адрес прописки
	String inhabitationAddress; //Адресс проживания
	String medicalExamination; //Данные по медицинскому освидетельствованию(осмотру)
	Date dateMadicalExamination; //Дата медицинского освидетельствования (осмотра)
	Date birthday; //День рождения
	long inn; //ИНН
	long pensionCertificate; //Пенсионное удостоверение
	Date datePassportIssue; //Дата выдачи паспорта
	String placePassportIssue; //Место выдачи паспорта
	Date lastDateOfRegistration; //Дата последней регистрации в офисе
	GeoObject geoObject; //Текущее положение водителя
	DriverTariff driverTariff; //Тариф водителя
	Order order; //Текущий заказ
	List<Long> attachedOrders = new ArrayList<>(); //Идентификаторы текущих заказов водителя
	List<District> districts ; //Районы работы водителя
	//Map<Integer, String> features; //Особенности водителя


	public Driver(){}

	public Driver(long driverId)
	{
		this.userId = driverId;
	}

	public String getDriverLicense()
	{
		return driverLicense;
	}

	public void setDriverLicense(String driverLicense)
	{
		this.driverLicense = driverLicense;
	}

	public long getDriverId()
	{
		return userId;
	}

	public void setDriverId(long driverId)
	{
		this.userId = driverId;
	}

	public DriverState getState()
	{
		return state;
	}

	public void setState(DriverState state)
	{
		this.state = state;
	}

	public DriverContract getContract()
	{
		return contract;
	}

	public void setContract(DriverContract contract)
	{
		this.contract = contract;
	}

	public Agent getAgent()
	{
		return agent;
	}

	public void setAgent(Agent agent)
	{
		this.agent = agent;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public int getRadiusWork()
	{
		return radiusWork;
	}

	public void setRadiusWork(int radiusWork)
	{
		this.radiusWork = radiusWork;
	}

	public Car getCar()
	{
		return car;
	}

	public void setCar(Car car)
	{
		this.car = car;
	}

	public long getPassport()
	{
		return passport;
	}

	public void setPassport(long passport)
	{
		this.passport = passport;
	}

	public String getRegistrationAddress()
	{
		return registrationAddress;
	}

	public void setRegistrationAddress(String registrationAddress)
	{
		this.registrationAddress = registrationAddress;
	}

	public String getInhabitationAddress()
	{
		return inhabitationAddress;
	}

	public void setInhabitationAddress(String inhabitationAddress)
	{
		this.inhabitationAddress = inhabitationAddress;
	}

	public String getMedicalExamination()
	{
		return medicalExamination;
	}

	public void setMedicalExamination(String medicalExamination)
	{
		this.medicalExamination = medicalExamination;
	}

	public Date getDateMadicalExamination()
	{
		return dateMadicalExamination;
	}

	public void setDateMadicalExamination(Date dateMadicalExamination)
	{
		this.dateMadicalExamination = dateMadicalExamination;
	}

	public Date getBirthday()
	{
		return birthday;
	}

	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}

	public long getInn()
	{
		return inn;
	}

	public void setInn(long inn)
	{
		this.inn = inn;
	}

	public long getPensionCertificate()
	{
		return pensionCertificate;
	}

	public void setPensionCertificate(long pensionCertificate)
	{
		this.pensionCertificate = pensionCertificate;
	}

	public Date getDatePassportIssue()
	{
		return datePassportIssue;
	}

	public void setDatePassportIssue(Date datePassportIssue)
	{
		this.datePassportIssue = datePassportIssue;
	}

	public String getPlacePassportIssue()
	{
		return placePassportIssue;
	}

	public void setPlacePassportIssue(String placePassportIssue)
	{
		this.placePassportIssue = placePassportIssue;
	}

	public Date getLastDateOfRegistration()
	{
		return lastDateOfRegistration;
	}

	public void setLastDateOfRegistration(Date lastDateOfRegistration)
	{
		this.lastDateOfRegistration = lastDateOfRegistration;
	}

	public GeoObject getGeoObject()
	{
		return geoObject;
	}

	public void setGeoObject(GeoObject geoObject)
	{
		this.geoObject = geoObject;
	}

	public List<District> getDistricts()
	{
		return districts;
	}

	public void setDistricts(List<District> districts)
	{
		this.districts = districts;
	}

	public DriverTariff getDriverTariff()
	{
		return driverTariff;
	}

	public void setDriverTariff(DriverTariff driverTariff)
	{
		this.driverTariff = driverTariff;
	}

//	public Map<Integer, String> getFeatures()
//	{
//		return features;
//	}
//
//	public void setFeatures(Map<Integer, String> features)
//	{
//		this.features = features;
//	}

	public Order getOrder()
	{
		return order;
	}

	public void setOrder(Order order)
	{
		this.order = order;
	}

	public List<Long> getAttachedOrders()
	{
		return attachedOrders;
	}

	public void setAttachedOrders(List<Long> attachedOrders)
	{
		this.attachedOrders = attachedOrders;
	}

	public Driver setUser(User user){
		this.setUserId(user.getUserId());
		this.setName(user.getName());
		this.setSurname(user.getSurname());
		this.setCity(user.getCity());
		this.setEmail(user.getEmail());
		this.setGender(user.getGender());
		this.setLanguage(user.getLanguage());
		this.setLogin(user.getLogin());
		this.setPassword(user.getPassword());
		this.setPrivateKey(user.getPublicKey());
		this.setPublicKey(user.getPublicKey());
		this.setRoles(user.getRoles());
		this.setSource(user.getSource());
		this.setFeatures(user.getFeatures());
		return this;
	}
}

