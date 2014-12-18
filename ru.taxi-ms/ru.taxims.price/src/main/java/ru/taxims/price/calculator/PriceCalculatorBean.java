package ru.taxims.price.calculator;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.exception.PriceException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;
import ru.taxims.domain.interfaces.price.PriceCalculator;
import ru.taxims.domain.interfaces.price.PriceCalculatorMain;
import ru.taxims.domain.util.Sorter;
import ru.taxims.price.selector.FunctionSelector;

import javax.ejb.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Developer_DB on 03.09.14.
 */
@PriceCalculatorMain
@Stateless
@Local(PriceCalculator.class)
public class PriceCalculatorBean implements PriceCalculator
{
	Logger logger = Logger.getLogger(PriceCalculatorBean.class);

	@EJB(beanName = "FeatureDictionaryDaoBean")
	Dictionary<Feature> featureDictionary;

	@EJB(beanName = "SortedFeatureDictionaryDaoBean")
	Dictionary<Feature> sortedFeatureDictionary;

	@EJB
	FunctionSelector functionSelector;

	@Lock(LockType.READ)
	@Override
	public float calculatePriceAlternative(Map<Integer, String> features) throws DaoException, PriceException
	{
		float cost = 0;
		String mainParameter = null;
		Map<Integer, Feature> featureMap = new HashMap<Integer, Feature>();
		for (Map.Entry entry: features.entrySet()){
			Feature feature = featureDictionary.getItem((Integer)entry.getKey());
			featureMap.put(feature.getId(), feature);
		}
		Map<Integer, Feature> map = Sorter.sortFeatureBySequence(featureMap);
		for (Map.Entry entry: map.entrySet()){
			Feature feature = (Feature)entry.getValue();
			if (isMainParameter(feature, features.get(feature.getId()))) {
				mainParameter = features.get(feature.getId());
			}
			logger.info("  ");
			logger.info("Feature  Name = " + feature.getName() + " ; FeatureId = " + feature.getId() + " ; Sequence = " + feature.getSequence()
				+ " Rule " +  feature.getRule().getId());
			cost += functionSelector.getCost(feature, features.get(feature.getId()), mainParameter);
			if (feature.getMinimumCost() > 0)
				cost = cost > feature.getMinimumCost()? cost : feature.getMinimumCost();
			logger.info("Feature  Cost = " + cost);
			logger.info("  ");
		}

		return cost;
	}

	@Lock(LockType.READ)
	@Override
	public float calculatePrice(Map<Integer, String> features) throws DaoException, PriceException
	{
		float cost = 0;
		String mainParameter = null;
		Map<Integer, Feature> featureMap = sortedFeatureDictionary.getItems();
		Iterator<Feature> iterator = featureMap.values().iterator();
		while (iterator.hasNext()){
			Feature feature = iterator.next();
			if (features.containsKey(feature.getId())){
				if (isMainParameter(feature, features.get(feature.getId()))) {
					mainParameter = features.get(feature.getId());
				}
				logger.info("  ");
				logger.info("Feature  Name = " + feature.getName() + " ; FeatureId = " + feature.getId() + " ; Sequence = " + feature.getSequence()
					+ " Rule " +  feature.getRule().getId());
				cost += functionSelector.getCost(feature, features.get(feature.getId()), mainParameter);
				if (feature.getMinimumCost() > 0)
					cost = cost > feature.getMinimumCost()? cost : feature.getMinimumCost();
				logger.info("Feature  Cost = " + cost);
				logger.info("  ");
			}
		}
		return cost;
	}

	private boolean isMainParameter(Feature feature, String value){
		switch (feature.getSequence()){
			case 10:
			case 60:
			case 80: return true;
			default: return false;
		}
	}
}
