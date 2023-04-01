package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet
public class AccountDataServlet extends HttpServlet {

    private AccountService accountService;

    @Override
    public void init(ServletConfig config) {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=utf-8");

        HttpSession session = req.getSession();
        session.invalidate();

        List<Account> accounts = accountService.getAccounts();
        StringBuilder sb = new StringBuilder();
        sb.append("Accounts List");
        for (Account account : accounts) {
            sb.append("<p>").append(account.getFirstName()).append(" ").append(account.getLastName()).append("<br>");
            for (Account.Phone phone : account.getPhones()) {
                sb.append(phone.getNumber()).append(" (").append(phone.getPhoneType().toString().toLowerCase()).append(")");
                sb.append("<br>");
            }
            List<Account.Address> homeAddresses = account.getAddresses().stream().filter(
                    address -> address.getAddrType() == Account.AddressType.HOME).collect(Collectors.toList());
            for (Account.Address address : homeAddresses) {
                sb.append(address.getAddress());
                sb.append("</p>");
            }
        }

        try {
            resp.getOutputStream().write(("<html><body>" + sb + "</body></html>").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}