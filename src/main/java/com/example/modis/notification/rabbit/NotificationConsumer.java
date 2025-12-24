package com.example.modis.notification.rabbit;

import com.example.modis.notification.dto.NotificationDTO;
import com.example.modis.notification.mapper.Mapper;
import com.example.modis.notification.model.Notification;
import com.example.modis.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final Mapper mapper;
    private final NotificationService service;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveMessage(NotificationDTO dto) {
        log.info("Received Message from Notification Queue");
        Notification notification = mapper.toEntity(dto);
        service.save(notification);

        try {
            Thread.sleep(2000);
            log.info("Processed Notification successfully to targetId: {}", dto.getTargetId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
