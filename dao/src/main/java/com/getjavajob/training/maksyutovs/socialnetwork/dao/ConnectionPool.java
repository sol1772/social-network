package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class ConnectionPool {

    private static final int INITIAL_POOL_SIZE = Runtime.getRuntime().availableProcessors() == 1 ?
            1 : Runtime.getRuntime().availableProcessors() - 1;
    private static final String DEF_URL = "jdbc:";
    private final Properties properties = new Properties();
    private final List<Connection> availableConnections = Collections.synchronizedList(new ArrayList<>());
    private Semaphore semaphore = new Semaphore(INITIAL_POOL_SIZE, true);
    private String resourceName;

    public ConnectionPool() {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            availableConnections.add(createNewConnectionForPool());
        }
    }

    public ConnectionPool(String resourceName) {
        this.resourceName = resourceName;
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            availableConnections.add(createNewConnectionForPool());
        }
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public synchronized int getAvailableConnections() {
        return availableConnections.size();
    }

    private Connection createNewConnectionForPool() {
        Connection connection = null;
        String url;
        try (InputStream fis = this.getClass().getResourceAsStream(resourceName)) {
            properties.load(fis);
            String type = properties.getProperty("type");
            String dbms = properties.getProperty("dbms");
            String host = properties.getProperty("host");
            String port = properties.getProperty("port");
            String database = properties.getProperty("database");
            Class.forName(properties.getProperty("driver"));
            if (type.equals("remote")) {
                url = DEF_URL + dbms + "://" + host + ":" + port + "/" + database;
            } else if (type.equals("embedded")) {
                url = DEF_URL + dbms + ":" + ((host.isEmpty()) ? "~/" : host) + database;
            } else {
                url = DEF_URL + dbms + ":" + host + database;
            }
            connection = DriverManager.getConnection(url, properties);
            System.out.println("Connected successfully to " + url);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            semaphore.acquire();
            if (availableConnections.isEmpty()) {
                throw new IllegalStateException("No available connections");
            }
            synchronized (availableConnections) {
                connection = availableConnections.get(0);
                if (nonNull(connection)) {
                    if (!connection.isValid(0)) {
                        availableConnections.remove(0);
                        connection = createNewConnectionForPool();
                    } else {
                        return availableConnections.remove(0);
                    }
                } else {
                    availableConnections.remove(0);
                    connection = createNewConnectionForPool();
                }
            }
            requireNonNull(connection).setAutoCommit(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void returnConnection(Connection connection) {
        try {
            synchronized (availableConnections) {
                if (nonNull(connection)) {
                    if (!connection.isValid(0)) {
                        connection.close();
                        availableConnections.add(createNewConnectionForPool());
                    } else {
                        availableConnections.add(connection);
                    }
                } else {
                    availableConnections.add(createNewConnectionForPool());
                }
            }
            semaphore.release();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        semaphore = new Semaphore(0);
        for (Connection connection : availableConnections) {
            if (nonNull(connection)) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        availableConnections.clear();
    }

}