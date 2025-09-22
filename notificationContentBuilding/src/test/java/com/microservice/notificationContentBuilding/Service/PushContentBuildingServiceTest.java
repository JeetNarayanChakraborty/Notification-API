package com.microservice.notificationContentBuilding.Service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.NotificationBody;



class PushContentBuildingServiceTest 
{
    private PushContentBuildingService pushContentBuildingService;
    private NotificationRequest notificationRequest;
    private UserInfo userInfo;
    private NotificationBody notificationBody;
    
    
    @BeforeEach
    void setUp() 
    {
        pushContentBuildingService = new PushContentBuildingService();
        
        // Setup test data
        userInfo = new UserInfo();
        notificationBody = new NotificationBody();
        
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserInfo(userInfo);
        notificationRequest.setNotificationBody(notificationBody);
    }
    
    
    @Test
    void testPersonalizeContent_ValidRequest() 
    {
        // Arrange
        String username = "john_doe";
        String originalHeader = "New Update Available";
        String originalMessage = "Please check your dashboard";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("john_doe, New Update Available", result.getNotificationBody().getHeader());
        assertEquals("Hi john_doe, Please check your dashboard", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_NullUsername() 
    {
        // Arrange
        String username = null;
        String originalHeader = "Important Notice";
        String originalMessage = "Action required";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("null, Important Notice", result.getNotificationBody().getHeader());
        assertEquals("Hi null, Action required", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_EmptyUsername() 
    {
        // Arrange
        String username = "";
        String originalHeader = "System Alert";
        String originalMessage = "Maintenance scheduled";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals(", System Alert", result.getNotificationBody().getHeader());
        assertEquals("Hi , Maintenance scheduled", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_NullHeader() 
    {
        // Arrange
        String username = "jane_smith";
        String originalHeader = null;
        String originalMessage = "Welcome to our platform";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("jane_smith, null", result.getNotificationBody().getHeader());
        assertEquals("Hi jane_smith, Welcome to our platform", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_EmptyHeader() 
    {
        // Arrange
        String username = "test_user";
        String originalHeader = "";
        String originalMessage = "Your order has been confirmed";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("test_user, ", result.getNotificationBody().getHeader());
        assertEquals("Hi test_user, Your order has been confirmed", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_NullMessage() 
    {
        // Arrange
        String username = "admin";
        String originalHeader = "Server Status";
        String originalMessage = null;
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("admin, Server Status", result.getNotificationBody().getHeader());
        assertEquals("Hi admin, null", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_EmptyMessage() 
    {
        // Arrange
        String username = "user123";
        String originalHeader = "Notification";
        String originalMessage = "";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("user123, Notification", result.getNotificationBody().getHeader());
        assertEquals("Hi user123, ", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_AllNullValues() 
    {
        // Arrange
        String username = null;
        String originalHeader = null;
        String originalMessage = null;
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("null, null", result.getNotificationBody().getHeader());
        assertEquals("Hi null, null", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_AllEmptyValues() 
    {
        // Arrange
        String username = "";
        String originalHeader = "";
        String originalMessage = "";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals(", ", result.getNotificationBody().getHeader());
        assertEquals("Hi , ", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_SpecialCharactersInUsername() 
    {
        // Arrange
        String username = "user@domain.com";
        String originalHeader = "Account Verification";
        String originalMessage = "Please verify your email address";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("user@domain.com, Account Verification", result.getNotificationBody().getHeader());
        assertEquals("Hi user@domain.com, Please verify your email address", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_SpecialCharactersInHeaderAndMessage() 
    {
        // Arrange
        String username = "testuser";
        String originalHeader = "Alert: <System> & \"Warning\"";
        String originalMessage = "Message with special chars: <>&\"'";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("testuser, Alert: <System> & \"Warning\"", result.getNotificationBody().getHeader());
        assertEquals("Hi testuser, Message with special chars: <>&\"'", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_LongUsernameHeaderAndMessage() 
    {
        // Arrange
        String username = "very_long_username_for_testing_purposes_123456789";
        String originalHeader = "This is a very long header that contains multiple words and should be handled properly by the service without any issues";
        String originalMessage = "This is a very long message that contains multiple sentences and should be processed correctly. It includes various punctuation marks, numbers like 123, and different formatting.";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals(username + ", " + originalHeader, result.getNotificationBody().getHeader());
        assertEquals("Hi " + username + ", " + originalMessage, result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_WhitespaceInValues() 
    {
        // Arrange
        String username = "  user with spaces  ";
        String originalHeader = "  Header with spaces  ";
        String originalMessage = "  Message with leading and trailing spaces  ";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("  user with spaces  ,   Header with spaces  ", result.getNotificationBody().getHeader());
        assertEquals("Hi   user with spaces  ,   Message with leading and trailing spaces  ", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_NumericUsername() 
    {
        // Arrange
        String username = "12345";
        String originalHeader = "Numeric Test";
        String originalMessage = "Testing numeric username";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("12345, Numeric Test", result.getNotificationBody().getHeader());
        assertEquals("Hi 12345, Testing numeric username", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_ModifiesOriginalObject() 
    {
        // Arrange
        String username = "modification_test";
        String originalHeader = "Original Header";
        String originalMessage = "Original Message";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Store original references
        NotificationBody originalBodyRef = notificationRequest.getNotificationBody();
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        // Verify it returns the same request object
        assertSame(notificationRequest, result);
        
        // Verify it modifies the same notification body object
        assertSame(originalBodyRef, result.getNotificationBody());
        
        // Verify the original object's values have been modified
        assertEquals("modification_test, Original Header", originalBodyRef.getHeader());
        assertEquals("Hi modification_test, Original Message", originalBodyRef.getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_UnicodeCharacters() 
    {
        // Arrange
        String username = "用户名";  // Chinese characters
        String originalHeader = "Título";  // Spanish with accent
        String originalMessage = "Mensaje con caracteres especiales: café";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("用户名, Título", result.getNotificationBody().getHeader());
        assertEquals("Hi 用户名, Mensaje con caracteres especiales: café", result.getNotificationBody().getMesssage());
    }
    
    
    @Test
    void testPersonalizeContent_MultipleConsecutiveSpaces() 
    {
        // Arrange
        String username = "user    with    spaces";
        String originalHeader = "Header    with    multiple    spaces";
        String originalMessage = "Message    with    multiple    spaces";
        
        userInfo.setUsername(username);
        notificationBody.setHeader(originalHeader);
        notificationBody.setMesssage(originalMessage);
        
        // Act
        NotificationRequest result = pushContentBuildingService.personalizeContent(notificationRequest);
        
        // Assert
        assertSame(notificationRequest, result);
        assertEquals("user    with    spaces, Header    with    multiple    spaces", result.getNotificationBody().getHeader());
        assertEquals("Hi user    with    spaces, Message    with    multiple    spaces", result.getNotificationBody().getMesssage());
    }
}






