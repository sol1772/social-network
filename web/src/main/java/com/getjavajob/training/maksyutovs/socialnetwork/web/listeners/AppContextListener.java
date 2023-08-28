package com.getjavajob.training.maksyutovs.socialnetwork.web.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);
    private static final String RESOURCE_NAME = "/config.properties";
    private final Properties properties = new Properties();

    public AppContextListener() {
        loadConfigProperties();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext sc = servletContextEvent.getServletContext();
        sc.setAttribute("ConfigProperties", properties);
        if (logger.isInfoEnabled()) {
            logger.info("Database connection initialized for Application.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (logger.isInfoEnabled()) {
            logger.info("Database connections closed for Application.");
        }
    }

    private void loadConfigProperties() {
        try (InputStream is = getClass().getResourceAsStream(RESOURCE_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }
    }

}
