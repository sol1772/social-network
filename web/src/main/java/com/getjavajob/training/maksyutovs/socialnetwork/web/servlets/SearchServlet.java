package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.lang.Math.max;

@WebServlet
public class SearchServlet extends HttpServlet {

    private static final String SEARCH = "/WEB-INF/jsp/search.jsp";
    private AccountService accountService;
    private GroupService groupService;

    @Override
    public void init(ServletConfig config) {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
        groupService = (GroupService) sc.getAttribute("GroupService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showPages(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
    }

    protected void showPages(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=utf-8");

        final int recordsPerPage = 5;
        String searchString = req.getParameter("q") == null ? "" : req.getParameter("q");
        int accountsTotal = accountService.getAccountsCountByString(searchString, 0, 0);
        int accountsPages = accountsTotal % recordsPerPage == 0 ?
                accountsTotal / recordsPerPage : accountsTotal / recordsPerPage + 1;
        int groupsTotal = groupService.getGroupsCountByString(searchString, 0, 0);
        int groupsPages = groupsTotal % recordsPerPage == 0 ?
                groupsTotal / recordsPerPage : groupsTotal / recordsPerPage + 1;
        int maxCountPages = max(accountsPages, groupsPages);
        int startRow = 1;
        String stringPageNumber = req.getParameter("page");
        if (stringPageNumber != null) {
            int page = Integer.parseInt(stringPageNumber);
            if (maxCountPages > 0 && page > maxCountPages) {
                req.setAttribute("error", "Page " + page + " exceeds total max count of pages " + maxCountPages);
            }
            startRow = (page - 1) * recordsPerPage + 1;
        }

        List<Account> accounts = accountService.getAccountsByString(searchString, startRow, recordsPerPage);
        List<Group> groups = groupService.getGroupsByString(searchString, startRow, recordsPerPage);

        try {
            req.setAttribute("accounts", accounts);
            req.setAttribute("groups", groups);
            req.setAttribute("q", searchString);
            req.setAttribute("accountsTotal", Integer.toString(accountsTotal));
            req.setAttribute("accountsPages", Integer.toString(accountsPages));
            req.setAttribute("groupsTotal", Integer.toString(groupsTotal));
            req.setAttribute("groupsPages", Integer.toString(groupsPages));
            req.getRequestDispatcher(SEARCH).forward(req, resp);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

}
