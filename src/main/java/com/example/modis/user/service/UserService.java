package com.example.modis.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.auth.dto.SignUpRequest;
import com.example.modis.user.dto.UserResponse;
import com.example.modis.user.enumm.Role;
import com.example.modis.user.enumm.Status;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    public User insert(User user) {
        log.info("Inserting user: {}", user.getUsername());
        return userRepository.insert(user);
    }

    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }

    public User getUserById(String id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với id: " + id));
    }

    public UserResponse getUser(String id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

    // Chuyển đổi từ Entity sang DTO
    UserResponse userDTO = new UserResponse();
    userDTO.setId(user.getId());
    userDTO.setUsername(user.getUsername());
    userDTO.setSdt(user.getSdt());
    userDTO.setAvatarUrl(user.getAvatarUrl());
    return userDTO;
    }

    public boolean checkUsernameExits(String username){
        if (userRepository.findByUsername(username).isPresent()) {
            return true;
        }
        return false;
    }
    
    public boolean checkPhoneExits(String phone){
        if (userRepository.findBySdt(phone).isPresent()) {
            return true;
        }
        return false;
    }

    public User registerNewUser(SignUpRequest signUpRequest){
        if ( checkUsernameExits(signUpRequest.getUsername())){
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
    
    public User updateUsername(String userId, String username) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Tên người dùng không hợp lệ");
        }
        
        if ( checkUsernameExits(username)){
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        user.setUsername(username.trim());
        return userRepository.save(user);
    }

    public User updatePhone(String userId, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("SDT không hợp lệ");
        }
        
        if (checkPhoneExits(phone)){
            throw new RuntimeException("SDT đã được sử dụng");
        }

        user.setSdt(phone.trim());
        return userRepository.save(user);
    }

    public User deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setIsActive(Status.INACTIVE);
        return userRepository.save(user);
    } 
    
    public User updateRole(String userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        if ( user.getRole() != role){
            user.setRole(role);
            return userRepository.save(user);
        }
        return user;
    }

    public String updateAvatar(String id, MultipartFile avatar) {
    // 1. Kiểm tra đầu vào - Ném Exception nếu lỗi
    if (avatar == null || avatar.isEmpty()) {
        throw new IllegalArgumentException("File ảnh rỗng hoặc không tồn tại");
    }
    // 2. Cấu hình Cloudinary
    Map<?, ?> options = ObjectUtils.asMap(
        "folder", "Modis/avatars",
        "public_id", id + "_avatar",
        "overwrite", true
    );
    try {
        // 3. Upload và lấy URL
        Map<?, ?> uploadResult = cloudinary.uploader().upload(avatar.getBytes(), options);
        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl == null) {
            throw new RuntimeException("Cloudinary không trả về URL");
        }
        // 4. Cập nhật Database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setAvatarUrl(secureUrl.toString());
        userRepository.save(user);
        return secureUrl.toString();
    } catch (IOException e) {
        throw new RuntimeException("Lỗi khi đọc file ảnh", e);
    }
    }
}
