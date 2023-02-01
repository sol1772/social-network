package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionData {

    private static final String DEF_URL = "jdbc:";
    private final Properties properties = new Properties();
    private Connection connection;

    Connection getConnection() {
        return connection;
    }

    void connect(String resourceName) {
        String url;
        try (InputStream fis = this.getClass().getResourceAsStream(resourceName)) {
            if (connection == null) {
                properties.load(fis);
                String type = properties.getProperty("type");
                String dbms = properties.getProperty("dbms");
                String host = properties.getProperty("host");
                String port = properties.getProperty("port");
                String database = properties.getProperty("database");
                if (type.equals("remote")) {
                    url = DEF_URL + dbms + "://" + host + ":" + port + "/" + database;
                } else if (type.equals("embedded")) {
                    url = DEF_URL + dbms + ":" + ((host.isEmpty()) ? "~/" : host) + database;
                } else {
                    url = DEF_URL + dbms + ":" + host + database;
                }
                connection = DriverManager.getConnection(url, properties);
                System.out.println("Connected successfully to " + url);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}