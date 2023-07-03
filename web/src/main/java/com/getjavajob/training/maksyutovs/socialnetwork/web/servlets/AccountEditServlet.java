package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.Utils;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
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
import java.util.Locale;
import java.util.Objects;

@WebServlet
public class AccountEditServlet extends HttpServlet {

    private static final String ACCOUNT = "account";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String MIDDLENAME = "middleName";
    private static final String USERNAME = "username";
    private static final String ABOUT = "addInfo";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String GENDER = "gender";
    private static final String EDIT_URL = "/WEB-INF/jsp/account-edit.jsp";
    private AccountService accountService;
    private GroupService groupService;

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void init() {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        accountService = Objects.requireNonNull(context).getBean(AccountService.class);
        groupService = Objects.requireNonNull(context).getBean(GroupService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);
        try {
            if (account == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                req.setAttribute(ACCOUNT, account);
                req.setAttribute(GENDER, String.valueOf(account.getGender() != null ?
                        account.getGender().toString().charAt(0) : 'M'));
                req.setAttribute("personalPhone", account.getPhones().stream().filter(phone ->
                        PhoneType.PERSONAL.equals(phone.getPhoneType())).map(Phone::getNumber).findAny().orElse(""));
                req.setAttribute("personalPhoneId", account.getPhones().stream().filter(phone ->
                        PhoneType.PERSONAL.equals(phone.getPhoneType())).map(Phone::getId).findAny().orElse(null));
                req.setAttribute("workPhone", account.getPhones().stream().filter(phone ->
                        PhoneType.WORK.equals(phone.getPhoneType())).map(Phone::getNumber).findAny().orElse(""));
                req.setAttribute("workPhoneId", account.getPhones().stream().filter(phone ->
                        PhoneType.WORK.equals(phone.getPhoneType())).map(Phone::getId).findAny().orElse(null));
                req.setAttribute("homeAddress", account.getAddresses().stream().filter(addr ->
                        AddressType.HOME.equals(addr.getAddrType())).map(Address::getAddr).findAny().orElse(""));
                req.setAttribute("homeAddressId", account.getAddresses().stream().filter(addr ->
                        AddressType.HOME.equals(addr.getAddrType())).map(Address::getId).findAny().orElse(null));
                req.setAttribute("workAddress", account.getAddresses().stream().filter(addr ->
                        AddressType.WORK.equals(addr.getAddrType())).map(Address::getAddr).findAny().orElse(""));
                req.setAttribute("workAddressId", account.getAddresses().stream().filter(addr ->
                        AddressType.WORK.equals(addr.getAddrType())).map(Address::getId).findAny().orElse(null));
                req.getRequestDispatcher(EDIT_URL).forward(req, resp);
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
                    fillAccountDataFromRequest(account, req);
                    Account dbAccount = accountService.editAccount(account);
                    // delete not actual numbers
                    for (Phone phone : dbAccount.getPhones()) {
                        Phone phoneFoundById = account.getPhones().stream().filter
                                (p -> phone.getId() == p.getId()).findAny().orElse(null);
                        if (phoneFoundById == null) {
                            accountService.deleteAccountData(phone.getPhoneType(), phone.getId());
                        }
                    }
                    // update existing and write new numbers
                    for (Phone phone : account.getPhones()) {
                        if (phone.getId() == 0) {
                            dbAccount = accountService.addAccountData(account, phone.getNumber(), phone.getPhoneType());
                        } else {
                            dbAccount = accountService.editAccountData(
                                    phone.getNumber(), phone.getPhoneType(), phone.getId(), account);
                        }
                    }
                    for (Address addr : account.getAddresses()) {
                        if (addr.getId() == 0) {
                            dbAccount = accountService.addAccountData(account, addr.getAddr(), addr.getAddrType());
                        } else {
                            dbAccount = accountService.editAccountData(
                                    addr.getAddr(), addr.getAddrType(), addr.getId(), account);
                        }
                    }
                    req.setAttribute(ACCOUNT, dbAccount);
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + dbAccount.getId());
                } else if (command.equals("Cancel")) {
                    req.setAttribute(ACCOUNT, account);
                    req.setAttribute("groups", groupService.getGroupsByAccount(account));
                    req.setAttribute("posts", accountService.getMessages(account, account, MessageType.POST));
                    req.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(req, resp);
                }
            }
        } catch (ServletException | IOException e) {
            req.setAttribute("exceptionMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
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

    void fillAccountDataFromRequest(Account account, HttpServletRequest req) {
        // phones
        String[] phoneNums = req.getParameterValues("phoneNum");
        String[] phoneTypes = req.getParameterValues("phoneType");
        String[] phoneIds = req.getParameterValues("phoneId");
        account.getPhones().clear();
        if (phoneNums != null && phoneTypes != null) {
            for (int i = 0; i < phoneNums.length; i++) {
                String phoneNum = phoneNums[i];
                PhoneType phoneType = PhoneType.valueOf(phoneTypes[i].toUpperCase(Locale.getDefault()));
                int phoneId = (phoneIds == null || phoneIds.length < i + 1) ? 0 : tryParse(phoneIds[i]);
                account.getPhones().add(new Phone(account, phoneId, phoneNum, phoneType));
            }
        }
        // addresses
        String homeAddress = req.getParameter("homeAddress");
        if (!StringUtils.isEmpty(homeAddress)) {
            int id = tryParse(req.getParameter("homeAddressId"));
            Address addr = account.getAddresses().stream().filter(p -> id == p.getId()).findAny().orElse(null);
            if (addr == null) {
                account.getAddresses().add(new Address(account, id, homeAddress, AddressType.HOME));
            } else {
                addr.setAddr(homeAddress);
            }
        }
        String workAddress = req.getParameter("workAddress");
        if (!StringUtils.isEmpty(workAddress)) {
            int id = tryParse(req.getParameter("workAddressId"));
            Address addr = account.getAddresses().stream().filter(p -> id == p.getId()).findAny().orElse(null);
            if (addr == null) {
                account.getAddresses().add(new Address(account, id, workAddress, AddressType.WORK));
            } else {
                addr.setAddr(workAddress);
            }
        }
    }

}
