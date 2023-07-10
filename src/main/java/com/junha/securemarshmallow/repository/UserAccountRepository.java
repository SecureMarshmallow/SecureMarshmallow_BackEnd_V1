package com.junha.securemarshmallow.repository;

import com.junha.securemarshmallow.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    boolean existsByEmail(String email);
    @Query("SELECT ua FROM UserAccount ua WHERE ua.refreshToken = :token")
    UserAccount findByRefreshToken(@Param("token") String refreshToken);

    @Modifying
    @Query("UPDATE UserAccount ua SET ua.refreshToken = NULL")
    void deleteAllRefreshTokens();
}
