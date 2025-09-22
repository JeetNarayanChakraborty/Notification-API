package com.microservice.userDetails.Controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.userDetails.Service.UserDetailsService;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import jakarta.validation.Valid;


/**
 * Controller class for handling user details submission.
 * This class defines REST endpoints for the User Details microservice.
 * It receives the main payload containing notification data, user information and preferences.
 * and forwards the data to the UserDetailsService for processing.
 * 
 * Features:
 * - Endpoint for submitting user details
 * - Input validation
 */



@RestController
@RequestMapping("/api/user")
public class UserDetailsController 
{
	private final UserDetailsService userDetailsService;
	
	
	
	// Constructor-based dependency injection
	public UserDetailsController(UserDetailsService userDetailsService)
	{
		this.userDetailsService = userDetailsService;
	}
	
	
	/**
	 * Endpoint to submit user details.
	 * 
	 * @param request The notification request payload containing user details and preferences.
	 * @return A ResponseEntity containing the result of the submission.
	 */
	
	@PostMapping("/submit")
	public ResponseEntity<Map<String, Object>> submitUserDetails(@Valid @RequestBody NotificationRequest request)
	{
		// Delegate the processing to the service layer
		return userDetailsService.takeUserDetails(request);
	}
}








