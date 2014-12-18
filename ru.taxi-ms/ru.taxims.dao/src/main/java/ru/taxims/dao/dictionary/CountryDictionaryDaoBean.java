package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Country;
import ru.taxims.domain.datamodels.Language;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
@DependsOn("LanguageDictionaryDaoBean")
public class CountryDictionaryDaoBean extends DictionaryDaoBean<Country>
{
	@EJB(beanName = "LanguageDictionaryDaoBean")
	protected Dictionary<Language> languageDictionary;

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT country_id, \"name\", language_id FROM country ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int countryId = result.getInt("country_id");
				Country country = new Country(countryId);
				country.setName(result.getString("name"));
				country.setLanguage(languageDictionary.getItem(result.getInt("language_id")));
				items.put(countryId, country);
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

	public int insertCountry(Country country) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_COUNTRY_SQL = "INSERT INTO country " +
				" (\"name\", language_id) " +
				" VALUES (?, ?) RETURNING country_id ";
			preparedStatement = connection.prepareStatement(INSERT_COUNTRY_SQL);
			preparedStatement.setString(1, country.getName());
			preparedStatement.setInt(2, country.getLanguage().getId());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				country.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting country failed, no generated key obtained.");
			}
			items.put(country.getId(), country);
			return  country.getId();
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
