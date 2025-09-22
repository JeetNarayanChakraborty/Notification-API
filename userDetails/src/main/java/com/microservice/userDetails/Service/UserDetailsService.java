package com.microservice.userDetails.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.microservice.userDetails.FeignClient.NotificationContentBuildingServiceClient;


/**
 * Service class for processing user details.
 * This class contains the business logic for handling user information and preferences.
 * It validates the input data and forwards it to the Notification Content Building microservice.
 * 
 * Features:
 * - Input validation
 * - Forwarding data to another microservice
 */


@Service
public class UserDetailsService 
{
	@Autowired
	private NotificationContentBuildingServiceClient notificationClient;
	
	
	/**
	 * Processes the user details submission.
	 * Validates the input data and forwards it to the Notification Content Building microservice.
	 * 
	 * @param request The notification request payload containing notification data, user details and preferences.
	 * @return A ResponseEntity containing the result of the processing.
	 */
	
	
	public ResponseEntity<Map<String, Object>> takeUserDetails(NotificationRequest request)
	{		
		   // Input validation: Check if subject is provided when notification type is EMAIL
		if((request.getUserInfo().getPreferences().getNotificationType().contains("EMAIL")
	       && request.getNotificationBody().getSubject() == null)
		
				// Input validation: Check if deviceId is provided when notification type is PUSH
	       || (request.getUserInfo().getPreferences().getNotificationType().contains("PUSH")
	       && request.getUserInfo().getDeviceId() == null))
			
		{
			Map<String, Object> response = new HashMap<>();
		    response.put("status", "error");
		    response.put("message", "Subject is required for EMAIL notification type");
		    response.put("timestamp", Instant.now().toString());
		   
		    return ResponseEntity.badRequest().body(response);
		}
		
		// Prepare a hashmap for a JSON success response
		Map<String, Object> response = new HashMap<>();
	    response.put("status", "success");
	    response.put("message", "User info received successfully");
	    response.put("timestamp", Instant.now().toString());
	    
	    
	
		// Forward the request to the Notification Content Building microservice
		notificationClient.buildNotificationContent(request);
	   
	    return ResponseEntity.ok(response);
	}
}






