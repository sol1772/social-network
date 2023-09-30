package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.Math.max;

@Controller
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);
    private static final String ACCOUNT = "account";
    private static final String GROUP = "group";
    private static final String UPLOAD = "upload";
    private static final String OPTION = "option";
    private static final String PATH = "path";
    private static final String ID = "id";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final int MAX_FILE_SIZE = 65535;
    private static final int ITEMS_PER_PAGE = 5;
    private static final String REDIRECT_ACC = "redirect:/account/";
    private static final String REDIRECT_GRP = "redirect:/group/";
    private final AccountService accountService;
    private final GroupService groupService;

    @Autowired
    public CommonController(AccountService accountService, GroupService groupService) {
        this.accountService = accountService;
        this.groupService = groupService;
    }

    public static InputStream getDefaultImage(String sign) {
        String pathImage = "img/";
        switch (sign) {
            case "M":
                pathImage += "profile_m.jpg";
                break;
            case "F":
                pathImage += "profile_f.jpg";
                break;
            default:
                pathImage += "group-logo.jpg";
        }
        return CommonController.class.getClassLoader().getResourceAsStream(pathImage);
    }

    @GetMapping("/upload")
    public String viewUpload(@RequestParam(OPTION) String option, @RequestParam(PATH) String path,
                             @RequestParam(name = ID, required = false) Integer id,
                             HttpSession session, ModelMap modelMap) {
        if (option.equals("Delete")) {
            return deleteImage(id, path, session);
        }
        modelMap.addAttribute(PATH, path);
        modelMap.addAttribute(ID, id.toString());
        return UPLOAD;
    }

    @PostMapping("/upload")
    public String doUpload(@RequestParam("file") MultipartFile file, @RequestParam(PATH) String path,
                           @RequestParam(name = ID, required = false) Integer id,
                           HttpSession session, ModelMap modelMap) {
        if (file.getSize() > MAX_FILE_SIZE) {
            modelMap.addAttribute(PATH, path);
            modelMap.addAttribute(ID, id.toString());
            modelMap.addAttribute(ERROR, "Max file size = 64 kb exceeded");
            return UPLOAD;
        }
        return uploadImage(file, id, path, session, modelMap);
    }

    private String uploadImage(MultipartFile file, Integer id, String path, HttpSession session, ModelMap modelMap) {
        try (InputStream fileContent = file.getInputStream()) {
            if (path.equals(ACCOUNT)) {
                Account account = (Account) session.getAttribute(ACCOUNT);
                account.setImage(fileContent.readAllBytes());
                Account dbAccount = accountService.editAccount(account);
                session.setAttribute(ACCOUNT, dbAccount);
                if (logger.isInfoEnabled()) {
                    logger.info("Updated image of account {}", dbAccount);
                }
                return REDIRECT_ACC + account.getId();
            } else if (path.equals(GROUP) && (id != null)) {
                Group group = groupService.getGroupById(id);
                group.setImage(fileContent.readAllBytes());
                groupService.editGroup(group);
                if (logger.isInfoEnabled()) {
                    logger.info("Updated image of group {}", group);
                }
                return REDIRECT_GRP + id;
            }
        } catch (IOException e) {
            modelMap.addAttribute(ERROR, "File storage error");
            return UPLOAD;
        }
        modelMap.addAttribute(EXC_MSG, "File storage error");
        return ERROR;
    }

    private String deleteImage(Integer id, String path, HttpSession session) {
        if (path.equals(ACCOUNT)) {
            Account account = (Account) session.getAttribute(ACCOUNT);
            account.setImage(null);
            Account dbAccount = accountService.editAccount(account);
            session.setAttribute(ACCOUNT, dbAccount);
            if (logger.isInfoEnabled()) {
                logger.info("Deleted image of account {}", dbAccount);
            }
            return REDIRECT_ACC + account.getId();
        } else if (path.equals(GROUP)) {
            Group group = groupService.getGroupById(id);
            group.setImage(null);
            groupService.editGroup(group);
            if (logger.isInfoEnabled()) {
                logger.info("Deleted image of group {}", group);
            }
            return REDIRECT_GRP + id;
        }
        return ERROR;
    }

    @GetMapping("/search/accounts")
    @ResponseBody
    public List<AccountDto> viewAccounts(@RequestParam(name = "q", required = false) String q,
                                         @RequestParam(name = "page", required = false) Integer page) {
        String searchString = q == null ? "" : q;
        int currentPage = page == null ? 0 : page - 1;
        return accountService.getAccountsByString(searchString, currentPage, ITEMS_PER_PAGE);
    }

    @GetMapping("/search/groups")
    @ResponseBody
    public List<Group> viewGroups(@RequestParam(name = "q", required = false) String q,
                                  @RequestParam(name = "page", required = false) Integer page) {
        String searchString = q == null ? "" : q;
        int startRow = page == null ? 1 : (page - 1) * ITEMS_PER_PAGE + 1;
        return groupService.getGroupsByString(searchString, startRow, ITEMS_PER_PAGE);
    }

    @GetMapping("/search")
    public String viewSearch(@RequestParam(name = "q", required = false) String q,
                             @RequestParam(name = "page", required = false) Integer page, Model model) {
        String searchString = q == null ? "" : q;
        int currentPage = page == null ? 0 : page - 1;
        Page<Account> pageAccounts = accountService.getPageAccountsByString(searchString, currentPage, ITEMS_PER_PAGE);
        int accountsTotal = (int) pageAccounts.getTotalElements();
        int accountPages = pageAccounts.getTotalPages();
        int groupsTotal = groupService.getGroupsCountByString(searchString);
        int groupPages = (int) Math.ceil((double) groupsTotal / ITEMS_PER_PAGE);

        model.addAttribute("q", searchString);
        model.addAttribute("page", page == null ? 1 : page);
        model.addAttribute("accountPages", accountPages);
        model.addAttribute("groupPages", groupPages);
        model.addAttribute("accountsTotal", accountsTotal);
        model.addAttribute("groupsTotal", groupsTotal);
        int maxCountPages = max(accountPages, groupPages);
        if (page != null && page > maxCountPages) {
            model.addAttribute(ERROR, "Page " + page + " exceeds total max count of pages " + maxCountPages);
        }
        return "search";
    }

    @GetMapping("/error")
    public String handleError(@RequestParam(name = "status", required = false) String statusCode, Model model) {
        if (!StringUtils.isEmpty(statusCode)) {
            String exceptionMessage;
            switch (statusCode) {
                case "401":
                    exceptionMessage = "Bad Request";
                    break;
                case "403":
                    exceptionMessage = "Sorry, you do not have permission to perform this action";
                    break;
                case "404":
                    exceptionMessage = "Page not found";
                    break;
                case "500":
                    exceptionMessage = "Internal Server Error";
                    break;
                default:
                    exceptionMessage = "Error";
                    break;
            }
            model.addAttribute(EXC_MSG, exceptionMessage);
        }
        return ERROR;
    }

}
