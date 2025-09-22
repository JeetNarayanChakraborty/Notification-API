package com.microservice.notificationContentBuilding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/** 
 * Main application class for the Notification Content Building microservice.
 * It is responsible for generating personalized notification content based on user details and preferences.
 * 
 * Features:
 * - Personalizes notification content for users
 * - Generates HTML email content using Thymeleaf templates (if applicable)
 */

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class NotificationContentBuildingApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(NotificationContentBuildingApplication.class, args);
	}
}
