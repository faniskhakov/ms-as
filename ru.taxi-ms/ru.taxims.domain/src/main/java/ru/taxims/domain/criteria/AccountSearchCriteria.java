package ru.taxims.domain.criteria;

/**
 * Created by Developer_DB on 30.05.14.
 */
public class AccountSearchCriteria extends SearchCriteria
{

	public static String blockageCriteria(boolean blockage){
		return " WHERE blockage = " + blockage;
	}

	public static String userCriteria(long userId){
		return " WHERE user_id = " + userId;
	}

	public static String userBlockageCriteria(long userId, boolean blockage){
		return " WHERE user_id = " + userId + " AND blockage = " + blockage;
	}

	public static String amountGreaterCriteria(float amount, boolean blockage){
		return " WHERE amount >= " + amount + " AND blockage = " + blockage;
	}

	public static String amountLessCriteria(float amount, boolean blockage){
		return " WHERE amount <= " + amount + " AND blockage = " + blockage;
	}

}
