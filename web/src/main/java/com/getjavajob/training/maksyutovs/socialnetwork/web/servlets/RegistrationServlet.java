package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.Utils;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet
public class RegistrationServlet extends HttpServlet {

    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String MIDDLENAME = "middleName";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASS = "password";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String GENDER = "gender";
    private static final String ABOUT = "addInfo";
    private static final String REG = "/WEB-INF/jsp/registration.jsp";
    private static final String LOGIN = "/WEB-INF/jsp/login.jsp";
    private AccountService accountService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(REG).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        String command = req.getParameter("submit");
        try {
            if (command.equals("Register")) {
                List<String> violations = validate(req);
                if (!violations.isEmpty()) {
                    req.setAttribute("violations", violations);
                    req.setAttribute(FIRSTNAME, req.getParameter(FIRSTNAME));
                    req.setAttribute(LASTNAME, req.getParameter(LASTNAME));
                    req.setAttribute(MIDDLENAME, req.getParameter(MIDDLENAME));
                    req.setAttribute(EMAIL, req.getParameter(EMAIL));
                    req.setAttribute(USERNAME, req.getParameter(USERNAME));
                    req.setAttribute(ABOUT, req.getParameter(ABOUT));
                    req.setAttribute(BIRTHDATE, req.getParameter(BIRTHDATE));
                    req.setAttribute(GENDER, req.getParameter(GENDER));
                    req.getRequestDispatcher(REG).include(req, resp);
                } else {
                    Account dbAccount = createAccount(req);
                    req.setAttribute(EMAIL, req.getParameter(EMAIL));
                    HttpSession session = req.getSession();
                    session.setAttribute("account", dbAccount);
                    session.setAttribute(USERNAME, dbAccount.getUserName());
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + dbAccount.getId());
                }
            } else if (command.equals("Cancel")) {
                req.getRequestDispatcher(LOGIN).forward(req, resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> validate(HttpServletRequest req) {
        List<String> violations = new ArrayList<>();

        if (StringUtils.isEmpty(req.getParameter(FIRSTNAME))) {
            violations.add("Required field 'firstName' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(LASTNAME))) {
            violations.add("Required field 'lastName' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(USERNAME))) {
            violations.add("Required field 'username' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(EMAIL))) {
            violations.add("Required field 'email' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(PASS))) {
            violations.add("Required field 'password' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(BIRTHDATE))) {
            violations.add("Required field 'dateOfBirth' not filled.");
        }
        if (StringUtils.isEmpty(req.getParameter(GENDER))) {
            violations.add("Required field 'gender' not filled.");
        }
        return violations;
    }

    private Account createAccount(HttpServletRequest req) {
        String firstName = req.getParameter(FIRSTNAME);
        String lastName = req.getParameter(LASTNAME);
        String middleName = req.getParameter(MIDDLENAME);
        String email = req.getParameter(EMAIL);
        String username = req.getParameter(USERNAME);
        String password = req.getParameter(PASS);
        String dateOfBirth = req.getParameter(BIRTHDATE);
        String gender = req.getParameter(GENDER);
        String addInfo = req.getParameter(ABOUT);
        String personalPhone = req.getParameter("personalPhone");
        String workPhone = req.getParameter("workPhone");
        String homeAddress = req.getParameter("homeAddress");
        String workAddress = req.getParameter("workAddress");

        Account account = new Account(firstName, lastName, username,
                LocalDate.parse(dateOfBirth, Utils.DATE_FORMATTER), email);
        account.setMiddleName(middleName);
        account.setGender(gender.equals("M") ? Gender.M : Gender.F);
        account.setAddInfo(addInfo);
        List<Phone> phones = account.getPhones();
        if (!StringUtils.isEmpty(personalPhone)) {
            phones.add(new Phone(account, personalPhone, PhoneType.PERSONAL));
        }
        if (!StringUtils.isEmpty(workPhone)) {
            phones.add(new Phone(account, workPhone, PhoneType.WORK));
        }
        List<Address> addresses = account.getAddresses();
        if (!StringUtils.isEmpty(homeAddress)) {
            addresses.add(new Address(account, homeAddress, AddressType.HOME));
        }
        if (!StringUtils.isEmpty(workAddress)) {
            addresses.add(new Address(account, workAddress, AddressType.WORK));
        }

        Account dbAccount = accountService.registerAccount(account);
        accountService.changePassword("", password, dbAccount);
        return dbAccount;
    }

}