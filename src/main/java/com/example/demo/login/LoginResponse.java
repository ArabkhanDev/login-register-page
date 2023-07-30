package com.example.demo.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    String message;
    Boolean status;

    public LoginResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }
}
