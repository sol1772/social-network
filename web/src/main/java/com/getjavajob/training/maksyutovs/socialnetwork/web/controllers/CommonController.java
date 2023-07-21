package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.Math.max;

@Controller
public class CommonController {

    private static final String ACCOUNT = "account";
    private static final String GROUP = "group";
    private static final String UPLOAD = "upload";
    private static final String OPTION = "option";
    private static final String PATH = "path";
    private static final String ID = "id";
    private static final String IMAGE = "image";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final int MAX_FILE_SIZE = 65535;
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
                Account dbAccount = accountService.editAccount(account, IMAGE, fileContent);
                session.setAttribute(ACCOUNT, dbAccount);
                return REDIRECT_ACC + account.getId();
            } else if (path.equals(GROUP) && (id != null)) {
                Group group = groupService.getGroupById(id);
                groupService.editGroup(group, IMAGE, fileContent);
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
            Account dbAccount = accountService.editAccount(account, IMAGE, null);
            session.setAttribute(ACCOUNT, dbAccount);
            return REDIRECT_ACC + account.getId();
        } else if (path.equals(GROUP)) {
            Group group = groupService.getGroupById(id);
            groupService.editGroup(group, IMAGE, null);
            return REDIRECT_GRP + id;
        }
        return ERROR;
    }

    @GetMapping("/search")
    public ModelAndView viewSearch(@RequestParam("q") String q, @RequestParam("page") String p) {
        final int recordsPerPage = 5;
        var mvSearch = new ModelAndView("search");
        String searchString = q == null ? "" : q;
        int accountsTotal = accountService.getAccountsCountByString(searchString, 0, 0);
        int accountsPages = accountsTotal % recordsPerPage == 0 ?
                accountsTotal / recordsPerPage : accountsTotal / recordsPerPage + 1;
        int groupsTotal = groupService.getGroupsCountByString(searchString, 0, 0);
        int groupsPages = groupsTotal % recordsPerPage == 0 ?
                groupsTotal / recordsPerPage : groupsTotal / recordsPerPage + 1;
        int maxCountPages = max(accountsPages, groupsPages);
        int startRow = 1;
        if (p != null) {
            int page = Integer.parseInt(p);
            if (maxCountPages > 0 && page > maxCountPages) {
                mvSearch.addObject(ERROR, "Page " + p + " exceeds total max count of pages " + maxCountPages);
            }
            startRow = (page - 1) * recordsPerPage + 1;
        }

        List<Account> accounts = accountService.getAccountsByString(searchString, startRow, recordsPerPage);
        List<Group> groups = groupService.getGroupsByString(searchString, startRow, recordsPerPage);

        mvSearch.addObject("accounts", accounts);
        mvSearch.addObject("groups", groups);
        mvSearch.addObject("q", searchString);
        mvSearch.addObject("accountsTotal", Integer.toString(accountsTotal));
        mvSearch.addObject("accountsPages", Integer.toString(accountsPages));
        mvSearch.addObject("groupsTotal", Integer.toString(groupsTotal));
        mvSearch.addObject("groupsPages", Integer.toString(groupsPages));
        return mvSearch;
    }

    @GetMapping("/error")
    public String handleError() {
        return ERROR;
    }

}
