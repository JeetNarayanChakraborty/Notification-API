package com.microservice.notificationContentBuilding.Service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.dto.input_dto.UserInputDTO.NotificationRequest;


/** * Service class for building email content using Thymeleaf templates.
 * * This service takes a NotificationRequest and a URL to generate
 * * a personalized HTML email content.
 */


@Service
public class MailContentBuildingService 
{
	private final TemplateEngine templateEngine;
	
	public MailContentBuildingService(TemplateEngine templateEngine)
	{
		this.templateEngine = templateEngine;
	}
	
	
	
	/** Builds the HTML content for an email notification.
	 * 
	 * @param request The NotificationRequest containing user and message details.
	 * @param URL The URL to be included in the email (e.g., for a button link).
	 * @return A String containing the generated HTML content.
	 */
	
	public String buildHTML(NotificationRequest request, String URL)
	{
		// Prepare the Thymeleaf context with variables
		Context context = new Context();
		String username = request.getUserInfo().getUsername();
		String message = "Hi " + username + ", " + request.getNotificationBody().getMesssage();
		String clientURL = URL;
		
		// Set variables for the template
		context.setVariable("body", message);
		context.setVariable("buttonUrl", clientURL);
		
		// Process the template with the context to generate HTML
		String HTMLString = templateEngine.process("notification_email_template", context);
		
		return HTMLString;
	}
}







