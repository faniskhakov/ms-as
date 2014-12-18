package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Source;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.*;

/**
 * Created by Developer_DB on 30.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class SourceDictionaryDaoBean extends DictionaryDaoBean<Source>
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
			preparedStatement = connection.prepareStatement("SELECT source_id, name FROM source ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int sourceId = result.getInt("source_id");
				Source source = new Source(sourceId);
				source.setName(result.getString("name"));
				items.put(sourceId, source);
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

	public int insertSource(Source source) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_SHIFT_SQL = "INSERT INTO source " +
				" (\"name\") " +
				" VALUES (?) RETURNING source_id ";
			preparedStatement = connection.prepareStatement(INSERT_SHIFT_SQL);
			preparedStatement.setString(1, source.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				source.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting source failed, no generated key obtained.");
			}
			items.put(source.getId(), source);
			return  source.getId();
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
