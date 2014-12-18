package ru.taxims.domain.datamodels;

import java.util.Date;

/**
 * Created by Developer_DB on 13.05.14.
 */
public class Account extends AbstractEntity
{
	long accountId;		//Идентификатор счета
	AccountType type;		//Идентификатор типа счета
	long userId;		//Идентификатор пользователя (владелец счета)
	float amount;		//Сумма счета
	Date dateCreate;		//Дата создания счета
	Boolean blockage;		//Блокировка счета

	public Account(){}

	public Account(long accountId)
	{
		this.accountId = accountId;
	}

	public long getAccountId()
	{
		return accountId;
	}

	public void setAccountId(long accountId)
	{
		this.accountId = accountId;
	}

	public AccountType getType()
	{
		return type;
	}

	public void setType(AccountType type)
	{
		this.type = type;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	public Date getDateCreate()
	{
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate)
	{
		this.dateCreate = dateCreate;
	}

	public Boolean getBlockage()
	{
		return blockage;
	}

	public void setBlockage(Boolean blockage)
	{
		this.blockage = blockage;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Account account = (Account)o;

		if (accountId != account.accountId) {
			return false;
		}
		if (Float.compare(account.amount, amount) != 0) {
			return false;
		}
		if (userId != account.userId) {
			return false;
		}
		if (!blockage.equals(account.blockage)) {
			return false;
		}
		if (!dateCreate.equals(account.dateCreate)) {
			return false;
		}
		if (!type.equals(account.type)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = (int)(accountId ^ (accountId >>> 32));
		result = 31 * result + type.hashCode();
		result = 31 * result + (int)(userId ^ (userId >>> 32));
		result = 31 * result + (amount != +0.0f ? Float.floatToIntBits(amount) : 0);
		result = 31 * result + dateCreate.hashCode();
		result = 31 * result + blockage.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "Account{" +
			"accountId=" + accountId +
			", type=" + type +
			", userId=" + userId +
			", amount=" + amount +
			", dateCreate=" + dateCreate +
			", blockage=" + blockage +
			'}';
	}
}

