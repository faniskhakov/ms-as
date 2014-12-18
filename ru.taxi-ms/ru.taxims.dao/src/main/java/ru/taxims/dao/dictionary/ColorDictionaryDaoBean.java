package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.Color;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.Dictionary;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Developer_DB on 29.05.14.
 */
@Singleton
@Startup
@Local(Dictionary.class)
public class ColorDictionaryDaoBean extends DictionaryDaoBean<Color>
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
			preparedStatement = connection.prepareStatement("SELECT color_id, \"name\", english_name FROM color ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int colorId = result.getInt("color_id");
				Color color = new Color(colorId);
				color.setName(result.getString("name"));
				color.setEnglishName(result.getString("english_name"));
				items.put(colorId, color);
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

	public int insertColor(Color color) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_COLOR_SQL = "INSERT INTO color " +
				" (\"name\", english_name) " +
				" VALUES (?, ?) RETURNING color_id ";
			preparedStatement = connection.prepareStatement(INSERT_COLOR_SQL);
			preparedStatement.setString(1, color.getName());
			preparedStatement.setString(2, color.getEnglishName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				color.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting color failed, no generated key obtained.");
			}
			items.put(color.getId(), color);
			return  color.getId();
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
