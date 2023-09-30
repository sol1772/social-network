package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final AccountService accountService;

    @Autowired
    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping({"/", "/login"})
    public String viewLogin(@RequestParam(name = "email", required = false) String email,
                            HttpSession session, Model model) {
        if (email == null) {
            String errorMessage = null;
            if (session != null) {
                AuthenticationException e = (AuthenticationException)
                        session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                if (e != null) {
                    errorMessage = e.getMessage();
                }
            }
            if (errorMessage != null) {
                model.addAttribute("error", errorMessage);
            }
            return "login";
        } else {
            Account account = accountService.getAccountByEmail(email);
            return "redirect:/account/" + account.getId();
        }
    }

}
