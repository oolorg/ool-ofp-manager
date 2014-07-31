package ool.com.orientdb.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import static ool.com.constants.OfpmDefinition.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class ConnectionManagerJdbc {

    private static ConnectionManagerJdbc dbAccessManager = null;

    private static DataSource dataSource = null;
    
    private static String driverUrl = null;
    
    /**
     * @param config
     */
    private ConnectionManagerJdbc(Config config) {
    	initializeDataSource(config);
    }

    /**
     * @param config
     * @throws SQLException
     */
    private void initializeDataSource(Config config) {
        String user = config.getString(CONFIG_KEY_DB_USER);
        String password = config.getString(CONFIG_KEY_DB_PASSWORD);

        Properties params = new Properties();

        if (isNotEmpty(user) && isNotEmpty(password)) {
            params.put("user", user);
            params.put("password", password);
        }

        // ドライバのロード
        String driver = config.getString(CONFIG_KEY_DB_DRIVER);
        boolean loadSuccess = DbUtils.loadDriver(driver);
        if (!loadSuccess) {
            String message = "failed to load driver.";
            throw new RuntimeException(message);
        }

        // コネクションをプールするDataSource を作成する
        ObjectPool pool = new GenericObjectPool();
        driverUrl = config.getString(CONFIG_KEY_DB_URL);
        ConnectionFactory connFactory = new DriverManagerConnectionFactory(driverUrl, params);
        new PoolableConnectionFactory(connFactory, pool, null,
                null, // validationQuery
                false, // defaultReadOnly
                false); // defaultAutoCommit
        dataSource = new PoolingDataSource(pool);
    }

    /**
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManagerJdbc getInstance() {
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManagerJdbc(new ConfigImpl());
        }
        return dbAccessManager;
    }

    /**
     * @param config
     * @return instance
     * @throws SQLException
     */
    synchronized public static ConnectionManagerJdbc getInstance(Config config) {
        if (dbAccessManager == null) {
            dbAccessManager = new ConnectionManagerJdbc(config);
        }
        return dbAccessManager;
    }

    /**
     * return database
     * @return database object
     */
    synchronized public Connection getConnection() throws SQLException {
        Connection conn = null;
        conn = dataSource.getConnection();
        return conn;
    }

    /**
     * commit
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void commit(Connection conn) throws SQLException {
        DbUtils.commitAndClose(conn);
    }

    /**
     * rollback
     *
     * @param database
     * @throws SQLException
     */
    synchronized public void rollback(Connection conn) throws SQLException {
        DbUtils.rollback(conn);
    }

    /**
     * close database
     *
     * @param database
     */
    synchronized public void close(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            DbUtils.close(conn);
        }
    }
    
    synchronized public void close(ResultSet rs) throws SQLException {
        if (rs != null && !rs.isClosed()) {
            DbUtils.close(rs);
        }
    }
}