package com.getjavajob.training.maksyutovs.socialnetwork.web.listeners;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDAO;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.ConnectionPool;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;

public class AppContextListener implements ServletContextListener {

    private final AccountService accountService = new AccountService();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        String resourceName = "/mysql.properties";
        AccountDAO dao = new AccountDAO(resourceName);
        accountService.setDao(dao);

        ctx.setAttribute("AccountService", accountService);
        ctx.setAttribute("AccountDAO", dao);
        System.out.println("Database connection initialized for Application.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        AccountDAO dao = (AccountDAO) ctx.getAttribute("AccountDAO");
        ConnectionPool pool = dao.getPool();
        Connection connection = dao.getConnection();
        pool.returnConnection(connection);
        System.out.println("Database connection closed for Application.");
    }

}
