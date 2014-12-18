package ru.taxims.domain.criteria;

/**
 * Created by Developer_DB on 28.05.14.
 */
public class OrderSearchCriteria extends SearchCriteria
{
	public static String stateCriteria(int stateId){
		return " WHERE state_id = " + stateId + " ; ";
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

