package com.example.modis.auth.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import com.example.modis.user.service.UserService;
import com.example.modis.auth.dto.LoginRequest;
import com.example.modis.auth.dto.LoginResponse;
import com.example.modis.auth.dto.SignUpRequest;
import com.example.modis.auth.dto.SignupResponse;
import com.example.modis.security.jwt.JwtTokenProvider;

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
        // System.out.println("Username : " + loginRequest.getUsername());
        // System.out.println("Password : " + loginRequest.getPassword());
        try {
            // 1. Tìm user theo username
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getPassword().equals(loginRequest.getPassword())) {
                    // 2. Tạo JWT Token nếu mật khẩu đúng
                    String token = jwtTokenProvider.getSecretToken(user.getId());

                    // 3. Trả về Token dưới dạng JSON DTO
                    System.out.println("Da dang nhap thanh cong");
                    return ResponseEntity.ok(new LoginResponse(token, user.getId().toHexString(), user.getUsername()));
                }
                Map<String, String> error = new HashMap<>();
                error.put("message", "Account not exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        } catch (RuntimeException e) {
            // Trả về một Map hoặc Object để phía React Native nhận được JSON { "message": "..." }
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signupRequest) {
        System.out.println(signupRequest.toString());
        try {

            User user = userService.registerNewUser(signupRequest);
            String token = jwtTokenProvider.getSecretToken(user.getId());
            System.out.println("Da dang ki thanh cong");
            return ResponseEntity.ok(new SignupResponse(token, user.getId().toHexString(), user.getUsername()));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
