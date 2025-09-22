package com.microservice.notificationSend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.notificationSend.repository.DLQRepository;


/** * Service class to send notification emails using Brevo API.
 * Implements retry mechanism and dead-letter queue (DLQ) handling.
 */

@Service
public class NotificationMailService 
{
	String apiKey;
	private final RestTemplate restTemplate = new RestTemplate();
	private final DLQRepository dlqRepository;
	private int retryCount=1;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	public NotificationMailService(DLQRepository dlqRepository) 
	{
		this.dlqRepository = dlqRepository;
	}
	
	@PostConstruct
    public void initialize() 
	{
		// Load the API key from environment variable
        this.apiKey = System.getenv("BREVO_API_KEY");
    }
	
	
	/** Sends a notification email using the Brevo API.
	 * 
	 * This method constructs the email payload, sets the necessary headers,
	 * and makes a POST request to the Brevo API endpoint. 
	 * It implements retry logic for handling failures and logs attempts
	 * to a Dead Letter Queue (DLQ) repository.
	 *
	 * @param request The notification request containing user and notification details.
	 * @param HTMLString The HTML content of the email.
	 * @return A list containing the message ID and response details.
	 * @throws RuntimeException if the email sending fails after retries.
	 */
	
	
	@Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))  //Retry mechanism
	public List<String> sendMail(NotificationRequest request, String HTMLString)
	{
		try
		{
			//Sender and recipient details
			String senderName = "Jeet Narayan Chakraborty";
			String senderMail = "chakra.n.jeet@gmail.com";
			String recipientName = request.getUserInfo().getUsername();
			UserInfo userInfo = request.getUserInfo();
			String recipientMail = userInfo.getEmail();
			String mailSubject = request.getNotificationBody().getSubject();
			String mailContent = HTMLString;
			
			// Brevo API endpoint
			String url = "https://api.brevo.com/v3/smtp/email"; 
			
			Map<String, Object> payload = new HashMap<>(); 
			
			//Sender details
			Map<String, String> sender = new HashMap<>();  
			sender.put("name", senderName);
			sender.put("email", senderMail);
			
			//Recipient details
			List<Object> to = new ArrayList<>();
			Map<String, String> recipient = new HashMap<>(); 
			recipient.put("email", recipientMail);
			recipient.put("name", recipientName);
			
			to.add(recipient);
			
			//Payload to send
			payload.put("sender", sender);
			payload.put("to", to);
			payload.put("subject", mailSubject);
			payload.put("htmlContent", mailContent);

			//Headers
			HttpHeaders headers = new HttpHeaders();
	        headers.set("api-key", apiKey);
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
	        //Http entity with payload and headers
	        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);
	        
	        //Post request to Mail API
	        ResponseEntity<Map> response = restTemplate.postForEntity(url, httpEntity, Map.class);  

	        List<String> res = new ArrayList<>();
	        res.add("Message :" + response.getBody().get("messageId").toString());
	        res.add("Response :" + ResponseEntity.ok().body(response));
	        
	        
	        String id = UUID.randomUUID().toString();      
			String channelType = "EMAIL";
			String reciepient = request.getUserInfo().getEmail();
			String originalPayload = objectMapper.writeValueAsString(request);
			int attemptCount = retryCount;
			LocalDateTime lastAttempt = LocalDateTime.now();
			String failureReason = null;
			LocalDateTime failedAt = null;
			String status = "SUCCESS";
	        
			
	        //Inserting successful notification to DLQ table with status as SUCCESS
			dlqRepository.insertData(id, channelType, reciepient, originalPayload, attemptCount, lastAttempt, failureReason, failedAt, status);

			return res;
		}
		
		catch(Exception e)
		{
			retryCount++;
			throw new RuntimeException("Notification mail failed to send", e);
		}
	}
	
	
	
	/**Recovery method invoked when all retry attempts for sending a push notification fail.
	 * 
	 * This method logs the failed notification attempt to the Dead Letter Queue (DLQ)
	 * repository with relevant details such as recipient, original payload, and failure reason.
	 *
	 * @param request the NotificationRequest that failed to send.
	 */
	
	@Recover
	public void recover(NotificationRequest request) 
	{
		// Get details for DLQ entry
		String id = UUID.randomUUID().toString();
		String channelType = "EMAIL";
		String reciepient = request.getUserInfo().getEmail();
		String originalPayload = "";
		
		try
		{
			//Convert the original request to JSON string
			originalPayload = objectMapper.writeValueAsString(request);
		}
		
		catch(Exception e) {}
		
		// Failed attempt details
		int attemptCount = 3;
		LocalDateTime lastAttempt = LocalDateTime.now();
		String failureReason = "All retry attempts failed";
		LocalDateTime failedAt = LocalDateTime.now();
		String status = "DEAD_LETTER";
		
		
		//Inserting failed notification to DLQ table with status as FAILED
		dlqRepository.insertData(id, channelType, reciepient, originalPayload, attemptCount, lastAttempt, failureReason, failedAt, status);
	}
}



















