package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.Utils;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet
public class RegistrationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegistrationServlet.class.getName());
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String MIDDLENAME = "middleName";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASS = "password";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String GENDER = "gender";
    private static final String ABOUT = "addInfo";
    private static final String REG_URL = "/WEB-INF/jsp/registration.jsp";
    private static final String LOGIN_URL = "/WEB-INF/jsp/login.jsp";
    private AccountService accountService;

    @Override
    public void init() {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        accountService = Objects.requireNonNull(context).getBean(AccountService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(REG_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        String command = req.getParameter("submit");
        try {
            if (command.equals("Register")) {
                List<String> violations = validate(req);
                if (violations.isEmpty()) {
                    Account dbAccount = createAccount(req);
                    req.setAttribute(EMAIL, req.getParameter(EMAIL));
                    HttpSession session = req.getSession();
                    session.setAttribute("account", dbAccount);
                    session.setAttribute(USERNAME, dbAccount.getUserName());
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + dbAccount.getId());
                } else {
                    req.setAttribute("violations", violations);
                    req.setAttribute(FIRSTNAME, req.getParameter(FIRSTNAME));
                    req.setAttribute(LASTNAME, req.getParameter(LASTNAME));
                    req.setAttribute(MIDDLENAME, req.getParameter(MIDDLENAME));
                    req.setAttribute(EMAIL, req.getParameter(EMAIL));
                    req.setAttribute(USERNAME, req.getParameter(USERNAME));
                    req.setAttribute(ABOUT, req.getParameter(ABOUT));
                    req.setAttribute(BIRTHDATE, req.getParameter(BIRTHDATE));
                    req.setAttribute(GENDER, req.getParameter(GENDER));
                    req.getRequestDispatcher(REG_URL).include(req, resp);
                }
            } else if (command.equals("Cancel")) {
                req.getRequestDispatcher(LOGIN_URL).forward(req, resp);
            }
        } catch (IOException | DaoRuntimeException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
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
        String email = req.getParameter(EMAIL);
        if (StringUtils.isEmpty(email)) {
            violations.add("Required field 'email' not filled.");
        } else if (accountService.getAccountByEmail(email) != null) {
            violations.add("Account with email '" + email + "' already exists");
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
        account.setPasswordHash(account.hashPassword(password));
        List<Phone> phones = account.getPhones();
        if (!StringUtils.isEmpty(personalPhone)) {
            phones.add(new Phone(account, 0, personalPhone, PhoneType.PERSONAL));
        }
        if (!StringUtils.isEmpty(workPhone)) {
            phones.add(new Phone(account, 0, workPhone, PhoneType.WORK));
        }
        List<Address> addresses = account.getAddresses();
        if (!StringUtils.isEmpty(homeAddress)) {
            addresses.add(new Address(account, 0, homeAddress, AddressType.HOME));
        }
        if (!StringUtils.isEmpty(workAddress)) {
            addresses.add(new Address(account, 0, workAddress, AddressType.WORK));
        }

        return accountService.registerAccount(account);
    }

}