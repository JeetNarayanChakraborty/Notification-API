package com.microservice.notificationSend.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.microservice.notificationSend.repository.DLQRepository;


/** * Service class for sending push notifications using Firebase Cloud Messaging (FCM).
 * * This class handles the initialization of Firebase, sending push notifications,
 * 
 * Features:
 * * - Initializes Firebase using service account credentials.
 * * - Sends push notifications to specified device tokens.
 * * - Implements retry logic for failed notification attempts.
 * * - Logs notification attempts and failures to a Dead Letter Queue (DLQ) repository.
 */


@Service
public class NotificationPushService 
{
	private final DLQRepository dlqRepository;
	private int retryCount=1;
	private String FIREBASE_SERVICE_ACCOUNT;
	private final FirebaseMessaging firebaseMessaging;
	private final ObjectMapper objectMapper;
	
	
	public NotificationPushService(DLQRepository dlqRepository, FirebaseMessaging firebaseMessaging, ObjectMapper objectMapper) 
	{
	    this.dlqRepository = dlqRepository;
	    this.firebaseMessaging = firebaseMessaging;
	    this.objectMapper = objectMapper;
	}

	
	/**
	 * Initializes the Firebase application with service account credentials.
	 * 
	 * It reads the service account file path from environment variables
	 * and initializes the FirebaseApp instance.
	 *
	 * @throws IOException if there is an error reading the service account file.
	 */
	
	
	/** Sends a push notification using Firebase Cloud Messaging (FCM).
	 * 
	 * This method constructs a notification message using the provided
	 * NotificationRequest object and sends it to the specified device.
	 * It implements retry logic for handling failures and logs attempts
	 * to a Dead Letter Queue (DLQ) repository.
	 *
	 * @param request the NotificationRequest containing user info and notification body.
	 * @return a list of strings indicating the result of the send operation.
	 * @throws RuntimeException if the notification fails to send after retries.
	 */
	
	@Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2)) //Retry mechanism
	public List<String> sendPush(NotificationRequest request)
	{
		try
		{
			if(request == null) return List.of("Notification request is null");
			if(request.getUserInfo() == null) return List.of("User info is missing in the notification request");
			        
			String deviceToken = request.getUserInfo().getDeviceId();  //Get device token from user info
			String title = request.getNotificationBody().getHeader();  //Get title from notification body
			String body = request.getNotificationBody().getMesssage();  //Get body from notification body
			String response = "";
			
			//Build the message to send
			Message message = Message.builder()   
		                .setToken(deviceToken)
		                .setNotification(Notification.builder()
		                .setTitle(title)
		                .setBody(body)
		                .build())
		                .putAllData(new HashMap<>())
		                .build();
			
			
			//Send the message via Firebase
			response = firebaseMessaging.send(message); //Send the message via FCM
	    
		    
		    List<String> res = new ArrayList<>();
		    res.add("Message :" + "Successfully sent message");
		    res.add("Response :" + ResponseEntity.ok().body(response));
		    
		    
		    String id = UUID.randomUUID().toString();
		    String channelType = "PUSH";
		    String reciepient = request.getUserInfo().getDeviceId();
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
			//Increment retry count on failure
			retryCount++;
			e.printStackTrace(); 
			throw new RuntimeException("Notification push failed to send", e);
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
		String channelType = "PUSH";
		String reciepient = request.getUserInfo().getDeviceId();
		String originalPayload = "";
		
		try
		{
			//Convert the original request to JSON string
			originalPayload = objectMapper.writeValueAsString(request);
		}
		
		catch(Exception e) {}
		
		// Set failure details
		int attemptCount = 3;
		LocalDateTime lastAttempt = LocalDateTime.now();
		String failureReason = "All retry attempts failed";
		LocalDateTime failedAt = LocalDateTime.now();
		String status = "DEAD_LETTER";
		
		
		//Inserting failed notification to DLQ table with status as DEAD_LETTER
		dlqRepository.insertData(id, channelType, reciepient, originalPayload, attemptCount, lastAttempt, failureReason, failedAt, status);
	}
}


@Configuration
class FirebaseConfig 
{
    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException 
    {
        String serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT");
        
        try(InputStream serviceAccount = new FileInputStream(serviceAccountPath)) 
        {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if(FirebaseApp.getApps().isEmpty()) 
            {
                FirebaseApp.initializeApp(options);
            }
        }
        
        return FirebaseMessaging.getInstance();
    }
}

































