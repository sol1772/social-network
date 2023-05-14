package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet
public class ErrorHandlerServlet extends HttpServlet {

    private static final String TITLE = "title";
    private static final String URI = "reqUri";
    private static final String STATUS = "statusCode";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    private void processError(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");

        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) req.getAttribute("javax.servlet.error.servlet_name");
        if (servletName == null) {
            servletName = "Unknown";
        }
        String reqUri = (String) req.getAttribute("javax.servlet.error.request_uri");
        if (reqUri == null) {
            reqUri = "Unknown";
        }

        if (throwable == null && statusCode == null) {
            req.setAttribute(TITLE, "Error information is missing");
        } else if (statusCode != 500) {
            req.setAttribute(TITLE, "Error Details");
            req.setAttribute(URI, reqUri);
            req.setAttribute(STATUS, statusCode);
        } else {
            assert throwable != null;
            req.setAttribute(TITLE, "Exception Details");
            req.setAttribute("servletName", servletName);
            req.setAttribute("exceptionName", throwable.getClass().getName());
            req.setAttribute("exceptionMessage", throwable.getMessage());
            req.setAttribute(URI, reqUri);
            req.setAttribute(STATUS, statusCode.toString());
        }
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

}