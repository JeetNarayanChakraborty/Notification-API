package com.microservice.notificationContentBuilding.Service;

import org.springframework.stereotype.Service;

import com.dto.input_dto.UserInputDTO.NotificationBody;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;


/** * Service class for building and personalizing push notification content.
 * This class provides methods to customize the notification header and message
 * based on user information.
 */

@Service
public class PushContentBuildingService 
{
	/**
	 * Personalizes the content of a push notification request by incorporating
	 * the user's name into the notification header and message.
	 * 
	 * @param request The original notification request containing user info and body.
	 * @return The modified notification request with personalized content.
	 */
	
	public NotificationRequest personalizeContent(NotificationRequest request)
	{
		// Extract user information and notification body
		UserInfo userInfo = request.getUserInfo();
		NotificationBody notificationBody = request.getNotificationBody();
		
		// Personalize the header and message
		String username = userInfo.getUsername();
		String newHeader = username + ", " + notificationBody.getHeader();
		String newBody = "Hi " + username + ", " + notificationBody.getMesssage();
		
		// Update the notification body with personalized content
		notificationBody.setHeader(newHeader);
		notificationBody.setMesssage(newBody);
		
		// Set the updated notification body back to the request
		request.setNotificationBody(notificationBody);
		
		return request;		
	}
}
