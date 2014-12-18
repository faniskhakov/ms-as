package ru.taxims.domain.criteria;

/**
 * Created by Developer_DB on 28.05.14.
 */
public class UserSearchCriteria extends SearchCriteria
{
	public static String blockageCriteria(boolean blockage){
		return " WHERE r.role_type_id = 1 AND r.blockage = " + blockage;
	}

	public static String userBlockageCriteria(long userId, boolean blockage){
		return "  WHERE r.role_type_id = 1 AND user_id = " + userId + " AND r.blockage = " + blockage;
	}

	public static String amountGreaterCriteria(float amount, boolean blockage){
		return "  WHERE r.role_type_id = 1 AND amount >= " + amount + " AND r.blockage = " + blockage;
	}

	public static String amountLessCriteria(float amount, boolean blockage){
		return "  WHERE r.role_type_id = 1 AND amount <= " + amount + " AND r.blockage = " + blockage;
	}
}
