package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class LoginServlet extends HttpServlet {

    private static final String EMAIL = "email";
    private static final String PASS = "password";
    private static final String ERROR = "error";
    private static final String LOGIN = "/WEB-INF/jsp/login.jsp";
    private AccountService accountService;

    @Override
    public void init(ServletConfig config) {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter(EMAIL);
        try {
            if (email == null) {
                req.getRequestDispatcher(LOGIN).forward(req, resp);
            } else {
                Account account = accountService.getAccountByEmail(email);
                String accountInfo = req.getContextPath() + "/account?id=" + account.getId();
                resp.sendRedirect(accountInfo);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        String email = req.getParameter(EMAIL);
        String password = req.getParameter(PASS);
        try (PrintWriter out = resp.getWriter()) {
            if (email.isEmpty()) {
                req.setAttribute(ERROR, "Enter email");
                req.getRequestDispatcher(LOGIN).forward(req, resp);
                return;
            }
            Account account = accountService.getAccountByEmail(email);
            if (account == null) {
                // error handling
                req.setAttribute(ERROR, "Account not found for email: " + email);
                req.getRequestDispatcher(LOGIN).forward(req, resp);
            } else {
                if (accountService.passwordIsValid(password, account)) {
                    out.println("You are logged in");
                    req.setAttribute(EMAIL, email);
                    if (req.getParameter("rememberMe") != null) {
                        Cookie cookieEmail = new Cookie(EMAIL, email);
                        Cookie cookiePass = new Cookie(PASS, account.getPasswordHash());
                        cookieEmail.setMaxAge(86400);
                        cookiePass.setMaxAge(86400);
                        resp.addCookie(cookieEmail);
                        resp.addCookie(cookiePass);
                    }
                    HttpSession session = req.getSession();
                    session.setAttribute("account", account);
                    session.setAttribute("username", account.getUserName());
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + account.getId());
                } else {
                    // error handling
                    req.setAttribute(ERROR, "Wrong password.");
                    req.getRequestDispatcher(LOGIN).forward(req, resp);
                }
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

}
