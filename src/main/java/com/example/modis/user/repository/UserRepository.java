package com.example.modis.user.repository;

import com.example.modis.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.modis.user.enumm.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //tim use theo ten
    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    Optional<User> findBySdt(String sdt);

    List<User> findAll();


    long countByIsActive(Status status);

    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
