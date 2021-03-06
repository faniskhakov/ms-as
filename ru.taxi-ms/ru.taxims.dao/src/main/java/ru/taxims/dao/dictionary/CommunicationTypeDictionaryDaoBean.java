package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.CommunicationType;
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
public class CommunicationTypeDictionaryDaoBean extends DictionaryDaoBean<CommunicationType>
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
			preparedStatement = connection.prepareStatement("SELECT type_id, \"name\" FROM communication_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int communicationTypeId = result.getInt("type_id");
				CommunicationType communicationType = new CommunicationType(communicationTypeId);
				communicationType.setName(result.getString("name"));
				items.put(communicationTypeId, communicationType);
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

	public int insertCommunicationType(CommunicationType communicationType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TYPE_SQL = "INSERT INTO communication_type " +
				" (\"name\") " +
				" VALUES (?) RETURNING type_id ";
			preparedStatement = connection.prepareStatement(INSERT_TYPE_SQL);
			preparedStatement.setString(1, communicationType.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				communicationType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting communicationType failed, no generated key obtained.");
			}
			items.put(communicationType.getId(), communicationType);
			return  communicationType.getId();
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
