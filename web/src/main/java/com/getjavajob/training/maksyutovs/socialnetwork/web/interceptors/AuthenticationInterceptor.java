package com.getjavajob.training.maksyutovs.socialnetwork.web.interceptors;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationInterceptor.class.getName());
    private final AccountService accountService;
    @Autowired
    private ServletContext sc;
    private Set<String> authorizationUrls;

    public AuthenticationInterceptor(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        LOGGER.log(Level.INFO, () -> "[preHandle][" + req + "]" + "[" + req.getMethod() + "]" + req.getRequestURI());
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

        Properties properties = (Properties) sc.getAttribute("ConfigProperties");
        String authPattern = properties.getProperty("authorizationUrls");
        if (!StringUtils.isEmpty(authPattern)) {
            authorizationUrls = new HashSet<>(Arrays.asList(authPattern.split("\\s*,\\s*")));
        }
        if (authorizationUrls.contains(servletPath)) {
            if (session.isNew() || account == null) {
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    Optional<Cookie> email = Arrays.stream(req.getCookies()).filter(n -> n.getName().equals("email")).findAny();
                    Optional<Cookie> pass = Arrays.stream(req.getCookies()).filter(n -> n.getName().equals("password")).findAny();
                    if (email.isPresent() && pass.isPresent()) {
                        account = accountService.getAccountByEmail(email.get().getValue());
                        if (account.getPasswordHash().equals(pass.get().getValue())) {
                            session.setAttribute("account", account);
                            session.setAttribute("username", account.getUserName());
                            resp.sendRedirect(contextPath + "/account/" + account.getId());
                            return true;
                        }
                    }
                }
            } else {
                resp.sendRedirect(contextPath + "/account/" + account.getId());
                return true;
            }
        }
        return true;
    }

}
