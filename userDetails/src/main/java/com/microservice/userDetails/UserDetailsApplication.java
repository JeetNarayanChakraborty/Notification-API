package com.microservice.userDetails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



/** Main application class for the User Details microservice.
 * This class is main entry point for the notification API.
 * It takes user details and preferences, then forwards them to the Notification Content Building microservice.
 * 
 * Features:
 * - Spring Boot Application
 * - Eureka Client for service discovery
 * - Feign Clients for declarative REST client functionality
 * 
 */


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserDetailsApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(UserDetailsApplication.class, args);
	}
}
