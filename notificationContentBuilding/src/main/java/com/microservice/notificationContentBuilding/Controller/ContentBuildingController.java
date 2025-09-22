package com.microservice.notificationContentBuilding.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.microservice.notificationContentBuilding.Service.ContentBuildingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;





/** Controller class for handling content building requests.
 *  Provides an endpoint to build notification content based on user preferences.
 *  Utilizes the ContentBuildingService to process the request.
 * 
 *  Features:
 *  - Endpoint: /api/build/buildNotificationContent
 *  - Method: POST
 */


@RestController
@RequestMapping("/api/build")
public class ContentBuildingController 
{
	@Autowired
	private ContentBuildingService contentBuildingService;
	
	
	
	/** Endpoint to build notification content.
	 *  Accepts a NotificationRequest, builds the content, forwards it to the sending service, and returns the response.
	 *  
	 *  @param request The notification request containing notification payload, user info and preferences.
	 *  @param serveletReq The HTTP servlet request to extract headers (e.g., Origin).
	 *  @return ResponseEntity containing the built notification content as a JSON string.
	 */

	@PostMapping("/buildNotificationContent")
	public ResponseEntity<String> buildNotificationContent(@RequestBody NotificationRequest request, HttpServletRequest serveletReq)
	{
		try
		{
			String clientURL = serveletReq.getHeader("Origin");
			
			// Call the service to build content
			String res = contentBuildingService.buildContent(request, clientURL);
			
			return ResponseEntity.ok()
			        .contentType(MediaType.APPLICATION_JSON)
			        .body("\"" + res + "\"");
			
		}
		
		catch (Exception e) 
		{
            System.err.println("Error building notification content: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("\"Error processing notification: " + e.getMessage() + "\"");
        }
		
	}
}








