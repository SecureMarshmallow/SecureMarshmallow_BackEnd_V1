package com.junha.securemarshmallow.controller;

import com.junha.securemarshmallow.ServerMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public ResponseEntity<?> root() {
        return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("status", "runserver"));
    }
}
