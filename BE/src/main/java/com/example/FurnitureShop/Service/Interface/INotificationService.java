package com.example.FurnitureShop.Service.Interface;


import com.example.FurnitureShop.DTO.Request.NotificationRequest;
import com.example.FurnitureShop.DTO.Response.NotificationResponse;

import java.util.List;

public interface INotificationService {

    NotificationResponse create(NotificationRequest notificationRequest);

    List<NotificationResponse> getByUserId(Long userId);
}
