package com.mardox.bloghub.controller;

import com.mardox.bloghub.dto.AuthResponseDto;
import com.mardox.bloghub.dto.LoginRequestDto;
import com.mardox.bloghub.dto.RegisterRequestDto;
import com.mardox.bloghub.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto dto){
        return new ResponseEntity<>(service.registerAuthor(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto dto, HttpSession session){
        return ResponseEntity.ok(service.login(dto, session));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session){
        service.logout(session);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser(HttpSession session){
        return ResponseEntity.ok(service.getCurrentUser(session));
    }
}
