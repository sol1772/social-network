package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionPoolTest {

    private static final String RESOURCE_NAME = "/h2.properties";
    private static final Properties properties = new Properties();
    private static final String DELIMITER = "----------------------------------";
    static volatile AtomicInteger successfulConnections = new AtomicInteger(0);
    private static ConnectionPool pool;

    @BeforeAll
    static void connect() {
        System.out.println(DELIMITER);
        System.out.println("ConnectionPoolTest.BeforeAll.connect()");
        try (InputStream is = ConnectionPoolTest.class.getResourceAsStream(RESOURCE_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = ConnectionPool.getInstance(properties);
        System.out.println("Total available connections: " + pool.getAvailableConnections());
    }

    @AfterAll
    static void close() {
        System.out.println(DELIMITER);
        System.out.println("ConnectionPoolTest.AfterAll.close()");
        System.out.println(DELIMITER);
        pool.closeConnections();
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
        } catch (InterruptedException err) {
            err.printStackTrace();
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
            Connection con;

            con = pool.getConnection();
            System.out.println(name + " connected (OK)");
            successfulConnections.getAndIncrement();

            AccountDao dao = new AccountDao(con);
            dao.selectAll("");

            System.out.println(name + " released");
            pool.returnConnection(con);
        }

    }

}
