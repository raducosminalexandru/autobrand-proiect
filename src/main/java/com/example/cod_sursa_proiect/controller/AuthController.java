package com.example.cod_sursa_proiect.controller;

import com.example.cod_sursa_proiect.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserAccountService accountService;

    public AuthController(UserAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Parolele introduse nu coincid!");
            return "register";
        }

        boolean success = accountService.registerUser(username, password);
        if (!success) {
            model.addAttribute("error", "Acest username este deja utilizat!");
            return "register";
        }

        return "redirect:/login?registered";
    }
}