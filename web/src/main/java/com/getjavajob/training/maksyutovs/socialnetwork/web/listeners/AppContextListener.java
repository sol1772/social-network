package com.getjavajob.training.maksyutovs.socialnetwork.web.listeners;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    private static final String RESOURCE_NAME = "/config.properties";
    private final Properties properties = new Properties();
    private final AccountService accountService;
    private final GroupService groupService;

    public AppContextListener() {
        loadConfigProperties();
        accountService = new AccountService(new AccountDao());
        groupService = new GroupService(new GroupDao());
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        ctx.setAttribute("AccountService", accountService);
        ctx.setAttribute("GroupService", groupService);
        ctx.setAttribute("ConfigProperties", properties);
        LOGGER.log(Level.CONFIG, "Database connection initialized for Application.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LOGGER.log(Level.CONFIG, "Database connections closed for Application.");
    }

    private void loadConfigProperties() {
        try (InputStream is = getClass().getResourceAsStream(RESOURCE_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

}
