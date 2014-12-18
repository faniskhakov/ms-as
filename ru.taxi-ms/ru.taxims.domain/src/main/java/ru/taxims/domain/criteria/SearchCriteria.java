package ru.taxims.domain.criteria;

/**
 * Created by Developer_DB on 28.05.14.
 */
public class SearchCriteria
{
	Integer first;
	Integer pageSize = 50;
	String sortField;
	Integer rowCount = 0;
	boolean ascending = true;
	Integer currentPage = 1;
}
