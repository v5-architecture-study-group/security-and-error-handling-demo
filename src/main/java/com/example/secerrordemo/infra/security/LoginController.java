package com.example.secerrordemo.infra.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
class LoginController {

    public static final String LOGIN_FORM_URL = "/login.html";
    public static final String LOGOUT_SUCCESS_URL = "/login.html?logout";
    public static final String LOGIN_PROCESSING_URL = "/login";

    @RequestMapping(LOGIN_FORM_URL)
    public String loginForm(Model model, WebRequest request) {
        model.addAttribute("loginError", request.getParameterMap().containsKey("error"));
        model.addAttribute("loggedOut", request.getParameterMap().containsKey("logout"));
        return "login";
    }
}
