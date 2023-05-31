package com.getjavajob.training.maksyutovs.socialnetwork.web.servlets;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.service.AccountService;
import com.getjavajob.training.maksyutovs.socialnetwork.service.GroupService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet
@MultipartConfig(fileSizeThreshold = 65535,     // 65 kb
        maxFileSize = 65535,                    // 65 kb
        maxRequestSize = 1024 * 1024)           // 1 MB
public class UploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UploadServlet.class.getName());
    private static final String ACCOUNT = "account";
    private static final String GROUP = "group";
    private static final String UPLOAD_DIR = "upload";
    private static final String CHANGE_DEL = "change_delete";
    private static final int MAX_FILE_SIZE = 65535;
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
        String path = req.getParameter("path");
        if (path == null) {
            setImage(req, resp);
        } else {
            String command = req.getParameter("submit");
            String id = req.getParameter("id");
            req.setAttribute(CHANGE_DEL, command);
            req.setAttribute("path", path);
            req.setAttribute("id", id);
            if (!StringUtils.isEmpty(command) && command.equals("Delete")) {
                doPost(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/upload.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        String command = req.getParameter(CHANGE_DEL) == null ? (String) req.getAttribute(CHANGE_DEL) :
                req.getParameter(CHANGE_DEL);
        if (StringUtils.isEmpty(command)) {
            req.getParts(); // throws IllegalStateException in case of file size exceed
            doGet(req, resp);
        } else {
            uploadImage(req, resp, command);
        }
    }

    private void setImage(HttpServletRequest req, HttpServletResponse resp) {
        try {
            byte[] image = new byte[0];
            String command = req.getParameter("command");
            String id = req.getParameter("id");
            if (id != null) {
                if (command.equals(ACCOUNT)) {
                    Account account = accountService.getAccountById(Integer.parseInt(id));
                    image = account.getImage();
                    if (image == null) {
                        image = getDefaultImage(account.getGender().toString()).readAllBytes();
                    }
                } else if (command.equals(GROUP)) {
                    Group group = groupService.getGroupById(Integer.parseInt(id));
                    image = group.getImage();
                    if (image == null) {
                        image = getDefaultImage("G").readAllBytes();
                    }
                }
            }
            resp.setContentType("image/jpeg");
            resp.setContentLength(image.length);
            resp.getOutputStream().write(image);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private InputStream getDefaultImage(String sign) {
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
        return getClass().getClassLoader().getResourceAsStream(pathImage);
    }

    private void uploadImage(HttpServletRequest req, HttpServletResponse resp, String command) {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute(ACCOUNT);
        String path = req.getParameter("path");
        try {
            InputStream fileContent = null;
            if (command.equals("Change")) {
                // gets absolute path of the web application
                String applicationPath = req.getServletContext().getRealPath("");
                // constructs path of the directory to save uploaded file
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                if (!StringUtils.isEmpty(path)) {
                    if (path.equals(ACCOUNT)) {
                        uploadFilePath += File.separator + ACCOUNT;
                    } else if (path.equals(GROUP)) {
                        uploadFilePath += File.separator + GROUP;
                    }
                }

                // creates the save directory if it does not exist
                File fileSaveDir = new File(uploadFilePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                }

                String fileName = null;
                //Get all the parts from request and write it to the file on server
                for (Part part : req.getParts()) {
                    String fn = part.getSubmittedFileName();
                    if (!StringUtils.isEmpty(fn)) {
                        if (part.getSize() > MAX_FILE_SIZE) {
                            throw new IOException("Max file size = 65535 kb exceeded");
                        }
                        fileName = Paths.get(fn).getFileName().toString();
                        part.write(uploadFilePath + File.separator + fileName);
                        fileContent = part.getInputStream();
                        break;
                    }
                }
                req.setAttribute("message", "File '" + fileName + "' uploaded successfully!");

            } else if (command.equals("Delete")) {
                if (path.equals(ACCOUNT)) {
                    fileContent = getDefaultImage(account.getGender().toString());
                } else if (path.equals(GROUP)) {
                    fileContent = getDefaultImage("G");
                }
            }

            if (path.equals(ACCOUNT)) {
                Optional<Account> dbAccount = accountService.editAccount(account, "image", fileContent);
                session.setAttribute(ACCOUNT, dbAccount.orElseThrow());
                resp.sendRedirect(req.getContextPath() + "/account?id=" + account.getId());
            } else if (path.equals(GROUP)) {
                String id = req.getParameter("id");
                if (id != null) {
                    Group group = groupService.getGroupById(Integer.parseInt(req.getParameter("id")));
                    groupService.editGroup(group, "image", fileContent);
                }
                resp.sendRedirect(req.getContextPath() + "/group?id=" + id);
            }
            if (fileContent != null) {
                fileContent.close();
            }

        } catch (ServletException | IOException | NumberFormatException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

}
