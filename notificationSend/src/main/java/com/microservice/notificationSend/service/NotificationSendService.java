package com.microservice.notificationSend.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.microservice.notificationSend.service.NotificationMailService;
import com.microservice.notificationSend.service.NotificationPushService;




/** * NotificationSendService is responsible for sending notifications to users based on their preferred channels.
 * It supports multiple notification channels such as EMAIL and PUSH notifications.
 * 
 * The sendNotification method takes a NotificationRequest object, retrieves the user's preferred notification channels,
 * and sends the notification through each channel. It returns a list of results from each notification attempt.
 */

@Service
public class NotificationSendService 
{
	@Autowired
	private NotificationMailService mailService;
	
	@Autowired
	private NotificationPushService pushService;
	
	
	
	/** * Sends notifications to users based on their preferred channels.
	 * 
	 * @param The NotificationRequest object containing user information and notification details.
	 * @return A list of lists containing results from each notification attempt.
	 */
	
	public List<List<String>> sendNotification(NotificationRequest request)
	{
		// Extract preferred notification channels from the request
		List<String> channels = request.getUserInfo().getPreferences().getNotificationType();
		List<List<String>> res = new ArrayList<>();
		
		
		// Iterate through each preferred channel and send the notification
		for(String channel : channels)
		{
			if(channel.equals("EMAIL"))
			{
				// Generate the HTML content for the email
				String HTMLString = request.getHTMLString();				
				res.add(mailService.sendMail(request, HTMLString));
			}
			
			else if(channel.equals("PUSH"))
			{
				// Send push notification
				res.add(pushService.sendPush(request));
			}
		}
		
		return res;
	}
}








