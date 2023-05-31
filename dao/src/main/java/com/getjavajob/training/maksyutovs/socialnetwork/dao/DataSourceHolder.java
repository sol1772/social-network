package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSourceHolder {

    private static final String DEF_URL = "jdbc:";
    private static DataSourceHolder dataSourceHolder;
    private static DataSource dataSource;
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    private DataSourceHolder() {
        setContextDataSource();
    }

    private DataSourceHolder(Properties properties) {
        setBasicDataSource(properties);
    }

    public static void setDataSource(DataSource ds) {
        DataSourceHolder.dataSource = ds;
    }

    public static synchronized DataSourceHolder getInstance(Properties properties) {
        if (dataSourceHolder == null) {
            if (properties == null) {
                dataSourceHolder = new DataSourceHolder();
            } else {
                dataSourceHolder = new DataSourceHolder(properties);
            }
        }
        return dataSourceHolder;
    }

    private static void setContextDataSource() {
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/socnet");
        } catch (NamingException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        }
    }

    private static void setBasicDataSource(Properties properties) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUsername(properties.getProperty("user"));
        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal")));
        String type = properties.getProperty("type");
        String dbms = properties.getProperty("dbms");
        String host = properties.getProperty("host");
        String port = properties.getProperty("port");
        String database = properties.getProperty("database");
        String url;
        if (type.equals("remote")) {
            url = DEF_URL + dbms + "://" + host + ":" + port + "/" + database;
        } else if (type.equals("embedded")) {
            url = DEF_URL + dbms + ":" + ((host.isEmpty()) ? "~/" : host) + database;
        } else {
            url = DEF_URL + dbms + ":" + host + database;
        }
        dataSource.setUrl(url);
        setDataSource(dataSource);
    }

    public Connection getConnection() {
        Connection connection = connectionHolder.get();
        try {
            if (connection == null || !connection.isValid(0)) {
                if (connection != null) {
                    connectionHolder.remove();
                    connection.close();
                }
                connection = dataSource.getConnection();
                connectionHolder.set(connection);
            }
        } catch (SQLException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        }
        return connection;
    }

    public void returnConnection() {
        Connection connection = connectionHolder.get();
        try {
            if (connection != null) {
                connectionHolder.remove();
                connection.close();
            }
        } catch (SQLException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        }
    }

}