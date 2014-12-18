package ru.taxims.domain.datamodels;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class User extends AbstractEntity
{
	long userId; //Идентификатор пользователя
	String name; //Имя пользователя
	String surname; //Фамилия пользователя
	String email; //Адрес электронной почты
	String login; //Логин пользователя
	String password; //Пароль пользователя
	Language language; //Язык по умолчанию
	City city; //Город по умолчанию
	Date dateCreate = new Date(); //Дата регистрации
	String publicKey; //Публичный ключ
	String privateKey; //Приватный ключ
	boolean blockage; //Блокировка пользователя
	int rating; //Рейтинг пользователя
	char gender; //Пол пользователя
	Source source; //Источник данных
	Communication communication; //Средство связи по умолчанию
	Account account; //Счет по умолчанию
	List<RoleType> roles; //Роли ползователя
	Map<Integer, String> features; //Особенности пользователя

	public User() {}

	public User(long userId)
	{
		this.userId = userId;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Language getLanguage()
	{
		return language;
	}

	public void setLanguage(Language language)
	{
		this.language = language;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public String getPublicKey()
	{
		return publicKey;
	}

	public void setPublicKey(String publicKey)
	{
		this.publicKey = publicKey;
	}

	public String getPrivateKey()
	{
		return privateKey;
	}

	public void setPrivateKey(String privateKey)
	{
		this.privateKey = privateKey;
	}

	public boolean isBlockage()
	{
		return blockage;
	}

	public void setBlockage(boolean blockage)
	{
		this.blockage = blockage;
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public char getGender()
	{
		return gender;
	}

	public void setGender(char gender)
	{
		this.gender = gender;
	}

	public Source getSource()
	{
		return source;
	}

	public void setSource(Source source)
	{
		this.source = source;
	}

	public Communication getCommunication()
	{
		return communication;
	}

	public void setCommunication(Communication communication)
	{
		this.communication = communication;
	}

	public Account getAccount()
	{
		return account;
	}

	public void setAccount(Account account)
	{
		this.account = account;
	}

	public List<RoleType> getRoles()
	{
		return roles;
	}

	public void setRoles(List<RoleType> roles)
	{
		this.roles = roles;
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
