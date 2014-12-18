package ru.taxims.dao.dictionary;

import org.apache.log4j.Logger;
import ru.taxims.domain.datamodels.GeoObjectDistance;
import ru.taxims.domain.exception.DaoException;
import ru.taxims.domain.interfaces.dictionary.DistanceCalculator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Developer_DB on 03.09.14.
 */
@Singleton
@Startup
@Local(DistanceCalculator.class)
public class DistanceDictionaryDaoBean implements DistanceCalculator
{
	@Resource(lookup="java:jboss/datasources/PostgresDS")
	protected DataSource dataSource;
	protected Logger logger = Logger.getLogger(DistanceDictionaryDaoBean.class);
	protected List<GeoObjectDistance> items = new ArrayList<GeoObjectDistance>();

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	@Lock(LockType.READ)
	@Override
	public int getDistance(int startSectionId, int endSectionId)
	{
		for (GeoObjectDistance item: items)
		{
			if (item.getStartSectionId() == startSectionId && item.getEndSectionId() == endSectionId) {
				logger.info("item.getStartPointId() = " + item.getStartSectionId() + " item.getEndPointId() = " +
					item.getEndSectionId() + " item.getDistance() " + item.getDistance());
				return item.getDistance();
			}
		}
		return 0;
	}

	@PostConstruct
	public void init() throws DaoException
	{
		assert dataSource != null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement("SELECT start_section_id, end_section_id, distance " +
				" FROM object_distance ");
			result = preparedStatement.executeQuery();
			while (result.next()) {
				GeoObjectDistance geoObjectDistance = new GeoObjectDistance();
				geoObjectDistance.setStartSectionId(result.getInt("start_section_id"));
				geoObjectDistance.setEndSectionId(result.getInt("end_section_id"));
				geoObjectDistance.setDistance(result.getInt("distance"));
				items.add(geoObjectDistance);
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
