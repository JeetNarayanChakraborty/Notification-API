package com.microservice.notificationContentBuilding.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.UserPreferences;
import com.microservice.notificationContentBuilding.FeignClient.NotificationSendServiceClient;



@ExtendWith(MockitoExtension.class)
class NotificationContentBuildingServiceTest 
{
    @Mock
    private MailContentBuildingService mailContentBuildingService;
    
    @Mock
    private PushContentBuildingService pushContentBuildingService;
    
    @Mock
    private NotificationSendServiceClient notificationSendServiceClient;
    
    private ContentBuildingService contentBuildingService;
    
    private NotificationRequest notificationRequest;
    private UserInfo userInfo;
    private UserPreferences preferences;
    
    
    @BeforeEach
    void setUp() 
    {
        contentBuildingService = new ContentBuildingService(
            mailContentBuildingService,
            pushContentBuildingService,
            notificationSendServiceClient
        );
        
        // Setup test data
        preferences = new UserPreferences();
        userInfo = new UserInfo();
        userInfo.setPreferences(preferences);
        
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserInfo(userInfo);
    }
    
    
    @Test
    void testBuildContent_EmailOnly() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedHTMLContent = "<html>Test Email Content</html>";
        String expectedResponse = "Email sent successfully";
        
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn(expectedHTMLContent);
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
        verify(pushContentBuildingService, never()).personalizeContent(any());
        verify(notificationSendServiceClient).sendNotificationContent(argThat(req -> 
            req.getHTMLString().equals(expectedHTMLContent)
        ));
    }
    
    
    @Test
    void testBuildContent_PushOnly() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedResponse = "Push notification sent successfully";
        NotificationRequest personalizedRequest = new NotificationRequest();
        
        preferences.setNotificationType(Arrays.asList("PUSH"));
        
        when(pushContentBuildingService.personalizeContent(notificationRequest))
            .thenReturn(personalizedRequest);
        when(notificationSendServiceClient.sendNotificationContent(personalizedRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService, never()).buildHTML(any(), any());
        verify(pushContentBuildingService).personalizeContent(notificationRequest);
        verify(notificationSendServiceClient).sendNotificationContent(personalizedRequest);
    }
    
    
    @Test
    void testBuildContent_BothEmailAndPush() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedHTMLContent = "<html>Test Email Content</html>";
        String expectedResponse = "Both notifications sent successfully";
        NotificationRequest personalizedRequest = new NotificationRequest();
        
        preferences.setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn(expectedHTMLContent);
        when(pushContentBuildingService.personalizeContent(notificationRequest))
            .thenReturn(personalizedRequest);
        when(notificationSendServiceClient.sendNotificationContent(personalizedRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
        verify(pushContentBuildingService).personalizeContent(notificationRequest);
        verify(notificationSendServiceClient).sendNotificationContent(argThat(req -> 
            req.getHTMLString().equals(expectedHTMLContent)
        ));
    }
    
    
    @Test
    void testBuildContent_EmptyNotificationTypeList() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedResponse = "No notifications sent";
        
        preferences.setNotificationType(Collections.emptyList());
        
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService, never()).buildHTML(any(), any());
        verify(pushContentBuildingService, never()).personalizeContent(any());
        verify(notificationSendServiceClient).sendNotificationContent(notificationRequest);
    }
    
    
    @Test
    void testBuildContent_UnknownNotificationType() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedResponse = "SMS notification sent";
        
        preferences.setNotificationType(Arrays.asList("SMS"));
        
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService, never()).buildHTML(any(), any());
        verify(pushContentBuildingService, never()).personalizeContent(any());
        verify(notificationSendServiceClient).sendNotificationContent(notificationRequest);
    }
    
    
    @Test
    void testBuildContent_MixedValidAndInvalidTypes() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedHTMLContent = "<html>Test Email Content</html>";
        String expectedResponse = "Mixed notifications sent";
        
        preferences.setNotificationType(Arrays.asList("EMAIL", "SMS", "PUSH"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn(expectedHTMLContent);
        when(pushContentBuildingService.personalizeContent(notificationRequest))
            .thenReturn(notificationRequest);
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
        verify(pushContentBuildingService).personalizeContent(notificationRequest);
        verify(notificationSendServiceClient).sendNotificationContent(argThat(req -> 
            req.getHTMLString().equals(expectedHTMLContent)
        ));
    }
    
    
    @Test
    void testBuildContent_NullURL() 
    {
        // Arrange
        String testURL = null;
        String expectedResponse = "Notification sent with null URL";
        
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn("<html>Content with null URL</html>");
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
    }
    
    
    @Test
    void testBuildContent_EmptyURL() 
    {
        // Arrange
        String testURL = "";
        String expectedResponse = "Notification sent with empty URL";
        
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn("<html>Content with empty URL</html>");
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
    }
    
    
    @Test
    void testBuildContent_MailContentBuildingReturnsNull() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedResponse = "Notification sent without HTML";
        
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn(null);
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, testURL);
        verify(notificationSendServiceClient).sendNotificationContent(argThat(req -> 
            req.getHTMLString() == null
        ));
    }
    
    
    @Test
    void testBuildContent_DuplicateNotificationTypes() 
    {
        // Arrange
        String testURL = "https://example.com";
        String expectedHTMLContent = "<html>Duplicate Email Content</html>";
        String expectedResponse = "Duplicate notifications handled";
        
        preferences.setNotificationType(Arrays.asList("EMAIL", "EMAIL", "PUSH", "PUSH"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, testURL))
            .thenReturn(expectedHTMLContent);
        when(pushContentBuildingService.personalizeContent(notificationRequest))
            .thenReturn(notificationRequest);
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        // Should be called twice due to duplicates
        verify(mailContentBuildingService, times(2)).buildHTML(notificationRequest, testURL);
        verify(pushContentBuildingService, times(2)).personalizeContent(notificationRequest);
    }
    
    
    @Test
    void testBuildContent_LongURL() 
    {
        // Arrange
        String longURL = "https://very-long-domain-name-for-testing-purposes.example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2&param3=value3";
        String expectedResponse = "Long URL handled successfully";
        
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        
        when(mailContentBuildingService.buildHTML(notificationRequest, longURL))
            .thenReturn("<html>Content with long URL</html>");
        when(notificationSendServiceClient.sendNotificationContent(notificationRequest))
            .thenReturn(expectedResponse);
        
        // Act
        String result = contentBuildingService.buildContent(notificationRequest, longURL);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(mailContentBuildingService).buildHTML(notificationRequest, longURL);
    }
}







