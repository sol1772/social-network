package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.AddressType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountValidator;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.io.IOUtils;
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
    private static final String LOGIN = "login";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final String REDIRECT_ACC = "redirect:/account/";
    private final AccountService accountService;
    private final GroupService groupService;
    private final AccountValidator accountValidator;
    private final Mapper mapper = new Mapper();

    @Autowired
    public AccountController(AccountService accountService, GroupService groupService, AccountValidator validator) {
        this.accountService = accountService;
        this.groupService = groupService;
        this.accountValidator = validator;
    }

    @GetMapping("/{id}")
    public String viewAccount(@PathVariable Integer id, Model model) {
        Account account = accountService.getFullAccountById(id);
        if (account != null) {
            model.addAttribute(ACCOUNT, account);
            model.addAttribute("groups", groupService.getGroupsByAccount(account));
            model.addAttribute("posts", accountService.getMessages(account, account, MessageType.POST));
            return ACCOUNT;
        } else {
            model.addAttribute(EXC_MSG, "Account with id " + id + " not found");
            return ERROR;
        }
    }

    @GetMapping("/add")
    public String viewRegistration() {
        return REG;
    }

    @PostMapping("/add")
    public String addAccount(@ModelAttribute AccountDto accountDto, @NotNull BindingResult result,
                             @RequestParam Map<String, String> p, HttpSession session, Model model) {
        String option = p.get("submit");
        if ("Cancel".equals(option)) {
            return "redirect:/login";
        }
        Account account = mapper.toNewAccount(accountDto);
        accountValidator.validate(account, result);
        if (result.hasErrors()) {
            model.addAllAttributes(p);
            model.addAttribute(ERROR, result.getAllErrors().get(0).getDefaultMessage());
            return REG;
        }
        if ("Register".equals(option)) {
            Account dbAccount = accountService.registerAccount(account);
            session.setAttribute(ACCOUNT, dbAccount);
            session.setAttribute("username", dbAccount.getUserName());
            return REDIRECT_ACC + dbAccount.getId();
        }
        return LOGIN;
    }

    @GetMapping("/{id}/edit")
    public String viewAccountEdit(@PathVariable Integer id, Model model) {
        Account account = accountService.getFullAccountById(id);
        if (account == null) {
            return LOGIN;
        } else {
            model.addAttribute(ACCOUNT, account);
            model.addAttribute("gender", String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            model.addAttribute("homeAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.HOME.equals(addr.getAddrType())).findAny().orElse(null));
            model.addAttribute("workAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.WORK.equals(addr.getAddrType())).findAny().orElse(null));
            return "account-edit";
        }
    }

    @PostMapping("/{id}/edit")
    public String saveAccount(@ModelAttribute("account") AccountDto accountDto, @NotNull BindingResult result,
                              @RequestParam("submit") String option, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        Account account = accountService.getFullAccountById(accountDto.getId());
        if (option.equals("Save")) {
            Account dbAccount = accountService.editAccount(mapper.toAccount(account, accountDto));
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
    public byte[] getImage(@PathVariable Integer id, HttpSession session) {
        Account account = (Account) session.getAttribute(ACCOUNT);
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
