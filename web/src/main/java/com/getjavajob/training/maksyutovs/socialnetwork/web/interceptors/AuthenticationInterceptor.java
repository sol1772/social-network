package com.getjavajob.training.maksyutovs.socialnetwork.web.interceptors;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import com.getjavajob.training.maksyutovs.socialnetwork.web.config.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(@NotNull HttpServletRequest req, @NotNull HttpServletResponse resp,
                             @NotNull Object handler) throws Exception {
        Account account;
        AccountDetails accountDetails = SecurityUtils.getAccountDetails();
        if (accountDetails != null) {
            account = accountDetails.getAccount();
        } else {
            account = (Account) req.getSession().getAttribute("account");
        }
        if (account != null) {
            resp.sendRedirect(req.getContextPath() + "/account/" + account.getId());
            if (logger.isInfoEnabled()) {
                logger.info("Account {} redirected from {} to account page", account, req.getRequestURI());
            }
            return false;
        }
        return HandlerInterceptor.super.preHandle(req, resp, handler);
    }

}
