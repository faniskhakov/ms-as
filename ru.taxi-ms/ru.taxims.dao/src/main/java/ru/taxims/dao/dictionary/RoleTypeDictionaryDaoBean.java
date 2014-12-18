package ru.taxims.dao.dictionary;

import ru.taxims.domain.datamodels.RoleType;
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
public class RoleTypeDictionaryDaoBean extends DictionaryDaoBean<RoleType>
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
			preparedStatement = connection.prepareStatement("SELECT role_type_id, \"name\" FROM role_type ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				int roleTypeId = result.getInt("role_type_id");
				RoleType roleType = new RoleType(roleTypeId);
				roleType.setName(result.getString("name"));
				items.put(roleTypeId, roleType);
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

	public int insertRoleType(RoleType roleType) throws DaoException
	{
		assert dataSource != null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
			connection = dataSource.getConnection();
			String INSERT_TYPE_SQL = "INSERT INTO role_type " +
				" (\"name\") " +
				" VALUES (?) RETURNING role_type_id ";
			preparedStatement = connection.prepareStatement(INSERT_TYPE_SQL);
			preparedStatement.setString(1, roleType.getName());
			preparedStatement.executeQuery();
			generatedKeys = preparedStatement.getResultSet();
			if (generatedKeys.next()) {
				roleType.setId(generatedKeys.getInt(1));
			} else {
				throw new DaoException("Inserting roleType failed, no generated key obtained.");
			}
			items.put(roleType.getId(), roleType);
			return  roleType.getId();
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
