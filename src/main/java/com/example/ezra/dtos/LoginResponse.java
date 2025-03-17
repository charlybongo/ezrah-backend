package com.example.ezra.dtos;

import com.example.ezra.models.authModel.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}
