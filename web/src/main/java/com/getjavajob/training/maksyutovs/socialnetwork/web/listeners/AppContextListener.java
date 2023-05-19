package com.getjavajob.training.maksyutovs.socialnetwork.web.listeners;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppContextListener implements ServletContextListener {

    static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    private static final String RESOURCE_NAME = "/mysql.properties";
    private final AccountService accountService = new AccountService(new AccountDao(RESOURCE_NAME));
    private final GroupService groupService = new GroupService(new GroupDao(RESOURCE_NAME));

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        ctx.setAttribute("AccountService", accountService);
        ctx.setAttribute("GroupService", groupService);
        LOGGER.log(Level.CONFIG, "Database connection initialized for Application.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        AccountDao dao = accountService.getDao();
        dao.getPool().returnConnection(dao.getConnection());
        GroupDao groupDao = groupService.getDao();
        groupDao.getPool().returnConnection(groupDao.getConnection());
        LOGGER.log(Level.CONFIG, "Database connections closed for Application.");
    }

}
