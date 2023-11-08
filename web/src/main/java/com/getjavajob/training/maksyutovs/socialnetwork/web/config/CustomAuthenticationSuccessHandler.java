package com.getjavajob.training.maksyutovs.socialnetwork.web.config;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth)
            throws IOException {
        handle(req, resp, auth);
        clearAuthenticationAttributes(req);
    }

    protected void handle(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException {
        String targetUrl = "";
        if (auth != null && logger.isInfoEnabled()) {
            Account account = auth.getPrincipal() == null ? null : ((AccountDetails) auth.getPrincipal()).getAccount();
            if (account != null) {
                HttpSession session = req.getSession();
                session.setAttribute("account", account);
                session.setAttribute("username", account.getUserName());
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                targetUrl = "/account/" + account.getId();
                if (logger.isInfoEnabled()) {
                    String refererUrl = req.getHeader("Referer") + targetUrl;
                    logger.info("Account {} logged in to {}", account, refererUrl);
                }
            } else {
                targetUrl = "/login";
            }
        }

        if (resp.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(req, resp, targetUrl);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
