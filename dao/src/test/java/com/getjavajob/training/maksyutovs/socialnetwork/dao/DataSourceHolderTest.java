package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSourceHolderTest {

    private static final Logger LOGGER = Logger.getLogger(DataSourceHolderTest.class.getName());
    private static final String RESOURCE_NAME = "/h2.properties";
    private static final Properties properties = new Properties();
    private static final String DELIMITER = "----------------------------------";
    static volatile AtomicInteger successfulConnections = new AtomicInteger(0);
    private static DataSourceHolder dataSourceHolder;

    @BeforeAll
    static void connect() {
        System.out.println(DELIMITER);
        System.out.println("DataSourceHolderTest.BeforeAll.connect()");
        try (InputStream is = DataSourceHolderTest.class.getResourceAsStream(RESOURCE_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        dataSourceHolder = DataSourceHolder.getInstance(properties);
        System.out.println("DataSourceHolder instance created");
    }

    @AfterAll
    static void close() {
        System.out.println(DELIMITER);
        System.out.println("DataSourceHolderTest.AfterAll.close()");
        System.out.println(DELIMITER);
        dataSourceHolder.returnConnection();
    }

    @Test
    void testConnections() {
        System.out.println(DELIMITER);
        System.out.println("ConnectionPoolTest.testConnections()");
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: " + numberOfThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
            for (int i = 1; i <= numberOfThreads; i++) {
                executor.execute(new ConnectionRunnable("Thread " + i));
            }
            executor.shutdown();
            boolean finished = executor.awaitTermination(60, TimeUnit.SECONDS);
            if (!finished) {
                System.out.println("Timeout elapsed before termination of all threads");
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        assertEquals(numberOfThreads, successfulConnections.intValue());
    }


    static class ConnectionRunnable implements Runnable {

        String name;

        ConnectionRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            dataSourceHolder.getConnection();
            System.out.println(name + " connected (OK)");
            successfulConnections.getAndIncrement();

            AccountDao dao = new AccountDao(properties);
            dao.selectAll("");

            System.out.println(name + " released");
            dataSourceHolder.returnConnection();
        }

    }

}