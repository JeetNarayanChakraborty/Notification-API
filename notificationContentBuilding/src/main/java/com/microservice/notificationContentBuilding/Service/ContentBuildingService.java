package com.microservice.notificationContentBuilding.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.microservice.notificationContentBuilding.FeignClient.NotificationSendServiceClient;


/** * Service class for building notification content and forwarding it to the sending service.
 * * This class utilizes MailContentBuildingService for email content creation and
 * PushContentBuildingService for push notification content personalization.
 */


@Service
public class ContentBuildingService 
{
	
	private final MailContentBuildingService mailContentBuildingService;
	private final PushContentBuildingService pushContentBuildingService;
	private final NotificationSendServiceClient notificationSendServiceClient;
	
    
    public ContentBuildingService(MailContentBuildingService mailContentBuildingService,
    		                      PushContentBuildingService pushContentBuildingService,
    		                      NotificationSendServiceClient notificationSendServiceClient) 
    {
        this.mailContentBuildingService = mailContentBuildingService;
        this.pushContentBuildingService = pushContentBuildingService;
        this.notificationSendServiceClient = notificationSendServiceClient;
    }
    
    

    
    /** Method to build notification content based on user preferences and forward it to the sending service.
     * 
	 * @param request The notification request containing user info and notification body.
	 * @param URL The URL to be included in the notification (e.g., for email buttons).
	 * @return Response from the notification sending service.
	 */
	
	public String buildContent(NotificationRequest request, String URL)
	{
		NotificationRequest req = request;
		List<Object> res = new ArrayList<>();
		
		// Determine the type of notification to be sent (EMAIL, PUSH, or both)
		List<String> type = request.getUserInfo().getPreferences().getNotificationType();
		
		
		String forMail = null;
		
		
		// Build content based on the notification type
		try
		{
			for(String t: type)
			{
				if(t.equals("EMAIL"))
				{
					// Build HTML content for email using Thymeleaf template
					forMail = mailContentBuildingService.buildHTML(request, URL);
				}
				
				else if(t.equals("PUSH"))
				{
					// Personalize content for push notification
					req = pushContentBuildingService.personalizeContent(request);
				}
			}
			
			if(forMail != null)
			{
				// Set the generated HTML content in the request object
				req.setHTMLString(forMail);
			}
		}
		
		catch(Exception e)
		{
			return "Error in building content: " + e.getMessage();
		}
		
		
		// Forward the request to the Notification Sending microservice
		return notificationSendServiceClient.sendNotificationContent(req);
	}
}











