package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Role;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/group")
public class GroupController {

    private static final Logger LOGGER = Logger.getLogger(GroupController.class.getName());
    private static final String GROUP = "group";
    private static final String TITLE = "title";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final String REDIRECT_GRP = "redirect:/group/";
    private final GroupService groupService;
    private final AccountService accountService;

    @Autowired
    public GroupController(GroupService groupService, AccountService accountService) {
        this.groupService = groupService;
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Integer id, Model model) {
        Group group = groupService.getGroupById(id);
        if (group == null) {
            model.addAttribute(EXC_MSG, "Group with id + " + id + " not found");
            return ERROR;
        } else {
            model.addAttribute(GROUP, group);
            model.addAttribute("owner", accountService.getAccountById(group.getCreatedBy()));
            return GROUP;
        }
    }

    @GetMapping("/add")
    public String viewAddGroup() {
        return "group-add";
    }

    @PostMapping("/add")
    public String addGroup(@ModelAttribute Group group, @NotNull BindingResult result,
                           @RequestParam Map<String, String> p, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "login";
        } else {
            if ("Create".equals(p.get("submit"))) {
                if (p.get(TITLE).isEmpty()) {
                    model.addAttribute(ERROR, "Enter title");
                    return "group-add";
                }
                group.setCreatedBy(account.getId());
                group.getMembers().add(new GroupMember(group, account, Role.ADMIN));
                Group dbGroup = groupService.createGroup(group);
                return REDIRECT_GRP + dbGroup.getId();
            }
        }
        return "redirect:/account/" + account.getId();
    }

    @GetMapping("/{id}/edit")
    public String viewEditGroup(@PathVariable Integer id, Model model) {
        model.addAttribute(GROUP, groupService.getGroupById(id));
        return "group-edit";
    }

    @PostMapping("/{id}/edit")
    public String editGroup(@ModelAttribute Group group, @NotNull BindingResult result,
                            HttpServletRequest req, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "login";
        } else {
            String option = req.getParameter("submit");
            if (option.equals("Save")) {
                if (group.getTitle().isEmpty()) {
                    model.addAttribute(ERROR, "Enter title");
                    return "group-edit";
                }
                group.setCreatedBy(account.getId());
                group.getMembers().add(new GroupMember(group, account, Role.ADMIN));
                Group dbGroup = groupService.editGroup(group);
                return REDIRECT_GRP + dbGroup.getId();
            } else if (option.equals("Cancel")) {
                return REDIRECT_GRP + group.getId();
            }
        }
        return REDIRECT_GRP + group.getId();
    }

    @GetMapping("/{id}/image")
    @ResponseBody
    public byte[] getImage(@PathVariable Integer id) {
        byte[] image = groupService.getGroupById(id).getImage();
        if (image == null) {
            try {
                image = IOUtils.toByteArray(CommonController.getDefaultImage("G"));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
        return image;
    }

}
