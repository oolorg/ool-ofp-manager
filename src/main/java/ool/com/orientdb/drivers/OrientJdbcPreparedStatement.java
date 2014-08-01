package ool.com.orientdb.drivers;

import java.lang.reflect.Field;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orientechnologies.orient.core.exception.OQueryParsingException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;

/**
 *
 */
public class OrientJdbcPreparedStatement extends com.orientechnologies.orient.jdbc.OrientJdbcPreparedStatement {

	public OrientJdbcPreparedStatement(OrientJdbcConnection iConnection, String sql) {
		super(iConnection, sql);
	}

	@Override
	public int executeUpdate() throws SQLSyntaxErrorException {
		try {
			Field field = null;
			field = com.orientechnologies.orient.jdbc.OrientJdbcPreparedStatement.class.getDeclaredField("params");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Integer, Object> params = (Map<Integer, Object>) field.get(this);
			field = com.orientechnologies.orient.jdbc.OrientJdbcPreparedStatement.class.getDeclaredField("sql");
			field.setAccessible(true);
			String sql = (String) field.get(this);

			query = new OCommandSQL(sql);
			Object result = database.command(query).execute(params.values().toArray());
			if (result instanceof ODocument) {
				return 1;
			}
			if (result instanceof ArrayList<?>) {
				List<?> list = (List<?>)result;
				return list.size();
			}
			if (result instanceof Integer) {
				return (Integer)result;
			}
		} catch (OQueryParsingException e) {
			throw new SQLSyntaxErrorException("Error on parsing the command", e);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

}