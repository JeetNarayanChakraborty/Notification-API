package com.microservice.notificationSend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;


/** Main application class for the Notification Send microservice. 
 *	This class is responsible for sending notifications to users via different channels such as email and push notifications.
 * 
 * Features:
 * - Sends notifications based on user preferences
 * - Supports multiple notification channels (EMAIL, PUSH)
 * */

@EnableDiscoveryClient
@SpringBootApplication
@EnableRetry
public class NotificationSendApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(NotificationSendApplication.class, args);
	}
}
