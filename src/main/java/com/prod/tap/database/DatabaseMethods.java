package com.prod.tap.database;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseMethods {

    private static final Logger logger = Logger.getLogger(DatabaseMethods.class);

    @Autowired
    private Configvariable configvariable;

    private static final String JDBC_DRIVER_CLASS_NOT_FOUND = "jdbc driver class [{}] not found in classpath; either provide the correct driver class name or provide the driver in the classpath";

    private Map<String, Connection> databaseConnObjects = new HashMap<>();

    public Connection connectToDatabase(String url, String userName, String password) {
        Connection conn = null;
        try {
            // create a connection to the database
            if (userName.isEmpty() && password.isEmpty()) {
                conn = DriverManager.getConnection(url);
            } else {
                conn = DriverManager.getConnection(url, userName, password);
            }

            logger.info("Connection to database has been established.");

        } catch (SQLException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Connection to database failed");
        }
        return conn;
    }

    public ResultSet getResultSet(Connection conn, String sql) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "There is no data present in DB for this sql query {}", sql);
        }
        return rs;
    }

    public Connection connectToSqlite(String dbFilePath) {
        return connectToDatabase("jdbc:sqlite:" + dbFilePath, "", "");
    }

    public Map<String, String> selectFromSqliteDB(String dbFilePath, String sql) {
        Map<String, String> dataMap = new HashMap<String, String>();
        try {
            Connection conn = connectToSqlite(dbFilePath);
            ResultSet rs = getResultSet(conn, sql);
            ResultSetMetaData columns = rs.getMetaData();
            // loop through the result set
            while (rs.next()) {
                for (int i = 1; i <= columns.getColumnCount(); i++) {
                    dataMap.put(columns.getColumnName(i), rs.getString(columns.getColumnName(i)));
                }
            }
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to select value from SQlite DB");
        }
        return dataMap;
    }

    public Connection connectToOracleDB(String connectionName) {
        logger.info("creating Connection - " + connectionName);
        String connTypeProp = connectionName + ".type";
        logger.info("  connTypeProp: " + connTypeProp);
        String connectionType = configvariable.getStringVar(connTypeProp);
        if (!"jdbc_a".equalsIgnoreCase(connectionType)) {
            logger.error("unsupported connection type " + connectionType);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unsupported connection type [{}]", connectionType);
        }

        String jdbcUrl = configvariable.getStringVar(connectionName + ".jdbc.url");
        String jdbcClass = configvariable.getStringVar(connectionName + ".jdbc.class");
        String jdbcUser = configvariable.getStringVar(connectionName + ".jdbc.user");
        String jdbcPass = configvariable.getStringVar(connectionName + ".jdbc.pass");
        String jdbcDescription = configvariable.getStringVar(connectionName + ".jdbc.description");

        logger.debug("  jdbcUrl: " + jdbcUrl);
        logger.debug("  jdbcClass: " + jdbcClass);
        logger.debug("  jdbcUser: " + jdbcUser);
        logger.debug("  jdbcPass: ********" + jdbcPass);
        logger.debug("  jdbcDescription: " + jdbcDescription);

        try {
            Class.forName(jdbcClass);
        } catch (ClassNotFoundException e) {
            logger.error(JDBC_DRIVER_CLASS_NOT_FOUND + jdbcClass, e);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, JDBC_DRIVER_CLASS_NOT_FOUND, jdbcClass);
        }
        Connection result = connectToDatabase(jdbcUrl, jdbcUser, jdbcPass);
        return result;
    }

    public void connectToDb(String dbName, String dbFilePath) {
        Connection conn = databaseConnObjects.get(dbName);
        try {
            if (conn != null && conn.isValid(5)) {
                logger.debug("Connection for [{}] is already established..." + dbName);
                return;
            } else if ("sqlite".equalsIgnoreCase(dbName)) {
                databaseConnObjects.put(dbName, connectToSqlite(dbFilePath));
            } else {
                databaseConnObjects.put(dbName, connectToOracleDB(dbName));
            }
        } catch (SQLException e) {
            //ignore
        }
    }
}
