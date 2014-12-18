package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Language;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class LanguageDictionaryDaoBean extends DictionaryDaoBean<Language>
{
	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT language_id, \"name\" FROM language ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int languageId = result.getInt("language_id");
				Language language = new Language(languageId);
				language.setName(result.getString("name"));
				items.put(languageId, language);
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

	public int insertLanguage(Language language) throws DaoException
	{
		assert dataSource != null;
		int languageId;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_LANG_SQL = "INSERT INTO language " +
				" (\"name\") " +
				" VALUES (?) RETURNING language_id ";
			preparedStatement = connection.prepareStatement(INSERT_LANG_SQL);
			preparedStatement.setString(1, language.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				language.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting language failed, no generated key obtained.");
			}
			items.put(language.getId(), language);
			return  language.getId();
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
