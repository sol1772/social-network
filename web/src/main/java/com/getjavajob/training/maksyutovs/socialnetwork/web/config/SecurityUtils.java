package com.getjavajob.training.maksyutovs.socialnetwork.web.config;

import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AccountDetails getAccountDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth instanceof AnonymousAuthenticationToken) ?
                null : (AccountDetails) auth.getPrincipal();
    }

    public static void setSecurityContext(AccountDetails accountDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                accountDetails, accountDetails.getPassword(), accountDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

}
