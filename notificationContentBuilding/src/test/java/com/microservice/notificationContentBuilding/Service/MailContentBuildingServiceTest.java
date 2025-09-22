package com.microservice.notificationContentBuilding.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.dto.input_dto.UserInputDTO.NotificationBody;



@ExtendWith(MockitoExtension.class)
class MailContentBuildingServiceTest 
{
    @Mock
    private TemplateEngine templateEngine;
    
    private MailContentBuildingService mailContentBuildingService;
    private NotificationRequest notificationRequest;
    private UserInfo userInfo;
    private NotificationBody notificationBody;

    
    
    @BeforeEach
    void setUp() 
    {
        mailContentBuildingService = new MailContentBuildingService(templateEngine);
        
        // Setup test data
        userInfo = new UserInfo();
        notificationBody = new NotificationBody();
        
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserInfo(userInfo);
        notificationRequest.setNotificationBody(notificationBody);
    }
    
    
    @Test
    void testBuildHTML_ValidRequest() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "john_doe";
        String message = "You have a new notification";
        String expectedHTML = "<html><body>Hi john_doe, You have a new notification</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi john_doe, You have a new notification") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_NullURL() 
    {
        // Arrange
        String testURL = null;
        String username = "jane_doe";
        String message = "Welcome to our platform";
        String expectedHTML = "<html><body>Hi jane_doe, Welcome to our platform</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi jane_doe, Welcome to our platform") &&
                   ctx.getVariable("buttonUrl") == null;
        }));
    }
    
    
    @Test
    void testBuildHTML_EmptyURL() 
    {
        // Arrange
        String testURL = "";
        String username = "test_user";
        String message = "Your order has been processed";
        String expectedHTML = "<html><body>Hi test_user, Your order has been processed</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi test_user, Your order has been processed") &&
                   ctx.getVariable("buttonUrl").equals("");
        }));
    }
    
    
    @Test
    void testBuildHTML_NullUsername() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = null;
        String message = "Account verification required";
        String expectedHTML = "<html><body>Hi null, Account verification required</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi null, Account verification required") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_EmptyUsername() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "";
        String message = "Password reset requested";
        String expectedHTML = "<html><body>Hi , Password reset requested</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi , Password reset requested") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_NullMessage() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "admin";
        String message = null;
        String expectedHTML = "<html><body>Hi admin, null</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi admin, null") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_EmptyMessage() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "user123";
        String message = "";
        String expectedHTML = "<html><body>Hi user123, </body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi user123, ") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_LongURL() 
    {
        // Arrange
        String longURL = "https://very-long-domain-name-for-testing-purposes.example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2&param3=value3";
        String username = "longurl_user";
        String message = "Click the button below to proceed";
        String expectedHTML = "<html><body>Hi longurl_user, Click the button below to proceed</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, longURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi longurl_user, Click the button below to proceed") &&
                   ctx.getVariable("buttonUrl").equals(longURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_SpecialCharactersInUsername() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "user@domain.com";
        String message = "Special characters test";
        String expectedHTML = "<html><body>Hi user@domain.com, Special characters test</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi user@domain.com, Special characters test") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_SpecialCharactersInMessage() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "testuser";
        String message = "Message with special chars: <>&\"'";
        String expectedHTML = "<html><body>Hi testuser, Message with special chars: <>&\"'</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals("Hi testuser, Message with special chars: <>&\"'") &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_LongMessage() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "longmessage_user";
        String message = "This is a very long message that contains multiple sentences and should be handled properly by the service. It includes various punctuation marks, numbers like 123, and different formatting. The message should be processed correctly regardless of its length.";
        String expectedBody = "Hi longmessage_user, " + message;
        String expectedHTML = "<html><body>" + expectedBody + "</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals(expectedBody) &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_TemplateEngineReturnsNull() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "testuser";
        String message = "Test message";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(null);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertNull(result);
        verify(templateEngine).process(eq("notification_email_template"), any(Context.class));
    }
    
    
    @Test
    void testBuildHTML_TemplateEngineReturnsEmptyString() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "testuser";
        String message = "Test message";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn("");
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals("", result);
        verify(templateEngine).process(eq("notification_email_template"), any(Context.class));
    }
    
    
    @Test
    void testBuildHTML_WhitespaceInUsernameAndMessage() 
    {
        // Arrange
        String testURL = "https://example.com";
        String username = "  user with spaces  ";
        String message = "  message with leading and trailing spaces  ";
        String expectedBody = "Hi   user with spaces  ,   message with leading and trailing spaces  ";
        String expectedHTML = "<html><body>" + expectedBody + "</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> 
        {
            Context ctx = (Context) context;
            return ctx.getVariable("body").equals(expectedBody) &&
                   ctx.getVariable("buttonUrl").equals(testURL);
        }));
    }
    
    
    @Test
    void testBuildHTML_ContextVariablesSetCorrectly() 
    {
        // Arrange
        String testURL = "https://example.com/verify";
        String username = "context_test_user";
        String message = "Verify your email address";
        String expectedHTML = "<html><body>Context test</body></html>";
        
        userInfo.setUsername(username);
        notificationBody.setMesssage(message);
        
        when(templateEngine.process(eq("notification_email_template"), any(Context.class)))
            .thenReturn(expectedHTML);
        
        // Act
        String result = mailContentBuildingService.buildHTML(notificationRequest, testURL);
        
        // Assert
        assertEquals(expectedHTML, result);
        verify(templateEngine).process(eq("notification_email_template"), argThat(context -> {
            Context ctx = (Context) context;
            // Verify both context variables are set correctly
            Object bodyVar = ctx.getVariable("body");
            Object buttonUrlVar = ctx.getVariable("buttonUrl");
            
            return bodyVar != null && 
                   buttonUrlVar != null &&
                   bodyVar.equals("Hi context_test_user, Verify your email address") &&
                   buttonUrlVar.equals(testURL);
        }));
    }
}








