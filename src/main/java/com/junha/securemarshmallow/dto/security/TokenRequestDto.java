package com.junha.securemarshmallow.dto.security;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TokenRequestDto {

    @NotBlank(message = "UserId is null.")
    @Size(max=50, message = "UserId's max length is 255.")
    private String id;
    @NotBlank(message = "Password is null.")
    @Size(max=255, message = "Password's max length is 255.")
    private String password;
    @Size(max=400, message = "RefreshToken's max length is 400.")
    private String refreshToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

//Complete