package com.junha.securemarshmallow.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "email", unique = true)
})
@Entity
public class UserAccount {
    @Id
    @Column(length = 50)
    private String userId;

    @Setter @Column(nullable = false,length = 255) private String userPassword;

    @Email
    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter @Column(length = 400) private String refreshToken;


    protected UserAccount() {}

    private UserAccount(String user_id, String userPassword, String email, String nickname) {
        this.userId = user_id;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
    }

    public void setRefreshToken(String refresh_token) {
        this.refreshToken = refresh_token;
    }

    public static UserAccount of(String userId, String userPassword, String email, String nickname) {
        return new UserAccount(userId, userPassword, email, nickname);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return userId != null && userId.equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }


}
//Complete