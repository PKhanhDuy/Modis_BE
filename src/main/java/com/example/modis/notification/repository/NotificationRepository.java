package com.example.modis.notification.repository;

import com.example.modis.chat.websocket.test.MongoCheck;
import com.example.modis.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
}
