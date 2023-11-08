package com.getjavajob.training.maksyutovs.socialnetwork.web.config;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth)
            throws IOException, ServletException {
        if (auth != null && logger.isInfoEnabled()) {
            Account account = auth.getPrincipal() == null ? null : ((AccountDetails) auth.getPrincipal()).getAccount();
            String refererUrl = req.getHeader("Referer");
            LOGGER.info("Account {} logged out from {}", account, refererUrl);
        }
        super.onLogoutSuccess(req, resp, auth);
    }

}