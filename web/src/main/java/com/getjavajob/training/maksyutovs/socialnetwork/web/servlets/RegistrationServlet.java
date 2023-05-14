package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final String REG = "/registration.jsp";
    private static final String LOGIN = "/login.jsp";
    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
        if (command.equals("Register")) {
            try {
                String firstName = req.getParameter(FIRSTNAME);
                String lastName = req.getParameter(LASTNAME);
                String middleName = req.getParameter(MIDDLENAME);
                String email = req.getParameter(EMAIL);
                String username = req.getParameter(USERNAME);
                String password = req.getParameter(PASS);
                String personalPhone = req.getParameter("personalPhone");
                String workPhone = req.getParameter("workPhone");
                String homeAddress = req.getParameter("homeAddress");
                String workAddress = req.getParameter("workAddress");
                String dateOfBirth = req.getParameter(BIRTHDATE);
                String gender = req.getParameter(GENDER);
                String addInfo = req.getParameter(ABOUT);

                List<String> violations = validate(req);
                if (!violations.isEmpty()) {
                    req.setAttribute("violations", violations);
                    req.setAttribute(FIRSTNAME, firstName);
                    req.setAttribute(LASTNAME, lastName);
                    req.setAttribute(MIDDLENAME, middleName);
                    req.setAttribute(EMAIL, email);
                    req.setAttribute(USERNAME, username);
                    req.setAttribute(ABOUT, addInfo);
                    req.setAttribute(BIRTHDATE, dateOfBirth);
                    req.setAttribute(GENDER, gender);
                    req.getRequestDispatcher(REG).include(req, resp);
                } else {

                    Account account = new Account(firstName, lastName, username, formatter.parse(dateOfBirth), email);
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

                    req.setAttribute(EMAIL, email);
                    HttpSession session = req.getSession();
                    session.setAttribute("account", dbAccount);
                    session.setAttribute(USERNAME, dbAccount.getUserName());
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + dbAccount.getId());
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("Cancel")) {
            req.getRequestDispatcher(LOGIN).forward(req, resp);
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

}