package com.microservice.userDetails.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dto.input_dto.UserInputDTO.NotificationRequest;



/**
 * Feign client interface for communicating with the Notification Content Building microservice.
 * This interface defines the REST endpoint for building notification content.
 * 
 * Features:
 * - Declarative REST client using Feign
 * - Method for forwarding the notification requests after all input validations
 */




@FeignClient(name = "notificationContentBuilding")
public interface NotificationContentBuildingServiceClient 
{
	@PostMapping("/api/build/buildNotificationContent")
	public ResponseEntity<?> buildNotificationContent(@RequestBody NotificationRequest request);
}
