package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class MessageController {

    private static final String MESSAGES = "messages";
    private static final String MSG_ACC = "messages_account";
    private static final String ACCOUNT = "account";
    private static final String TRG_ACCOUNT = "targetAccount";
    private static final String REDIRECT_MSG = "redirect:/messages_account/";
    private static final String REDIRECT_ACC = "redirect:/account/";
    private static final String REPORT = "report";
    private final AccountService accountService;

    @Autowired
    public MessageController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/messages")
    public String viewMessageAccounts(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute(ACCOUNT);
        model.addAttribute(ACCOUNT, account);
        model.addAttribute("accounts", accountService.getTargetAccounts(account, MessageType.PERSONAL));
        return MESSAGES;
    }

    @GetMapping("/messages_account")
    public String viewMessagesByAccount(@RequestParam("trgId") Integer trgId, HttpSession session, Model model) {
        Account account = (Account) session.getAttribute(ACCOUNT);
        Account targetAccount = accountService.getAccountById(trgId);
        model.addAttribute(ACCOUNT, account);
        model.addAttribute(TRG_ACCOUNT, targetAccount);
        model.addAttribute(MESSAGES, accountService.getMessages(account, targetAccount, MessageType.PERSONAL));
        return MSG_ACC;
    }

    @PostMapping("/messages_account")
    public String doMessagesByAccount(@RequestParam("submit") String submit, @RequestParam("trgId") Integer trgId,
                                      @RequestParam(name = "message", required = false) String message,
                                      @RequestParam(name = "msgId", required = false) Integer msgId,
                                      HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Account account = (Account) session.getAttribute(ACCOUNT);
        Account targetAccount = accountService.getAccountById(trgId);
        if (submit.equals("Send")) {
            return sendMessage(account, targetAccount, message, model, redirectAttrs);
        } else if (submit.equals("delMsg")) {
            return deleteMessage(account, targetAccount, msgId, model, redirectAttrs);
        }
        return MSG_ACC;
    }

    protected String sendMessage(Account account, Account targetAccount, String messageStr, Model model,
                                 RedirectAttributes redirectAttrs) {
        MessageType type = account.getId() == targetAccount.getId() ? MessageType.POST : MessageType.PERSONAL;
        if (StringUtils.isEmpty(messageStr)) {
            model.addAttribute(REPORT, "Empty message");
            model.addAttribute(ACCOUNT, account);
            model.addAttribute(TRG_ACCOUNT, targetAccount);
            model.addAttribute(MESSAGES, accountService.getMessages(account, targetAccount, type));
        } else {
            Message message = new Message(account, targetAccount, type, messageStr);
            boolean messageSent = accountService.sendMessage(message);
            if (messageSent) {
                if (type == MessageType.PERSONAL) {
                    redirectAttrs.addAttribute("trgId", targetAccount.getId());
                    return REDIRECT_MSG;
                } else {
                    return REDIRECT_ACC + account.getId();
                }
            } else {
                model.addAttribute(REPORT, "Message sending error!");
            }
        }
        return MSG_ACC;
    }

    protected String deleteMessage(Account account, Account targetAccount, Integer id, Model model,
                                   RedirectAttributes redirectAttrs) {
        boolean messageDeleted = accountService.deleteMessage(id);
        if (messageDeleted) {
            MessageType type = account.getId() == targetAccount.getId() ? MessageType.POST : MessageType.PERSONAL;
            if (type == MessageType.PERSONAL) {
                redirectAttrs.addAttribute("trgId", targetAccount.getId());
                return REDIRECT_MSG;
            } else {
                return REDIRECT_ACC + account.getId();
            }
        } else {
            model.addAttribute(REPORT, "Message deleting error!");
        }
        return MSG_ACC;
    }

    @GetMapping("/message")
    public String viewMessageSend(@RequestParam("trgId") Integer trgId, HttpSession session, Model model) {
        Account account = (Account) session.getAttribute(ACCOUNT);
        Account targetAccount = accountService.getAccountById(trgId);
        model.addAttribute(ACCOUNT, account);
        model.addAttribute(TRG_ACCOUNT, targetAccount);
        return "message";
    }

}
