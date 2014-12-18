package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Tariff;
import ru.taxims.domain.datamodels.TariffRule;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;

/**
 * Created by Developer_DB on 05.06.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("TariffRuleDictionaryDaoBean")
public class TariffDictionaryDaoBean extends DictionaryDaoBean<Tariff>
{
	@EJB(beanName = "TariffRuleDictionaryDaoBean")
	protected Dictionary<TariffRule> tariffRuleDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT tariff_id, name, duration, rule_id, blockage " +
				"  FROM tariff ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int tariffId = result.getInt("tariff_id");
				Tariff tariff = new Tariff(tariffId);
				tariff.setName(result.getString("name"));
				tariff.setDuration(result.getLong("duration"));
				tariff.setRule(tariffRuleDictionary.getItem(result.getInt("rule_id")));
				tariff.setBlockage(result.getBoolean("blockage"));
				items.put(tariffId, tariff);
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

	public int insertTariff(Tariff tariff) throws DaoException
	{
		assert dataSource != null;
		int tariffId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TYPE_SQL = "INSERT INTO tariff " +
				" (\"name\", duration, rule_id) " +
				" VALUES (?, ?, ?) RETURNING tariff_id ";
			preparedStatement = connection.prepareStatement(INSERT_TYPE_SQL);
			preparedStatement.setString(1, tariff.getName());
			preparedStatement.setLong(2, tariff.getDuration());
			preparedStatement.setInt(3, tariff.getRule().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				tariff.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting tariff failed, no generated key obtained.");
			}
			items.put(tariff.getId(), tariff);
			return  tariff.getId();
		} catch (SQLException e) {
			logger.error(e);
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
