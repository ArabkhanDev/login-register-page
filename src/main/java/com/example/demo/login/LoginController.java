package com.example.demo.login;

import com.example.demo.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class LoginController {

    private final AppUserService appUserService;

    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginDTO loginDTO) {
        return appUserService.loginUser(loginDTO);
    }

}
