package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.Utils;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Gender;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet
public class AccountEditServlet extends HttpServlet {

    private static final String ACCOUNT = "account";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String MIDDLENAME = "middleName";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String ABOUT = "addInfo";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String GENDER = "gender";
    private static final String EDIT = "/WEB-INF/jsp/account-edit.jsp";


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
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);
        try {
            if (account == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                req.setAttribute(FIRSTNAME, account.getFirstName());
                req.setAttribute(LASTNAME, account.getLastName());
                req.setAttribute(MIDDLENAME, account.getMiddleName());
                req.setAttribute(USERNAME, account.getUserName());
                req.setAttribute(EMAIL, account.getEmail());
                req.setAttribute(ABOUT, account.getAddInfo());
                req.setAttribute(BIRTHDATE, Utils.DATE_FORMATTER.format(account.getDateOfBirth()));
                req.setAttribute(GENDER, String.valueOf(account.getGender() != null ?
                        account.getGender().toString().charAt(0) : 'M'));
                req.getRequestDispatcher(EDIT).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);

        try {
            String command = req.getParameter("submit");
            if (StringUtils.isEmpty(command)) {
                doGet(req, resp);
            } else {
                if (command.equals("Save")) {
                    fillAccountFromRequest(account, req);
                    Account dbAccount = accountService.editAccount(account).orElseThrow();
                    req.setAttribute(ACCOUNT, dbAccount);
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + dbAccount.getId());
                } else if (command.equals("Cancel")) {
                    req.setAttribute(ACCOUNT, account);
                    req.setAttribute("groups", groupService.getGroupsByAccount(account));
                    req.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(req, resp);
                }
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    void fillAccountFromRequest(Account account, HttpServletRequest req) {
        String firstName = req.getParameter(FIRSTNAME);
        account.setFirstName(StringUtils.isEmpty(firstName) ? account.getFirstName() : firstName);
        String lastName = req.getParameter(LASTNAME);
        account.setLastName(StringUtils.isEmpty(lastName) ? account.getLastName() : lastName);
        String middleName = req.getParameter(MIDDLENAME);
        account.setMiddleName(StringUtils.isEmpty(middleName) ? account.getMiddleName() : middleName);
        String username = req.getParameter(USERNAME);
        account.setUserName(StringUtils.isEmpty(username) ? account.getUserName() : username);
        String addInfo = req.getParameter(ABOUT);
        account.setAddInfo(StringUtils.isEmpty(addInfo) ? "" : addInfo);
        String dateOfBirth = req.getParameter(BIRTHDATE);
        account.setDateOfBirth(StringUtils.isEmpty(dateOfBirth) ? account.getDateOfBirth()
                : LocalDate.parse(dateOfBirth, Utils.DATE_FORMATTER));
        String gender = req.getParameter(GENDER);
        account.setGender(StringUtils.isEmpty(gender) ? account.getGender() : Gender.valueOf(gender));
    }

}
