package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/account")
public class AccountController {

    private static final Logger LOGGER = Logger.getLogger(AccountController.class.getName());
    private static final String REG = "registration";
    private static final String ACCOUNT = "account";
    private static final String GENDER = "gender";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final String REDIRECT_ACC = "redirect:/account/";
    private final AccountService accountService;
    private final GroupService groupService;

    @Autowired
    public AccountController(AccountService accountService, GroupService groupService) {
        this.accountService = accountService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}")
    public String viewAccount(@PathVariable Integer id, Model model) {
        Account account = accountService.getAccountById(id);
        if (account == null) {
            model.addAttribute(EXC_MSG, "Account with id + " + id + " not found");
            return ERROR;
        } else {
            model.addAttribute(ACCOUNT, account);
            model.addAttribute("groups", groupService.getGroupsByAccount(account));
            model.addAttribute("posts", accountService.getMessages(account, account, MessageType.POST));
            return ACCOUNT;
        }
    }

    @GetMapping("/add")
    public String viewRegistration() {
        return REG;
    }

    @PostMapping("/add")
    public String addAccount(@ModelAttribute Account account, @NotNull BindingResult result,
                             @RequestParam Map<String, String> p, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        if ("Register".equals(p.get("submit"))) {
            if (accountService.accountExists(account.getEmail())) {
                model.addAllAttributes(p);
                model.addAttribute(ERROR, "Account with email '" + account.getEmail() + "' already exists");
                return REG;
            }
            fillAccount(account, p);
            Account dbAccount = accountService.registerAccount(account);
            session.setAttribute(ACCOUNT, dbAccount);
            session.setAttribute("username", dbAccount.getUserName());
            return REDIRECT_ACC + dbAccount.getId();
        }
        return "login";
    }

    private void fillAccount(Account account, Map<String, String> p) {
        account.setPasswordHash(account.hashPassword(p.get("password")));
        if (!StringUtils.isEmpty(p.get("personalPhone"))) {
            account.getPhones().add(new Phone(account, 0, p.get("personalPhone"), PhoneType.PERSONAL));
        }
        if (!StringUtils.isEmpty(p.get("workPhone"))) {
            account.getPhones().add(new Phone(account, 0, p.get("workPhone"), PhoneType.WORK));
        }
        if (!StringUtils.isEmpty(p.get("homeAddress"))) {
            account.getAddresses().add(new Address(account, 0, p.get("homeAddress"), AddressType.HOME));
        }
        if (!StringUtils.isEmpty(p.get("workAddress"))) {
            account.getAddresses().add(new Address(account, 0, p.get("workAddress"), AddressType.WORK));
        }
    }

    @GetMapping("/{id}/edit")
    public String viewAccountEdit(@SessionAttribute(ACCOUNT) Account account, Model model) {
        if (account == null) {
            return "login";
        } else {
            model.addAttribute(ACCOUNT, account);
            model.addAttribute(GENDER, String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            model.addAttribute("homeAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.HOME.equals(addr.getAddrType())).findAny().orElse(null));
            model.addAttribute("workAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.WORK.equals(addr.getAddrType())).findAny().orElse(null));
            return "account-edit";
        }
    }

    @PostMapping("/{id}/edit")
    public String saveAccount(@ModelAttribute("account") Account account, @NotNull BindingResult result,
                              @RequestParam("submit") String option, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        if (option.equals("Save")) {
            Account dbAccount = accountService.editAccount(account);
            dbAccount = accountService.editAccountData(dbAccount, account);
            session.setAttribute(ACCOUNT, dbAccount);
            session.setAttribute("username", dbAccount.getUserName());
            model.addAttribute(ACCOUNT, dbAccount);
            return REDIRECT_ACC + dbAccount.getId();
        } else if (option.equals("Cancel")) {
            model.addAttribute(ACCOUNT, account);
            return REDIRECT_ACC + account.getId();
        }
        return "account-edit";
    }

    @GetMapping("/{id}/image")
    @ResponseBody
    public byte[] getImage(@PathVariable Integer id,
                           @SessionAttribute(name = "account", required = false) Account account) {
        if (account == null || account.getId() != id) {
            account = accountService.getAccountById(id);
        }
        byte[] image = account.getImage();
        if (image == null || image.length == 0) {
            try {
                image = IOUtils.toByteArray(CommonController.getDefaultImage(account.getGender().toString()));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
        return image;
    }

}
