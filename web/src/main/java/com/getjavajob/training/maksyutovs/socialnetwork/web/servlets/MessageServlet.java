package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@WebServlet
public class MessageServlet extends HttpServlet {

    private static final String MESSAGES_URL = "/WEB-INF/jsp/message.jsp";
    private static final String ACCOUNT = "account";
    private static final String TRG_ACCOUNT = "targetAccount";
    private AccountService accountService;

    @Override
    public void init() {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        accountService = Objects.requireNonNull(context).getBean(AccountService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account account = (Account) req.getSession().getAttribute(ACCOUNT);
        Account targetAccount = null;
        try {
            String idString = req.getParameter("id");
            String trgIdString = req.getParameter("trgId");
            if (!StringUtils.isEmpty(idString)) {
                int id = Integer.parseInt(idString);
                if (account == null || account.getId() != id) {
                    account = accountService.getAccountById(id);
                }
            }
            if (!StringUtils.isEmpty(trgIdString)) {
                int id = Integer.parseInt(trgIdString);
                targetAccount = accountService.getAccountById(id);
            }
            if (account == null || targetAccount == null) {
                req.setAttribute("exceptionMessage", "Account not found");
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            } else {
                req.setAttribute(ACCOUNT, account);
                req.setAttribute(TRG_ACCOUNT, targetAccount);
                req.getRequestDispatcher(MESSAGES_URL).forward(req, resp);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
    }

}