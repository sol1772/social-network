package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.AddressType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.storage.FileStorage;
import com.getjavajob.training.maksyutovs.socialnetwork.service.storage.JsonStorage;
import com.getjavajob.training.maksyutovs.socialnetwork.service.storage.XmlStorage;
import com.getjavajob.training.maksyutovs.socialnetwork.service.validation.AccountValidator;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private static final String REG = "registration";
    private static final String ACCOUNT = "account";
    private static final String ACCOUNT_EDIT = "account-edit";
    private static final String LOGIN = "login";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final String REDIRECT_ACC = "redirect:/account/";
    private final AccountService accountService;
    private final GroupService groupService;
    private final AccountValidator accountValidator;
    private final Mapper mapper = new Mapper();
    private final FileStorage<Account> xmlStorage = new XmlStorage<>(Account.class);
    private final FileStorage<Account> jsonStorage = new JsonStorage<>(Account.class);

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
            if (logger.isInfoEnabled()) {
                logger.info("Created account {}", dbAccount);
            }
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
            return ACCOUNT_EDIT;
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
            if (logger.isInfoEnabled()) {
                logger.info("Saved account {}", dbAccount);
            }
            return REDIRECT_ACC + dbAccount.getId();
        } else if (option.equals("Cancel")) {
            model.addAttribute(ACCOUNT, account);
            return REDIRECT_ACC + account.getId();
        }
        return ACCOUNT_EDIT;
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
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage());
                }
            }
        }
        return image;
    }

    @GetMapping("/{id}/toXml")
    public void saveToXml(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Account account = accountService.getFullAccountById(id);
        File file = xmlStorage.store(account);
        response.setContentType("text/xml");
        // offers upload to file, not direct display in the browser
        response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
        xmlStorage.writeFileToOutputStream(file, response.getOutputStream());
        if (logger.isInfoEnabled()) {
            logger.info("Account {} saved to xml-file", account);
        }
    }

    @GetMapping("/{id}/toJson")
    public void saveToJson(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Account account = accountService.getFullAccountById(id);
        File file = jsonStorage.store(account);
        response.setContentType("application/json");
        response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
        jsonStorage.writeFileToOutputStream(file, response.getOutputStream());
        if (logger.isInfoEnabled()) {
            logger.info("Account {} saved to json-file", account);
        }
    }

    @GetMapping("/{id}/fromFile")
    public String viewAccountFromFile(@PathVariable String id) {
        return "file-loader";
    }

    @PostMapping("/{id}/fromFile")
    public String loadFromFile(@PathVariable Integer id, Model model,
                               @RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        String mimeType = Files.probeContentType(file.toPath());
        Account account = null;
        if ("text/xml".equals(mimeType)) {
            account = xmlStorage.load(file);
        } else if ("application/json".equals(mimeType)) {
            account = jsonStorage.load(file);
        }
        model.addAttribute(ACCOUNT, account);
        if (account != null) {
            model.addAttribute("gender", String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            model.addAttribute("homeAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.HOME.equals(addr.getAddrType())).findAny().orElse(null));
            model.addAttribute("workAddress", account.getAddresses().stream().filter(addr ->
                    AddressType.WORK.equals(addr.getAddrType())).findAny().orElse(null));
        }
        if (logger.isInfoEnabled()) {
            logger.info("Account {} loaded from file", account);
        }
        return ACCOUNT_EDIT;
    }

}
