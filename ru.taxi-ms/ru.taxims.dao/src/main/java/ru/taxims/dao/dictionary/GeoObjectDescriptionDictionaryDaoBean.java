package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.GeoObjectDescription;
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
public class GeoObjectDescriptionDictionaryDaoBean extends DictionaryDaoBean<GeoObjectDescription>
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
			preparedStatement = connection.prepareStatement("SELECT description_id, description " +
				"  FROM object_description; ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int descriptionId = result.getInt("description_id");
				GeoObjectDescription geoObjectDescription = new GeoObjectDescription(descriptionId);
				geoObjectDescription.setDescription(result.getString("description"));
				items.put(descriptionId, geoObjectDescription);
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

	public int insertGeoObjectDescription(GeoObjectDescription geoObjectDescription) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_SQL = "INSERT INTO object_description(description) " +
				" VALUES (?) RETURNING description_id ";
			preparedStatement = connection.prepareStatement(INSERT_SQL);
			preparedStatement.setString(1, geoObjectDescription.getDescription());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				geoObjectDescription.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting geoObjectDescription failed, no generated key obtained.");
			}
			items.put(geoObjectDescription.getId(), geoObjectDescription);
			return geoObjectDescription.getId();
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
