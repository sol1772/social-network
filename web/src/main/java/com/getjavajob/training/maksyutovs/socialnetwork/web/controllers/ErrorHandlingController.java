package com.getjavajob.training.maksyutovs.socialnetwork.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorHandlingController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest req, Model model) {
        String exceptionMessage = "";
        String statusCode = "";
        Integer status = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            exceptionMessage = HttpStatus.valueOf(status).getReasonPhrase();
            statusCode = status.toString();
            if (status == HttpStatus.NOT_FOUND.value()) {
                exceptionMessage = "Page not found";
            } else if (status == HttpStatus.FORBIDDEN.value()) {
                exceptionMessage = "Sorry, you do not have permission to perform this action";
            }
        }
        model.addAttribute("exceptionMessage", exceptionMessage);
        model.addAttribute("statusCode", statusCode);
        return "error";
    }

    @GetMapping("/error/{id}")
    public String handleErrorId(@PathVariable String id, Model model) {
        String exceptionMessage = "";
        String statusCode = "";
        if (!StringUtils.isEmpty(id)) {
            statusCode = id;
            switch (id) {
                case "401":
                    exceptionMessage = "Bad Request";
                    break;
                case "403":
                    exceptionMessage = "Sorry, you do not have permission to perform this action";
                    break;
                case "404":
                    exceptionMessage = "Page not found";
                    break;
                default:
                    exceptionMessage = "Error";
                    break;
            }
        }
        model.addAttribute("exceptionMessage", exceptionMessage);
        model.addAttribute("statusCode", statusCode);
        return "error";
    }

}
