package com.example.modis.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.auth.dto.LoginRequest;
import com.example.modis.auth.dto.LoginResponse;
import com.example.modis.auth.dto.SignUpRequest;
import com.example.modis.security.jwt.JwtTokenProvider;
import com.example.modis.user.dto.UserResponse;
import com.example.modis.user.enumm.Role;
import com.example.modis.user.enumm.Status;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Cloudinary cloudinary;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> searchUsers(String keyword, String currentUserId) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCase(keyword, keyword)
                .stream()
                .filter(u -> !u.getId().toString().equals(currentUserId))
                .map(u -> {
                    UserResponse dto = new UserResponse();
                    dto.setId(u.getId().toString());
                    dto.setUsername(u.getUsername());
                    dto.setFullname(u.getFullname());
                    dto.setMail(u.getMail());
                    dto.setSdt(u.getSdt());
                    dto.setAvatarUrl(u.getAvatarUrl());
                    return dto;
                })
                .toList();
    }

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

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        if (user.getIsActive() == Status.INACTIVE) {
            throw new RuntimeException("Tài khoản đã bị khóa!");
        }
        String token = jwtTokenProvider.getSecretToken(user.getId());
        return new LoginResponse(token, user.getId(), user.getUsername());
    }


    public User registerNewUser(SignUpRequest signUpRequest){
        if ( checkUsernameExits(signUpRequest.getUsername())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setFullname(signUpRequest.getFullname());
        user.setMail(signUpRequest.getMail());
        user.setSdt(signUpRequest.getSdt());

        user.setRole(Role.USER); 
        user.setIsActive(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("Tai khoan user trước khi" + user.toString());
        return userRepository.save(user);
    }
    
    public UserResponse getUser(String id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Chuyển đổi từ Entity sang DTO
        UserResponse userDTO = new UserResponse();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullname(user.getFullname());
        userDTO.setSdt(user.getSdt());
        userDTO.setMail(user.getMail());
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

    public User updateMail(String userId, String mail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (mail == null || mail.trim().isEmpty()) {
            throw new RuntimeException("SDT không hợp lệ");
        }
        
        if (checkPhoneExits(mail)){
            throw new RuntimeException("SDT đã được sử dụng");
        }

        user.setMail(mail.trim());
        return userRepository.save(user);
    }

    public String updateAvatar(String id, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File ảnh rỗng hoặc không tồn tại");
            }
            
            Map options = ObjectUtils.asMap("folder", "Modis");

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new RuntimeException("Upload lên Cloudinary thất bại");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            user.setAvatarUrl(secureUrl);
            userRepository.save(user);

            return secureUrl;
        } catch (IOException e) {
            log.error("Lỗi upload ảnh: {}", e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi upload ảnh");
        }
    }

    public String changePassword(String userId, String oldPass, String newPass) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }
        if (passwordEncoder.matches(newPass, user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được giống mật khẩu cũ!");
        }
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
        log.info("Người dùng {} đã đổi mật khẩu thành công", user.getUsername());
        return userId;
    }
}
