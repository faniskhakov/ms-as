package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Feature;
import ru.taxims.domain.datamodels.FeatureCalculationElement;
import ru.taxims.domain.datamodels.FeatureComparisonRule;
import ru.taxims.domain.datamodels.FeatureRule;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Developer_DB on 09.09.14.
 */
@Singleton
@Startup
@Local({Dictionary.class})
@DependsOn({"FeatureRuleDictionaryDaoBean", "FeatureComparisonRuleDictionaryDaoBean"})
public class SortedFeatureDictionaryDaoBean extends SortedDictionaryDaoBean<Feature>
{
	@EJB(beanName = "FeatureRuleDictionaryDaoBean")
	protected Dictionary<FeatureRule> featureRuleDictionary;
	@EJB(beanName = "FeatureComparisonRuleDictionaryDaoBean")
	protected Dictionary<FeatureComparisonRule> comparisonRuleDictionary;

	public void subInit() throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(" SELECT " +
				"	f.feature_id, " +
				"	f.name, " +
				"	f.rule_id, " +
				"	f.unit, " +
				"	f.enabled, " +
				"	f.auto_calc, " +
				"	f.bind_car, " +
				"	f.bind_driver, " +
				"	f.sequence, " +
				"	f.minimum_cost, " +
				"	f.comparison_rule_id " +
				" FROM feature f  ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				Feature feature = new Feature(result.getInt("feature_id"));
				feature.setName(result.getString("name"));
				feature.setRule(featureRuleDictionary.getItem(result.getInt("rule_id")));
				feature.setUnit(result.getString("unit"));
				feature.setEnebled(result.getBoolean("enabled"));
				feature.setAutoCalc(result.getBoolean("auto_calc"));
				feature.setBindCar(result.getBoolean("bind_car"));
				feature.setBindDriver(result.getBoolean("bind_driver"));
				feature.setSequence(result.getInt("sequence"));
				feature.setMinimumCost(result.getFloat("minimum_cost"));
				feature.setComparisonRule(comparisonRuleDictionary.getItem(result.getInt("comparison_rule_id")));
				feature.setCalculationElements(new ArrayList<FeatureCalculationElement>());
				items.put(feature.getId(), feature);
			}
			preparedStatement = connection.prepareStatement(" SELECT " +
				"	feature_id, " +
				"	name, " +
				"	sequence, " +
				"	value, " +
				"	min, " +
				"	max,  " +
				"	\"group\"  " +
				" FROM feature_calculation " +
				" WHERE enabled is true ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				FeatureCalculationElement element = new FeatureCalculationElement();
				element.setName(result.getString("name"));
				element.setValue(result.getFloat("value"));
				element.setMin(result.getFloat("min"));
				element.setMax(result.getFloat("max"));
				element.setSequence(result.getInt("sequence"));
				element.setGroup(result.getInt("group"));
				items.get(result.getInt("feature_id")).getCalculationElements().add(element);
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	@PostConstruct
	public void init() throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(" SELECT " +
				"	f.feature_id, " +
				"	f.name AS feature_name, " +
				"	f.rule_id, " +
				"	f.unit, " +
				"	f.enabled, " +
				"	f.auto_calc, " +
				"	f.bind_car, " +
				"	f.bind_driver, " +
				"	f.sequence AS feature_sequence," +
				"	f.minimum_cost, " +
				"	f.comparison_rule_id, " +
				"	c.name, " +
				"	c.sequence, " +
				"	c.value, " +
				"	c.min, " +
				"	c.max,  " +
				"	c.group  " +
				" FROM feature f  " +
				" JOIN feature_calculation c ON (f.feature_id = c.feature_id) " +
				" WHERE c.enabled is true " +
				" ORDER BY f.sequence, f.feature_id, c.group, c.sequence ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				FeatureCalculationElement element = new FeatureCalculationElement();
				element.setName(result.getString("name"));
				element.setValue(result.getFloat("value"));
				element.setMin(result.getFloat("min"));
				element.setMax(result.getFloat("max"));
				element.setSequence(result.getInt("sequence"));
				element.setGroup(result.getInt("group"));
				if(result.getInt("sequence") == 1 && result.getInt("group") == 1){
					Feature feature = new Feature(result.getInt("feature_id"));
					feature.setName(result.getString("feature_name"));
					feature.setRule(featureRuleDictionary.getItem(result.getInt("rule_id")));
					feature.setUnit(result.getString("unit"));
					feature.setEnebled(result.getBoolean("enabled"));
					feature.setAutoCalc(result.getBoolean("auto_calc"));
					feature.setBindCar(result.getBoolean("bind_car"));
					feature.setBindDriver(result.getBoolean("bind_driver"));
					feature.setSequence(result.getInt("feature_sequence"));
					feature.setMinimumCost(result.getFloat("minimum_cost"));
					feature.setComparisonRule(comparisonRuleDictionary.getItem(result.getInt("comparison_rule_id")));
					feature.setCalculationElements(new ArrayList<FeatureCalculationElement>());
					items.put(feature.getId(), feature);
				}
				items.get(result.getInt("feature_id")).getCalculationElements().add(element);
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new DaoException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	public int insertFeature(Feature feature) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_FEATURE_SQL = "INSERT INTO feature(name, rule_id, unit, enabled, " +
				"  auto_calc, bind_car, bind_driver, sequence) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING feature_id ";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			preparedStatement.setString(1, feature.getName());
			preparedStatement.setInt(2, feature.getRule().getId());
			preparedStatement.setString(3, feature.getUnit());
			preparedStatement.setBoolean(4, feature.isEnebled());
			preparedStatement.setBoolean(5, feature.isAutoCalc());
			preparedStatement.setBoolean(6, feature.isBindCar());
			preparedStatement.setBoolean(7, feature.isBindDriver());
			preparedStatement.setInt(8, feature.getSequence());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				feature.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting feature failed, no generated key obtained.");
			}
			INSERT_FEATURE_SQL = " INSERT INTO feature_calculation(name, value, min, max, feature_id, enabled, sequence, group) " +
				"    VALUES (?, ?, ?, ?, ?, ?, ?, ?); ";
			preparedStatement = connection.prepareStatement(INSERT_FEATURE_SQL);
			for (FeatureCalculationElement element: feature.getCalculationElements()) {
				preparedStatement.setString(1, element.getName());
				preparedStatement.setFloat(2, element.getValue());
				preparedStatement.setFloat(3, element.getMin());
				preparedStatement.setFloat(4, element.getMax());
				preparedStatement.setInt(5, feature.getId());
				preparedStatement.setBoolean(6, feature.isEnebled());
				preparedStatement.setInt(7, feature.getSequence());
				preparedStatement.setInt(8, element.getGroup());
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			items.put(feature.getId(), feature);
			return  feature.getId();
		} catch (SQLException e) {
			logger.error(e + " Transaction was rolled back");
			throw new DaoException(e);
		} finally {
			try {
				if (generatedKeys != null) {
					generatedKeys.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}


}
