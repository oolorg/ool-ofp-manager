package ool.com.orientdb.drivers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author user
 *
 */
public class OrientJdbcConnection extends com.orientechnologies.orient.jdbc.OrientJdbcConnection {

	public OrientJdbcConnection(String iUrl, Properties iInfo) {
		super(iUrl, iInfo);
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new OrientJdbcPreparedStatement(this, sql);
	}
}
