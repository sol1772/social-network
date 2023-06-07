package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet
public class MessagesAccountServlet extends HttpServlet {

    private static final String MESSAGES_URL = "/WEB-INF/jsp/messages_account.jsp";
    private static final String ERROR_URL = "/WEB-INF/jsp/error.jsp";
    private static final String ACCOUNT = "account";
    private static final String TRG_ACCOUNT = "targetAccount";
    private static final String MESSAGES = "messages";
    private static final String REPORT = "report";
    private static final String EXCEPTION = "exceptionMessage";
    private static final String ACCOUNT_NOT_FOUND = "Account not found";
    private static final String TRG_ID = "trgId";
    private AccountService accountService;
    private GroupService groupService;

    @Override
    public void init(ServletConfig config) {
        ServletContext sc = config.getServletContext();
        accountService = (AccountService) sc.getAttribute("AccountService");
        groupService = (GroupService) sc.getAttribute("GroupService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account account = (Account) req.getSession().getAttribute(ACCOUNT);
        Account targetAccount = null;
        try {
            String idString = req.getParameter("id");
            String trgIdString = req.getParameter(TRG_ID);
            if (!StringUtils.isEmpty(idString)) {
                int id = Integer.parseInt(idString);
                if (account == null || account.getId() != id) {
                    account = accountService.getAccountById(id);
                }
            }
            if (!StringUtils.isEmpty(trgIdString)) {
                int id = Integer.parseInt(trgIdString);
                targetAccount = accountService.getAccountById(id);
            }
            if (account == null || targetAccount == null) {
                req.setAttribute(EXCEPTION, ACCOUNT_NOT_FOUND);
                req.getRequestDispatcher(ERROR_URL).forward(req, resp);
            } else {
                req.setAttribute(ACCOUNT, account);
                req.setAttribute(TRG_ACCOUNT, targetAccount);
                req.setAttribute(MESSAGES, accountService.getMessages(account, targetAccount, MessageType.PERSONAL));
                req.getRequestDispatcher(MESSAGES_URL).forward(req, resp);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=utf-8");
        try {
            String submit = req.getParameter("submit");
            if (!StringUtils.isEmpty(submit)) {
                if (submit.equals("Send")) {
                    sendMessage(req, resp);
                } else if (submit.equals("del_msg")) {
                    deleteMessage(req, resp);
                }
            } else {
                req.setAttribute(EXCEPTION, "Submit parameter not defined");
                req.getRequestDispatcher(ERROR_URL).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);
        Account targetAccount;
        boolean messageSent = false;
        try {
            targetAccount = validateAccountById(req.getParameter(TRG_ID));
            if (account == null || targetAccount == null) {
                req.setAttribute(EXCEPTION, ACCOUNT_NOT_FOUND);
                req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                return;
            }
            MessageType type = account.getId() == targetAccount.getId() ? MessageType.POST : MessageType.PERSONAL;
            String messageStr = req.getParameter("message");
            if (StringUtils.isEmpty(messageStr)) {
                req.setAttribute(REPORT, "Empty message");
            } else {
                Message message = new Message(account, targetAccount, type, messageStr);
                messageSent = accountService.sendMessage(message);
                if (!messageSent) {
                    req.setAttribute(REPORT, "Message sending error!");
                }
            }
            if (messageSent) {
                account = validateAccountById(req.getParameter("accId"));
                session.setAttribute(ACCOUNT, account);
                if (type == MessageType.PERSONAL) {
                    resp.sendRedirect(req.getContextPath() + "/messages_account?trgId=" + targetAccount.getId());
                } else {
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + account.getId());
                }
            } else {
                req.setAttribute(ACCOUNT, account);
                req.setAttribute(TRG_ACCOUNT, targetAccount);
                if (type == MessageType.PERSONAL) {
                    req.setAttribute(MESSAGES, accountService.getMessages(account, targetAccount, type));
                    req.getRequestDispatcher(MESSAGES_URL).forward(req, resp);
                } else {
                    req.getRequestDispatcher("/WEB-INF/jsp/message.jsp").forward(req, resp);
                }
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void deleteMessage(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);
        Account targetAccount;
        boolean messageDeleted = false;
        try {
            targetAccount = validateAccountById(req.getParameter(TRG_ID));
            if (account == null || targetAccount == null) {
                req.setAttribute(EXCEPTION, ACCOUNT_NOT_FOUND);
                req.getRequestDispatcher(ERROR_URL).forward(req, resp);
                return;
            }
            String msgId = req.getParameter("msg_id");
            if (StringUtils.isEmpty(msgId)) {
                req.setAttribute(REPORT, "Empty message id");
            } else {
                int id = Integer.parseInt(msgId);
                messageDeleted = accountService.deleteMessage(id);
                if (!messageDeleted) {
                    req.setAttribute(REPORT, "Message deleting error!");
                }
            }
            MessageType type = account.getId() == targetAccount.getId() ? MessageType.POST : MessageType.PERSONAL;
            if (messageDeleted) {
                account = validateAccountById(req.getParameter("accId"));
                session.setAttribute(ACCOUNT, account);
                if (type == MessageType.PERSONAL) {
                    resp.sendRedirect(req.getContextPath() + "/messages_account?trgId=" + targetAccount.getId());
                } else if (type == MessageType.POST) {
                    resp.sendRedirect(req.getContextPath() + "/account?id=" + account.getId());
                }
            } else {
                req.setAttribute(ACCOUNT, account);
                req.setAttribute(TRG_ACCOUNT, targetAccount);
                if (type == MessageType.PERSONAL) {
                    req.setAttribute(MESSAGES, accountService.getMessages(account, targetAccount, type));
                    req.getRequestDispatcher(MESSAGES_URL).forward(req, resp);
                } else if (type == MessageType.POST) {
                    req.setAttribute("groups", groupService.getGroupsByAccount(account));
                    req.setAttribute("posts", accountService.getMessages(account, account, MessageType.POST));
                    req.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(req, resp);
                }
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    protected Account validateAccountById(String id) {
        Account account = null;
        if (!StringUtils.isEmpty(id)) {
            account = accountService.getAccountById(Integer.parseInt(id));
        }
        return account;
    }

}