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

@WebServlet
public class SearchServlet extends HttpServlet {

    private static final String SEARCH = "/search.jsp";
    private static final String SEARCH_STRING = "searchString";
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
        showPages(req, resp);
    }

    protected void showPages(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/html;charset=utf-8");

        final int recordsPerPage = 5;
        String searchString = req.getParameter(SEARCH_STRING) == null ? "" : req.getParameter(SEARCH_STRING);
        int accountsTotal = accountService.getAccountsByString(searchString, 0, 0).size();
        int accountsPages = accountsTotal % recordsPerPage == 0 ?
                accountsTotal / recordsPerPage : accountsTotal / recordsPerPage + 1;
        int groupsTotal = groupService.getGroupsByString(searchString, 0, 0).size();
        int groupsPages = groupsTotal % recordsPerPage == 0 ?
                groupsTotal / recordsPerPage : groupsTotal / recordsPerPage + 1;
        int page = 1;
        String stringPageNumber = req.getParameter("page");
        if (stringPageNumber != null) {
            page = Integer.parseInt(stringPageNumber);
            if (page != 1) {
                page = (page - 1) * recordsPerPage + 1;
            }
        }

        List<Account> accounts = accountService.getAccountsByString(searchString, page, recordsPerPage);
        List<Group> groups = groupService.getGroupsByString(searchString, page, recordsPerPage);

        req.setAttribute("accounts", accounts);
        req.setAttribute("groups", groups);
        req.setAttribute(SEARCH_STRING, searchString);
        req.setAttribute("accountsTotal", Integer.toString(accountsTotal));
        req.setAttribute("accountsPages", Integer.toString(accountsPages));
        req.setAttribute("groupsTotal", Integer.toString(groupsTotal));
        req.setAttribute("groupsPages", Integer.toString(groupsPages));
        req.getRequestDispatcher(SEARCH).forward(req, resp);
    }

}
