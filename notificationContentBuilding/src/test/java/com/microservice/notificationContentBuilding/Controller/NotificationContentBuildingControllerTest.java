package com.microservice.notificationContentBuilding.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.NotificationBody;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.UserPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.notificationContentBuilding.Service.ContentBuildingService;

import java.util.Arrays;




@ExtendWith(MockitoExtension.class)
class NotificationContentBuildingControllerTest 
{
    @Mock
    private ContentBuildingService contentBuildingService;

    @InjectMocks
    private ContentBuildingController contentBuildingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private NotificationRequest testRequest;

    
    
    
    @BeforeEach
    void setUp() 
    {
        mockMvc = MockMvcBuilders.standaloneSetup(contentBuildingController).build();
        objectMapper = new ObjectMapper();
        
        // Setup test data
        testRequest = createTestNotificationRequest();
    }

    
    @Test
    void testBuildNotificationContent_Success() throws Exception 
    {
        // Given
        String expectedResponse = "Content built successfully";
        when(contentBuildingService.buildContent(any(NotificationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000")
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(expectedResponse));
    }

    
    @Test
    void testBuildNotificationContent_WithNullOriginHeader() throws Exception 
    {
        // Given
        String expectedResponse = "Content built without origin";
        when(contentBuildingService.buildContent(any(NotificationRequest.class), any()))
            .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(expectedResponse));
    }

    
    @Test
    void testBuildNotificationContent_WithEmptyRequest() throws Exception 
    {
        // Given
        NotificationRequest emptyRequest = new NotificationRequest();
        String expectedResponse = "Error: Empty request";
        when(contentBuildingService.buildContent(any(NotificationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000")
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedResponse));
    }

    
    @Test
    void testBuildNotificationContent_ServiceThrowsException() throws Exception 
    {
        // Given
        when(contentBuildingService.buildContent(any(NotificationRequest.class), anyString()))
            .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000")
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isInternalServerError());
    }

    
    @Test
    void testBuildNotificationContent_InvalidJson() throws Exception 
    {
        // When & Then
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000")
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void testBuildNotificationContent_WithDifferentOriginHeaders() throws Exception 
    {
        // Given
        String expectedResponse = "Content built";
        when(contentBuildingService.buildContent(any(NotificationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // Test with HTTPS origin
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://example.com")
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());

        // Test with different port
        mockMvc.perform(post("/api/build/buildNotificationContent")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:8080")
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());
    }

    
    private NotificationRequest createTestNotificationRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        // Create UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("testuser");
        userInfo.setUserId("123");
        userInfo.setEmail("test@example.com");
        
        // Create Preferences
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        userInfo.setPreferences(preferences);
        
        // Create NotificationBody
        NotificationBody body = new NotificationBody();
        body.setHeader("Test Header");
        body.setMesssage("Test message");
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(body);
        
        return request;
    }
}








