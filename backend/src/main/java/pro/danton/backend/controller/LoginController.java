package pro.danton.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.danton.backend.dto.LoginDTO;
import pro.danton.backend.service.LoginService;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService service;

    @PostMapping
    public LoginDTO login(@RequestHeader(LoginService.X_KONNEQT_TOKEN_HEADER) String email)
            throws IOException, InterruptedException {
        return service.login(email);
    }
}