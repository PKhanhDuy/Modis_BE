package com.example.modis.auth.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Optional;

import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import com.example.modis.auth.dto.LoginRequest;
import com.example.modis.auth.dto.TokenResponse;
import com.example.modis.security.jwt.JwtTokenProvider; 
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Tìm user theo username
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                // 2. Tạo JWT Token nếu mật khẩu đúng
                String token = jwtTokenProvider.getSecretToken(user.getId());

                // 3. Trả về Token dưới dạng JSON DTO
                return ResponseEntity.ok(new TokenResponse(token));
            }
        }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");    }
}
