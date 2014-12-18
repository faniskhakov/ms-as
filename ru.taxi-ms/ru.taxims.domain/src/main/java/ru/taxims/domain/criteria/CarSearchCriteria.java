package ru.taxims.domain.criteria;

public class CarSearchCriteria extends SearchCriteria
{
	public String getNumberCriteria(String number){
		return "number ILIKE  % ||" + number + "|| %";
	}
	public String getColorCriteria(int colorId){
		return "colorId = " + colorId;
	}
	public String getModelCriteria(int modelId){
		return "modelId = " + modelId;
	}
}