package com.getjavajob.training.maksyutovs.socialnetwork.web.interceptors;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import com.getjavajob.training.maksyutovs.socialnetwork.web.config.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    @Autowired
    private ServletContext sc;
    private Set<String> authorizationUrls;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Account account;
        AccountDetails accountDetails = SecurityUtils.getAccountDetails();
        if (accountDetails != null) {
            account = accountDetails.getAccount();
        } else {
            account = (Account) req.getSession().getAttribute("account");
        }
        if (account != null) {
            String servletPath = req.getServletPath();
            Properties properties = (Properties) sc.getAttribute("ConfigProperties");
            String authPattern = properties.getProperty("authorizationUrls");
            if (!StringUtils.isEmpty(authPattern)) {
                authorizationUrls = new HashSet<>(Arrays.asList(authPattern.split("\\s*,\\s*")));
            }
            if (authorizationUrls.contains(servletPath)) {
                resp.sendRedirect(req.getContextPath() + "/account/" + account.getId());
                if (logger.isInfoEnabled()) {
                    logger.info("[preHandle][{}] [{}] {}", req, req.getMethod(), req.getRequestURI());
                }
                return false;
            }
        }
        return HandlerInterceptor.super.preHandle(req, resp, handler);
    }

}
