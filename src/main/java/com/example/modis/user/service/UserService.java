package com.example.modis.user.service;

import com.example.modis.auth.dto.SignUpRequest;
import com.example.modis.user.enumm.Role;
import com.example.modis.user.enumm.Status;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public User insert(User user) {
        log.info("Inserting user: {}", user.getUsername());
        return userRepository.insert(user);
    }

    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }

    public User registerNewUser(SignUpRequest signUpRequest){
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(signUpRequest.getPassword());
        user.setFullname(signUpRequest.getFullname());
        user.setMail(signUpRequest.getMail());
        user.setSdt(signUpRequest.getSdt());

        user.setRole(Role.USER); 
        user.setIsActive(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
