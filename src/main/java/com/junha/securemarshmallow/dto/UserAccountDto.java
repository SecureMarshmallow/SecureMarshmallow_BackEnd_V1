package com.junha.securemarshmallow.dto;

import com.junha.securemarshmallow.domain.UserAccount;

import javax.persistence.Id;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

public record UserAccountDto(
        @Id
        String user_id,
        String userPassword,
        @Email
        String email,
        String nickname
) {
    public static UserAccountDto of(String user_id, String userPassword, String email, String nickname) {
        return new UserAccountDto(user_id, userPassword, email, nickname);
    }

    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                user_id,
                userPassword,
                email,
                nickname
        );
    }

}