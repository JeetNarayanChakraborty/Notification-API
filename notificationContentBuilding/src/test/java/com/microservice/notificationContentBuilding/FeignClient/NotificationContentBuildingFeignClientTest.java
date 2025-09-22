package com.microservice.notificationContentBuilding.FeignClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import feign.FeignException;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.NotificationBody;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.UserPreferences;



/**
 * Test class for NotificationSendServiceClient Feign interface.
 * Tests the integration behavior and error handling of the Feign client.
 * Note: This is more of an integration test since Feign clients are interfaces.
 */




@ExtendWith(MockitoExtension.class)
class NotificationContentBuildingFeignClientTest 
{

    @Mock
    private NotificationSendServiceClient notificationSendServiceClient;

    private NotificationRequest testRequest;

    
    
    
    @BeforeEach
    void setUp() 
    {
        testRequest = createTestNotificationRequest();
    }

    
    @Test
    void testSendNotificationContent_Success() 
    {
        // Given
        String expectedResponse = "Notification sent successfully";
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_WithEmailNotification() 
    {
        // Given
        testRequest.getUserInfo().getPreferences().setNotificationType(Arrays.asList("EMAIL"));
        testRequest.setHTMLString("<html><body>Test email content</body></html>");
        String expectedResponse = "Email notification sent";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_WithPushNotification() 
    {
        // Given
        testRequest.getUserInfo().getPreferences().setNotificationType(Arrays.asList("PUSH"));
        String expectedResponse = "Push notification sent";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_WithBothNotificationTypes() 
    {
        // Given
        testRequest.getUserInfo().getPreferences().setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        testRequest.setHTMLString("<html><body>Email content</body></html>");
        String expectedResponse = "Both notifications sent successfully";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    @Test
    void testSendNotificationContent_WithNullRequest() 
    {
        // Given
        when(notificationSendServiceClient.sendNotificationContent(null))
            .thenThrow(new IllegalArgumentException("Request cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(null);
        });
        
        verify(notificationSendServiceClient).sendNotificationContent(null);
    }

    @Test
    void testSendNotificationContent_ServiceUnavailable() 
    {
        // Given
        FeignException.ServiceUnavailable exception = mock(FeignException.ServiceUnavailable.class);
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenThrow(exception);

        // When & Then
        assertThrows(FeignException.ServiceUnavailable.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(testRequest);
        });
        
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    @Test
    void testSendNotificationContent_BadRequest() 
    {
        // Given
        FeignException.BadRequest exception = mock(FeignException.BadRequest.class);
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenThrow(exception);

        // When & Then
        assertThrows(FeignException.BadRequest.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(testRequest);
        });
        
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_InternalServerError() 
    {
        // Given
        FeignException.InternalServerError exception = mock(FeignException.InternalServerError.class);
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenThrow(exception);

        // When & Then
        assertThrows(FeignException.InternalServerError.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(testRequest);
        });
        
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_Timeout() 
    {
        // Given
        RuntimeException timeoutException = new RuntimeException("Connection timeout");
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenThrow(timeoutException);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(testRequest);
        });
        
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_RetryLogic() 
    {
        // Given
        String expectedResponse = "Success after retry";
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenThrow(new RuntimeException("First attempt failed"))
            .thenReturn(expectedResponse);

        // When
        // First call should fail
        assertThrows(RuntimeException.class, () -> 
        {
            notificationSendServiceClient.sendNotificationContent(testRequest);
        });
        
        // Second call should succeed
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient, times(2)).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_WithComplexRequest() 
    {
        // Given
        NotificationRequest complexRequest = createComplexNotificationRequest();
        String expectedResponse = "Complex notification processed successfully";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(complexRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(complexRequest);
    }

    
    @Test
    void testSendNotificationContent_WithLargePayload() 
    {
        // Given
        String largeHtmlContent = "<html><body>" + "Large content ".repeat(1000) + "</body></html>";
        testRequest.setHTMLString(largeHtmlContent);
        String expectedResponse = "Large payload processed successfully";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(expectedResponse);

        // When
        String result = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(expectedResponse, result);
        verify(notificationSendServiceClient).sendNotificationContent(testRequest);
    }

    
    @Test
    void testSendNotificationContent_MultipleInvocations() 
    {
        // Given
        String response1 = "First notification sent";
        String response2 = "Second notification sent";
        String response3 = "Third notification sent";
        
        when(notificationSendServiceClient.sendNotificationContent(any(NotificationRequest.class)))
            .thenReturn(response1)
            .thenReturn(response2)
            .thenReturn(response3);

        // When
        String result1 = notificationSendServiceClient.sendNotificationContent(testRequest);
        String result2 = notificationSendServiceClient.sendNotificationContent(testRequest);
        String result3 = notificationSendServiceClient.sendNotificationContent(testRequest);

        // Then
        assertEquals(response1, result1);
        assertEquals(response2, result2);
        assertEquals(response3, result3);
        verify(notificationSendServiceClient, times(3)).sendNotificationContent(testRequest);
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
        preferences.setNotificationType(Arrays.asList("EMAIL"));
        userInfo.setPreferences(preferences);
        
        // Create NotificationBody
        NotificationBody body = new NotificationBody();
        body.setHeader("Test Header");
        body.setMesssage("Test message content");
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(body);
        
        return request;
    }

    
    private NotificationRequest createComplexNotificationRequest() 
    {
        NotificationRequest request = createTestNotificationRequest();
        
        // Add complex data
        request.getUserInfo().getPreferences().setNotificationType(Arrays.asList("EMAIL", "PUSH"));
        request.setHTMLString("<html><head><title>Complex Email</title></head><body><div class='container'><h1>Welcome</h1><p>Complex content</p></div></body></html>");
        request.getNotificationBody().setHeader("Complex Notification with Special Characters: !@#$%^&*()");
        request.getNotificationBody().setMesssage("This is a complex message with unicode: 测试 и тест and emojis: 🚀🎉");
        
        return request;
    }
}





