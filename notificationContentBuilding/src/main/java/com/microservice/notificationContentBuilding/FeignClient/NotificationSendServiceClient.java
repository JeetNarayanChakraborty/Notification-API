package com.microservice.notificationContentBuilding.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dto.input_dto.UserInputDTO.NotificationRequest;


/** 
 * Feign client interface for communicating with the notification sending service.
 * Provides a method to send notification content to the sending service.
 */

@FeignClient(name = "notificationSend")
public interface NotificationSendServiceClient 
{
	@PostMapping("/api/send/sendNotification")
	public String sendNotificationContent(@RequestBody NotificationRequest request);
}
