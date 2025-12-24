package com.example.modis.user.service;

import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
