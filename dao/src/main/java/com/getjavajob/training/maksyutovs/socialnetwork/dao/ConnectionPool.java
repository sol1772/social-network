package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConnectionPool {

    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());
    private static final String DEF_URL = "jdbc:";
    private static ConnectionPool pool;
    private final int initialPoolSize = Runtime.getRuntime().availableProcessors() == 1 ?
            1 : Runtime.getRuntime().availableProcessors() - 1;
    private final BlockingQueue<Connection> availableConnections = new LinkedBlockingQueue<>(initialPoolSize);
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private final Properties properties;
    private Semaphore semaphore = new Semaphore(initialPoolSize, true);

    private ConnectionPool() {
        throw new AssertionError("Default constructor is non instantiable");
    }

    private ConnectionPool(Properties properties) {
        this.properties = properties;
        for (int i = 0; i < initialPoolSize; i++) {
            availableConnections.add(createNewConnectionForPool());
        }
    }

    public static synchronized ConnectionPool getInstance(Properties properties) {
        if (pool == null) {
            pool = new ConnectionPool(properties);
        }
        return pool;
    }

    public synchronized int getAvailableConnections() {
        return availableConnections.size();
    }

    private Connection createNewConnectionForPool() {
        Connection connection = null;
        String url;
        try {
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
            LOGGER.log(Level.CONFIG, String.format("Connected successfully to %s", url), connection);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return connection;
    }

    public Connection getConnection() {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            connection = getConnectionFromPool();
            connectionHolder.set(connection);
        }
        return connection;
    }

    private Connection getConnectionFromPool() {
        Connection connection = null;
        try {
            semaphore.acquire();
            if (availableConnections.isEmpty()) {
                throw new IllegalStateException("No available connections");
            }
            synchronized (availableConnections) {
                connection = availableConnections.take();
                if (!connection.isValid(0)) {
                    connection.close();
                    connection = createNewConnectionForPool();
                }
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted!", e);
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return connection;
    }

    public void returnConnection(Connection connection) {
        try {
            synchronized (availableConnections) {
                connectionHolder.remove();
                if (connection == null) {
                    availableConnections.add(createNewConnectionForPool());
                } else if (!connection.isValid(0)) {
                    connection.close();
                    availableConnections.add(createNewConnectionForPool());
                } else if (!availableConnections.contains(connection)) {
                    availableConnections.put(connection);
                }
            }
            if (semaphore.availablePermits() < initialPoolSize) {
                semaphore.release();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    public void closeConnections() {
        for (Connection connection : availableConnections) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }

    public void shutdown() {
        semaphore = new Semaphore(0);
        closeConnections();
        availableConnections.clear();
    }

}