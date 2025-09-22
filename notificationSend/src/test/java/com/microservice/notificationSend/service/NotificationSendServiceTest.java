package com.microservice.notificationSend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.UserPreferences;




@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSendService Tests")
class NotificationSendServiceTest 
{
    @Mock
    private NotificationMailService mailService;

    @Mock
    private NotificationPushService pushService;

    @InjectMocks
    private NotificationSendService notificationSendService;

    private NotificationRequest notificationRequest;
    private UserInfo userInfo;
    private UserPreferences preferences;

    
    
    @BeforeEach
    void setUp() 
    {
        setupTestData();
    }

    
    private void setupTestData() 
    {
        preferences = new UserPreferences();
        userInfo = new UserInfo();
        notificationRequest = new NotificationRequest();
        
        // Setup relationships
        userInfo.setPreferences(preferences);
        notificationRequest.setUserInfo(userInfo);
        
        // Set HTML string for email notifications
        notificationRequest.setHTMLString("<html><body>Test notification</body></html>");
    }
    

    @Test
    @DisplayName("Should send EMAIL notification successfully")
    void testSendNotification_EmailOnly() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockMailResponse, result.get(0));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, never()).sendPush(any(NotificationRequest.class));
    }

    
    @Test
    @DisplayName("Should send PUSH notification successfully")
    void testSendNotification_PushOnly() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("PUSH"));
        List<String> mockPushResponse = Arrays.asList("SUCCESS", "Push notification sent successfully");
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(mockPushResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockPushResponse, result.get(0));
        
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
        verify(mailService, never()).sendMail(any(NotificationRequest.class), anyString());
    }

    
    @Test
    @DisplayName("Should send both EMAIL and PUSH notifications")
    void testSendNotification_EmailAndPush() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        List<String> mockPushResponse = Arrays.asList("SUCCESS", "Push notification sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(mockPushResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockMailResponse, result.get(0));
        assertEquals(mockPushResponse, result.get(1));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle multiple EMAIL notifications")
    void testSendNotification_MultipleEmails() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL", "EMAIL"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockMailResponse, result.get(0));
        assertEquals(mockMailResponse, result.get(1));
        
        verify(mailService, times(2)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, never()).sendPush(any(NotificationRequest.class));
    }

    
    @Test
    @DisplayName("Should handle empty notification type list")
    void testSendNotification_EmptyChannels() 
    {
        // Given
        preferences.setNotificationType(Collections.emptyList());

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(mailService, never()).sendMail(any(NotificationRequest.class), anyString());
        verify(pushService, never()).sendPush(any(NotificationRequest.class));
    }

    
    @Test
    @DisplayName("Should handle unknown notification type")
    void testSendNotification_UnknownChannel() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("SMS", "FAX"));

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(mailService, never()).sendMail(any(NotificationRequest.class), anyString());
        verify(pushService, never()).sendPush(any(NotificationRequest.class));
    }

    
    @Test
    @DisplayName("Should handle mixed valid and invalid notification types")
    void testSendNotification_MixedChannels() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL", "SMS", "PUSH", "FAX"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        List<String> mockPushResponse = Arrays.asList("SUCCESS", "Push notification sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(mockPushResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Only EMAIL and PUSH should be processed
        assertEquals(mockMailResponse, result.get(0));
        assertEquals(mockPushResponse, result.get(1));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle case sensitive notification types")
    void testSendNotification_CaseSensitive() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("email", "Email", "EMAIL", "push", "Push", "PUSH"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        List<String> mockPushResponse = Arrays.asList("SUCCESS", "Push notification sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(mockPushResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Only "EMAIL" and "PUSH" (uppercase) should match
        assertEquals(mockMailResponse, result.get(0));
        assertEquals(mockPushResponse, result.get(1));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle mail service returning null")
    void testSendNotification_MailServiceReturnsNull() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(null);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
    }

    
    @Test
    @DisplayName("Should handle push service returning null")
    void testSendNotification_PushServiceReturnsNull() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("PUSH"));
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(null);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle services returning empty responses")
    void testSendNotification_ServicesReturnEmpty() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        List<String> emptyResponse = Collections.emptyList();
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(emptyResponse);
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(emptyResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isEmpty());
        assertTrue(result.get(1).isEmpty());
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, times(1)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle complex notification order")
    void testSendNotification_ComplexOrder() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("PUSH", "EMAIL", "PUSH", "EMAIL"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        List<String> mockPushResponse = Arrays.asList("SUCCESS", "Push notification sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);
        when(pushService.sendPush(any(NotificationRequest.class)))
            .thenReturn(mockPushResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(mockPushResponse, result.get(0)); // First PUSH
        assertEquals(mockMailResponse, result.get(1)); // First EMAIL
        assertEquals(mockPushResponse, result.get(2)); // Second PUSH
        assertEquals(mockMailResponse, result.get(3)); // Second EMAIL
        
        verify(mailService, times(2)).sendMail(eq(notificationRequest), eq("<html><body>Test notification</body></html>"));
        verify(pushService, times(2)).sendPush(eq(notificationRequest));
    }

    
    @Test
    @DisplayName("Should handle null HTML string for email")
    void testSendNotification_NullHTMLString() 
    {
        // Given
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        notificationRequest.setHTMLString(null);
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), any()))
            .thenReturn(mockMailResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockMailResponse, result.get(0));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq(null));
    }
    

    @Test
    @DisplayName("Should pass correct HTML string to mail service")
    void testSendNotification_CorrectHTMLString() 
    {
        // Given
        String customHtml = "<html><body><h1>Custom Notification</h1><p>Important message</p></body></html>";
        notificationRequest.setHTMLString(customHtml);
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        List<String> mockMailResponse = Arrays.asList("SUCCESS", "Email sent successfully");
        
        when(mailService.sendMail(any(NotificationRequest.class), anyString()))
            .thenReturn(mockMailResponse);

        // When
        List<List<String>> result = notificationSendService.sendNotification(notificationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockMailResponse, result.get(0));
        
        verify(mailService, times(1)).sendMail(eq(notificationRequest), eq(customHtml));
    }
}





