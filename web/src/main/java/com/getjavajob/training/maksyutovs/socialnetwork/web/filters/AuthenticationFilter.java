package com.getjavajob.training.maksyutovs.socialnetwork.web.filters;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebFilter
public class AuthenticationFilter implements Filter {

    private Set<String> authorizationUrls;
    private AccountService accountService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sc = filterConfig.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
        accountService = (AccountService) sc.getAttribute("AccountService");
        Properties properties = (Properties) sc.getAttribute("ConfigProperties");

        String authPattern = properties.getProperty("authorizationUrls");
        if (!StringUtils.isEmpty(authPattern)) {
            authorizationUrls = new HashSet<>(Arrays.asList(authPattern.split("\\s*,\\s*")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

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
                            resp.sendRedirect(contextPath + "/account?id=" + account.getId());
                            return;
                        }
                    }
                }
            } else {
                resp.sendRedirect(contextPath + "/account?id=" + account.getId());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
