package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

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

@WebServlet
public class GroupServlet extends HttpServlet {

    private GroupService groupService;
    private AccountService accountService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
        groupService = (GroupService) sc.getAttribute("GroupService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Group group = groupService.getGroupById(id);
            if (group == null) {
                req.setAttribute("error", "Group not found");
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            } else {
                req.setAttribute("group", group);
                req.setAttribute("owner", accountService.getAccountById(group.getCreatedBy()));
                req.getRequestDispatcher("/WEB-INF/jsp/group.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
    }

}
