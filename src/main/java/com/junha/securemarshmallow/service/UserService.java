package com.junha.securemarshmallow.service;
import com.junha.securemarshmallow.ServerMessage;
import com.junha.securemarshmallow.domain.UserAccount;
import com.junha.securemarshmallow.dto.UserAccountDto;
import com.junha.securemarshmallow.dto.security.TokenResponseDto;
import com.junha.securemarshmallow.repository.UserAccountRepository;
import com.junha.securemarshmallow.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager,
                       TokenUtils tokenUtils, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.passwordEncoder = passwordEncoder;
    }
    public TokenResponseDto login(String userId, String password) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String encodedPassword = userAccount.getUserPassword();
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new BadCredentialsException("Invalid userId or password");
        }

        UserDetails userDetails = new User(userId, encodedPassword, Collections.emptyList());
        String accessToken = tokenUtils.generateAccessToken(userDetails);
        String refreshToken = generateAndSaveRefreshToken(userDetails, userAccount);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ServerMessage> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ServerMessage serverMessage = new ServerMessage("error","User Not Found");
        return new ResponseEntity<>(serverMessage, HttpStatus.NOT_FOUND);
    }

    private String generateAndSaveRefreshToken(UserDetails userDetails, UserAccount userAccount) {
        try {
            String refreshToken = tokenUtils.generateRefreshToken(userDetails);
            userAccount.setRefreshToken(refreshToken);
            userAccountRepository.save(userAccount);
            return refreshToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void signup(UserAccountDto userAccountDto) {

        String user_id = userAccountDto.user_id();
        if (userAccountRepository.existsById(user_id)) {
            throw new RuntimeException("User with userId " + user_id + " already exists");
        }
        String password = userAccountDto.userPassword();
        try {
            if (!password.matches(".*\\d.*")) {
                throw new Exception("Password must contain a number.");
            }
            if (!password.matches(".*[a-zA-Z].*")) {
                throw new Exception("Password must contain an alphabet.");
            }
            if (!password.matches(".*[!@#$%^&*()\\-=_+\\[\\]{};':\"|,.<>/?]+.*")) {
                throw new Exception("Password must contain a special symbol.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        UserAccount userAccount = UserAccount.of(
                user_id,
                password,
                userAccountDto.email(),
                userAccountDto.nickname()
        );
        String encodedPassword = passwordEncoder.encode(userAccount.getUserPassword());
        userAccount.setUserPassword(encodedPassword);
        userAccountRepository.save(userAccount);
    }

    public String logout(String refreshTokenJson) {
        try {
            JSONObject jsonObject = new JSONObject(refreshTokenJson);
            String refreshToken = jsonObject.getString("refreshToken");

            UserAccount userAccount = userAccountRepository.findByRefreshToken(refreshToken);
            userAccount.setRefreshToken(null);
            userAccountRepository.save(userAccount);
        } catch (Exception e) {
            System.out.println(e);
            return "Logout Failed.";
        }

        return "Logout Successfully.";
    }



    public String refreshAccessToken(String refreshToken) {
        if (!tokenUtils.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        UserAccount userAccount = userAccountRepository.findByRefreshToken(refreshToken);

        UserDetails userDetails = new User(userAccount.getUserId(), "", Collections.emptyList());

        String accessToken = tokenUtils.generateAccessTokenFromRefreshToken(refreshToken);

        return accessToken;
    }

    public int initialize()
    {
        UserAccount userAccount = (UserAccount) userAccountRepository.findAll();
        userAccount.setRefreshToken(null);
        return 200;
    }

    //Complete

}
