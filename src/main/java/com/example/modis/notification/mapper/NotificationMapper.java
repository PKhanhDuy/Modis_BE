package com.example.modis.notification.mapper;

import com.example.modis.notification.dto.NotificationDTO;
import com.example.modis.notification.model.Metadata;
import com.example.modis.notification.model.Notification;
import org.springframework.stereotype.Component;


@Component
public class NotificationMapper {
    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }
        return Notification.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .metadata(new Metadata(dto.getTargetId(), dto.getNotificationType()))
                .build();
    }

    public NotificationDTO toDTO(Notification entity) {
        if (entity == null) {
            return null;
        }
        return NotificationDTO.builder()
                .userId(entity.getUserId())
                .content(entity.getContent())
                .targetId(entity.getMetadata().getTargetId())
                .notificationType(entity.getMetadata().getNotificationType())
                .build();
    }
}
