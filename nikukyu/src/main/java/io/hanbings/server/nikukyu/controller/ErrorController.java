package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.model.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @ResponseBody
    @RequestMapping("/error")
    public Message<?> handleError() {
        return Message.Messages.NOT_FOUND;
    }
}