package com.example.modis.user.repository;

import com.example.modis.user.enumm.Role;
import com.example.modis.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    Optional<User> findBySdt(String sdt);

    List<User> findAll();

    List<User> findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCase(String username, String fullname);

    long countByRole(Role role);

    long countByRoleAndCreatedAtBetween(
            Role role,
            LocalDateTime start,
            LocalDateTime end
    );
}
