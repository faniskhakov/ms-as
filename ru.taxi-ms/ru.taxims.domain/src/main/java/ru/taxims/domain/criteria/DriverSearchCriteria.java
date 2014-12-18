package ru.taxims.domain.criteria;

public class DriverSearchCriteria extends SearchCriteria
{
	public static String blockageCriteria(boolean blockage){
		return " WHERE r.role_type_id = 2 AND r.blockage = " + blockage;
	}

	public static String userCriteria(long userId){
		return " WHERE user_id = " + userId;
	}

	public static String userBlockageCriteria(long userId, boolean blockage){
		return "  WHERE r.role_type_id = 2 AND user_id = " + userId + " AND r.blockage = " + blockage;
	}

	public static String amountGreaterCriteria(float amount, boolean blockage){
		return "  WHERE r.role_type_id = 2 AND amount >= " + amount + " AND r.blockage = " + blockage;
	}

	public static String amountLessCriteria(float amount, boolean blockage){
		return "  WHERE r.role_type_id = 2 AND amount <= " + amount + " AND r.blockage = " + blockage;
	}

}
