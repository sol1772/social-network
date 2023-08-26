package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Role;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.GroupDto;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private static final String GROUP = "group";
    private static final String TITLE = "title";
    private static final String ACCOUNT = "account";
    private static final String ERROR = "error";
    private static final String EXC_MSG = "exceptionMessage";
    private static final String REDIRECT_GRP = "redirect:/group/";
    private final GroupService groupService;
    private final AccountService accountService;
    private final Mapper mapper = new Mapper();

    @Autowired
    public GroupController(GroupService groupService, AccountService accountService) {
        this.groupService = groupService;
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Integer id, Model model) {
        Group group = groupService.getFullGroupById(id);
        if (group == null) {
            model.addAttribute(EXC_MSG, "Group with id + " + id + " not found");
            return ERROR;
        } else {
            model.addAttribute(GROUP, group);
            model.addAttribute(ACCOUNT, accountService.getFullAccountById(group.getCreatedBy()));
            return GROUP;
        }
    }

    @GetMapping("/add")
    public String viewAddGroup() {
        return "group-add";
    }

    @PostMapping("/add")
    public String addGroup(@ModelAttribute GroupDto groupDto, @NotNull BindingResult result,
                           @RequestParam Map<String, String> p, HttpSession session, Model model) {
        Account account = (Account) session.getAttribute(ACCOUNT);
        if (account == null) {
            return "login";
        }
        String option = p.get("submit");
        if ("Cancel".equals(option)) {
            return "redirect:/account/" + account.getId();
        }
        if (result.hasErrors()) {
            model.addAllAttributes(p);
            model.addAttribute(ERROR, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (p.get(TITLE).isEmpty()) {
            model.addAttribute(ERROR, "Enter title");
        } else if (groupService.getGroupByTitle(p.get(TITLE)) != null) {
            model.addAttribute(ERROR, "Group with title '" + p.get(TITLE) + "' already exists");
        }
        if (model.getAttribute(ERROR) != null) {
            return "group-add";
        }
        if ("Create".equals(option)) {
            Group group = mapper.toNewGroup(groupDto);
            group.setCreatedBy(account.getId());
            group.getMembers().add(new GroupMember(group, account, Role.ADMIN));
            Group dbGroup = groupService.createGroup(group);
            logger.info("Created group {}", dbGroup);
            return REDIRECT_GRP + dbGroup.getId();
        }
        return "redirect:/account/" + account.getId();
    }

    @GetMapping("/{id}/edit")
    public String viewEditGroup(@PathVariable Integer id, Model model) {
        model.addAttribute(GROUP, groupService.getGroupById(id));
        return "group-edit";
    }

    @PostMapping("/{id}/edit")
    public String editGroup(@ModelAttribute GroupDto groupDto, @NotNull BindingResult result,
                            HttpServletRequest req, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(EXC_MSG, result.getAllErrors().get(0).toString());
            return ERROR;
        }
        Account account = (Account) session.getAttribute(ACCOUNT);
        if (account == null) {
            return "login";
        } else {
            String option = req.getParameter("submit");
            Group group = groupService.getFullGroupById(groupDto.getId());
            if (option.equals("Save")) {
                Group dbGroup = groupService.editGroup(mapper.toGroup(group, groupDto));
                logger.info("Saved group {}", dbGroup);
                return REDIRECT_GRP + dbGroup.getId();
            } else {
                return REDIRECT_GRP + group.getId();
            }
        }
    }

    @GetMapping("/{id}/image")
    @ResponseBody
    public byte[] getImage(@PathVariable Integer id) {
        byte[] image = groupService.getGroupById(id).getImage();
        if (image == null) {
            try {
                image = IOUtils.toByteArray(CommonController.getDefaultImage("G"));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return image;
    }

}
