package com.microservice.userDetails.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.userDetails.Service.UserDetailsService;
import com.dto.input_dto.UserInputDTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@ExtendWith(MockitoExtension.class)
class UserDetailsControllerTest 
{
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private UserDetailsController userDetailsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    
    
    @BeforeEach
    void setUp() 
    {
        mockMvc = MockMvcBuilders.standaloneSetup(userDetailsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void submitUserDetails_Success() throws Exception 
    {
        // Arrange
        NotificationRequest request = createValidNotificationRequest();
        Map<String, Object> successResponse = createSuccessResponse();
        
        when(userDetailsService.takeUserDetails(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok(successResponse));

        // Act & Assert
        mockMvc.perform(post("/api/user/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userDetailsService, times(1)).takeUserDetails(any(NotificationRequest.class));
    }

    @Test
    void submitUserDetails_BadRequest() throws Exception 
    {
        // Arrange
        NotificationRequest request = createInvalidNotificationRequest();
        Map<String, Object> errorResponse = createErrorResponse();
        
        when(userDetailsService.takeUserDetails(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.badRequest().body(errorResponse));

        // Act & Assert
        mockMvc.perform(post("/api/user/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").exists());

        verify(userDetailsService, times(1)).takeUserDetails(any(NotificationRequest.class));
    }

    private NotificationRequest createValidNotificationRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        // Create UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("user123");
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");
        userInfo.setDeviceId("device-123");
        
        // Create UserPreferences
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        
        // Create NotificationBody
        NotificationBody notificationBody = new NotificationBody();
        notificationBody.setHeader("This is a test notification header");
        notificationBody.setSubject("This is a test subject for email notification validation");
        notificationBody.setMesssage("This is a test message content that meets the minimum character requirement for notification body validation rules and should be between 150 and 200 characters");
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        request.setHTMLString("<html><body>Test HTML</body></html>");
        
        return request;
    }

    private NotificationRequest createInvalidNotificationRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        // Create UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("user123");
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");
        userInfo.setDeviceId("device-123");
        
        // Create UserPreferences with EMAIL type
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        
        // Create NotificationBody without subject (invalid for EMAIL)
        NotificationBody notificationBody = new NotificationBody();
        notificationBody.setHeader("This is a test notification header");
        // Missing subject for EMAIL type
        notificationBody.setMesssage("This is a test message content that meets the minimum character requirement for notification body validation rules and should be between 150 and 200 characters");
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    private Map<String, Object> createSuccessResponse() 
    {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User info received successfully");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    private Map<String, Object> createErrorResponse() 
    {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Subject is required for EMAIL notification type");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}










