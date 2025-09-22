package com.microservice.notificationSend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.microservice.notificationSend.service.NotificationSendService;


/** 
 * This class is a REST controller that handles notification sending requests.
 * It exposes an endpoint to send notifications based on user preferences.
 */

@RestController
@RequestMapping("/api/send")
public class NotificationSendController 
{
	@Autowired
	private NotificationSendService notificationSendService;
	
	
	/** 
	 * Endpoint to send notifications.
	 * 
	 * @param The notification request containing notification payload, user details and preferences.
	 * @return A string representation of the response from the notification sending process.
	 */
	
	@PostMapping("/sendNotification")
	public String sendNotification(@RequestBody NotificationRequest request)
	{
		List<List<String>> response;
		
		try
		{
			response = notificationSendService.sendNotification(request);
			
			if(response == null || response.isEmpty()) return "";
			return response.toString();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
		
	}
}







