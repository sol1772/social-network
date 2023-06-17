package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Role;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@WebServlet
public class GroupEditServlet extends HttpServlet {

    private static final String EDIT_URL = "/WEB-INF/jsp/group-edit.jsp";
    private static final String TITLE = "title";
    private static final String ERROR = "error";
    private GroupService groupService;

    @Override
    public void init() {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        groupService = Objects.requireNonNull(context).getBean(GroupService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Group group = groupService.getGroupById(id);
        if (group == null) {
            req.setAttribute(ERROR, "Group not found");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
        req.setAttribute("group", group);
        req.getRequestDispatcher(EDIT_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        String title = req.getParameter(TITLE);
        Group group = groupService.getGroupByTitle(title);
        String command = req.getParameter("submit");
        if (command.equals("Save")) {
            if (title.isEmpty()) {
                req.setAttribute(ERROR, "Enter title");
                req.getRequestDispatcher(EDIT_URL).forward(req, resp);
            }
            String metaTitle = req.getParameter("metaTitle");
            group.setMetaTitle(metaTitle);
            group.setCreatedBy(account.getId());
            group.getMembers().add(new GroupMember(group, account, Role.ADMIN));
            Group dbGroup = groupService.editGroup(group);
            resp.sendRedirect(req.getContextPath() + "/group?id=" + dbGroup.getId());

        } else if (command.equals("Cancel")) {
            resp.sendRedirect(req.getContextPath() + "/group?id=" + group.getId());
        }
    }

}
