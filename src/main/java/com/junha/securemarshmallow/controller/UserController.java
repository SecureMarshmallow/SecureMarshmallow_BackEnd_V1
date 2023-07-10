package com.junha.securemarshmallow.controller;
import com.junha.securemarshmallow.ServerMessage;
import com.junha.securemarshmallow.dto.UserAccountDto;
import com.junha.securemarshmallow.dto.security.TokenRequestDto;
import com.junha.securemarshmallow.dto.security.TokenResponseDto;
import com.junha.securemarshmallow.repository.UserAccountRepository;
import com.junha.securemarshmallow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserController(UserService userService, UserAccountRepository userAccountRepository) {
        this.userService = userService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Object> login(@RequestBody TokenRequestDto tokenRequest) {
        try {
            String userId = tokenRequest.getId();
            String password = tokenRequest.getPassword();
            System.out.println(userId);
            System.out.println(password);
            try {
                TokenResponseDto tokenResponse = userService.login(userId, password);
                return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Id or Password is incorrect."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Data missed"));
        }

    }


    @PostMapping(value = "/signup", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody UserAccountDto userAccountDto) {

        try {
            String user_id = userAccountDto.user_id();
            String password = userAccountDto.userPassword();
            if (userAccountRepository.existsById(user_id) || userAccountRepository.existsByEmail(userAccountDto.email())) {
                ServerMessage serverMessage = new ServerMessage("error", "User with userId " + user_id + " or email " + userAccountDto.email() + " already exists");
                return ResponseEntity.badRequest().body(serverMessage);
            }

            if (!password.matches(".*\\d.*")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Password must contains number."));
            }
            if (!password.matches(".*[a-zA-Z].*")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Password must contains characters."));
            }
            if (!password.matches(".*[!@#$%^&*()\\-=_+\\[\\]{};':\"|,.<>/?]+.*")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Password must contains special symbols."));
            }
            if ((password.length()) < 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Password length must longer than 10."));
            }
            userService.signup(userAccountDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ServerMessage("success", "Signup Successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Data missed"));
        }
    }

    @PostMapping(value = "/refresh-token", produces = "application/json")

    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequest) {
        try {
            String refreshToken = tokenRequest.getRefreshToken();
            String newAccessToken = userService.refreshAccessToken(refreshToken);
            return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("success", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "AccessToken generate Failed."));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<?> logout(@RequestBody String RefreshToken) {
        String status = userService.logout(RefreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("status", status));

    }

}
