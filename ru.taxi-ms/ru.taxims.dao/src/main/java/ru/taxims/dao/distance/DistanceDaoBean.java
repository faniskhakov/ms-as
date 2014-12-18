package ru.taxims.dao.distance;

import org.apache.log4j.Logger;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.DistanceCalculator;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Developer_DB on 03.09.14.
 */
public class DistanceDaoBean implements DistanceCalculator
{
	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;
	protected Logger logger = Logger.getLogger(DistanceDaoBean.class);
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	@Override
	public int getDistance(int startSectionId, int endSectionId) throws DaoException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			String SELECT_ACCOUNT_SQL = "SELECT distance  " +
				" FROM object_distance " +
				" WHERE start_section_id = ? AND end_section_id = ? ";
			preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_SQL);
			preparedStatement.setInt(1, startSectionId);
			preparedStatement.setInt(1, endSectionId);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				return result.getInt("distance");
			} else {
				throw new SQLException("Selecting account failed, no rows affected.");
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
}
