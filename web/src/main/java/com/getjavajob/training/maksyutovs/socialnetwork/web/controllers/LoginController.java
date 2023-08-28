package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN = "login";
    private static final String EMAIL = "email";
    private static final String PASS = "password";
    private static final String ERROR = "error";
    private static final String REDIRECT_ACC = "redirect:/account/";
    private final AccountService accountService;

    @Autowired
    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping({"/", "/login"})
    public String viewLogin(@RequestParam(name = EMAIL, required = false) String email) {
        if (email == null) {
            return LOGIN;
        } else {
            Account account = accountService.getAccountByEmail(email);
            return REDIRECT_ACC + account.getId();
        }
    }

    @PostMapping("/login")
    public ModelAndView doLogin(@RequestParam(EMAIL) String email, @RequestParam(PASS) String password,
                                HttpSession session, HttpServletRequest req, HttpServletResponse resp) {
        var mvLogin = new ModelAndView(LOGIN);
        if (email.isEmpty()) {
            mvLogin.addObject(ERROR, "Enter email");
            return mvLogin;
        }
        Account account = accountService.getAccountByEmail(email);
        if (account == null) {
            mvLogin.addObject(ERROR, "Account not found for email: " + email);
            return mvLogin;
        } else {
            if (accountService.passwordIsValid(password, account)) {
                req.setAttribute(EMAIL, email);
                if (req.getParameter("rememberMe") != null) {
                    Cookie cookieEmail = new Cookie(EMAIL, email);
                    Cookie cookiePass = new Cookie(PASS, account.getPasswordHash());
                    cookieEmail.setMaxAge(86400);
                    cookiePass.setMaxAge(86400);
                    resp.addCookie(cookieEmail);
                    resp.addCookie(cookiePass);
                }
                session.setAttribute("account", account);
                session.setAttribute("username", account.getUserName());
                if (logger.isInfoEnabled()) {
                    logger.info("Account {} logged in", account);
                }
                return new ModelAndView(REDIRECT_ACC + account.getId());
            } else {
                mvLogin.addObject(ERROR, "Wrong password");
                if (logger.isInfoEnabled()) {
                    logger.info("Wrong password entered for account {}", account);
                }
                return mvLogin;
            }
        }
    }

    @GetMapping("/logout")
    public String viewLogout(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
        }
        session.invalidate();
        if (logger.isInfoEnabled()) {
            logger.info("Account {} logged out", account);
        }
        return ("redirect:/login");
    }

}
