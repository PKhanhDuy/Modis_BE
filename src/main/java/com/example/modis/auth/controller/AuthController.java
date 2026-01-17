package com.example.modis.auth.controller;

import com.example.modis.auth.dto.LoginRequest;
import com.example.modis.auth.dto.LoginResponse;
import com.example.modis.auth.dto.SignUpRequest;
import com.example.modis.auth.dto.SignupResponse;
import com.example.modis.security.jwt.JwtTokenProvider;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import com.example.modis.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signupRequest) {
        System.out.println(signupRequest.toString());
        try{
            User user = userService.registerNewUser(signupRequest);
            String token = jwtTokenProvider.getSecretToken(user.getId());
            System.out.println("Da dang ki thanh cong");
            return ResponseEntity.ok(new SignupResponse(token, user.getId(), user.getUsername()));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage()); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
