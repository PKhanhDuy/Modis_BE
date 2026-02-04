package com.example.modis.admin.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.example.modis.admin.dto.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.modis.auth.dto.LoginRequest;
import com.example.modis.auth.dto.LoginResponse;
import com.example.modis.security.jwt.JwtTokenProvider;
import com.example.modis.user.enumm.Role;
import com.example.modis.user.enumm.Status;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class ManagementAccountService {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Cloudinary cloudinary;
    private final JwtTokenProvider jwtTokenProvider;
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
        if (user.getRole() != role) {
            user.setRole(role);
            return userRepository.save(user);
        }
        return user;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        if (user.getRole() == Role.USER) {
            throw new RuntimeException("Tài khoản không có quyền truy cập!");
        }
        if (user.getIsActive() == Status.INACTIVE) {
            throw new RuntimeException("Tài khoản đã bị khóa!");
        }
        String token = jwtTokenProvider.getSecretToken(user.getId());

        System.out.println(token);
        System.out.println("Đã đăng nhập thành công");
        return new LoginResponse(token, user.getId(), user.getUsername());
    }

    public User updateUser(String id, UserRequestDTO dto, MultipartFile file) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + id));
        user.setFullname(dto.getFullname());
        user.setSdt(dto.getSdt());
        user.setMail(dto.getMail());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive());
        if (file != null && !file.isEmpty()) {
            try {
                Map options = ObjectUtils.asMap(
                        "folder", "Modis",
                        "resource_type", "auto"
                );

                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                String secureUrl = (String) uploadResult.get("secure_url");
                if (secureUrl != null) {
                    user.setAvatarUrl(secureUrl);
                } else {
                    log.error("Cloudinary trả về secure_url null");
                }
            } catch (IOException e) {
                log.error("Lỗi IO khi upload ảnh lên Cloudinary: {}", e.getMessage());
                throw new RuntimeException("Không thể xử lý file ảnh");
            }
        }
        return userRepository.save(user);
    }
}
