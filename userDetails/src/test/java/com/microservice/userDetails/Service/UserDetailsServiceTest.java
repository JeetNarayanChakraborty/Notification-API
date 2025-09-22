package com.microservice.userDetails.Service;

import com.dto.input_dto.UserInputDTO.*;
import com.microservice.userDetails.FeignClient.NotificationContentBuildingServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest 
{
    @Mock
    private NotificationContentBuildingServiceClient notificationClient;

    @InjectMocks
    private UserDetailsService userDetailsService;

    private NotificationRequest validEmailRequest;
    private NotificationRequest validPushRequest;
    private NotificationRequest invalidEmailRequest;
    private NotificationRequest invalidPushRequest;

    @BeforeEach
    void setUp() 
    {
        setupValidEmailRequest();
        setupValidPushRequest();
        setupInvalidEmailRequest();
        setupInvalidPushRequest();
    }

    @Test
    void takeUserDetails_ValidEmailRequest_Success() 
    {
        // Arrange
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(validEmailRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("success", result.getBody().get("status"));
        assertEquals("User info received successfully", result.getBody().get("message"));
        assertNotNull(result.getBody().get("timestamp"));

        verify(notificationClient, times(1)).buildNotificationContent(validEmailRequest);
    }

    
    @Test
    void takeUserDetails_ValidPushRequest_Success() 
    {
        // Arrange
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(validPushRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("success", result.getBody().get("status"));
        assertEquals("User info received successfully", result.getBody().get("message"));
        assertNotNull(result.getBody().get("timestamp"));

        verify(notificationClient, times(1)).buildNotificationContent(validPushRequest);
    }

    
    @Test
    void takeUserDetails_EmailRequestMissingSubject_BadRequest() 
    {
        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(invalidEmailRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("error", result.getBody().get("status"));
        assertEquals("Subject is required for EMAIL notification type", result.getBody().get("message"));
        assertNotNull(result.getBody().get("timestamp"));

        verify(notificationClient, never()).buildNotificationContent(any(NotificationRequest.class));
    }

    
    @Test
    void takeUserDetails_PushRequestMissingDeviceId_BadRequest() 
    {
        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(invalidPushRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("error", result.getBody().get("status"));
        assertEquals("Subject is required for EMAIL notification type", result.getBody().get("message"));
        assertNotNull(result.getBody().get("timestamp"));

        verify(notificationClient, never()).buildNotificationContent(any(NotificationRequest.class));
    }

    
    @Test
    void takeUserDetails_BothEmailAndPushValid_Success() 
    {
        // Arrange
        NotificationRequest bothTypesRequest = createBothTypesValidRequest();
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(bothTypesRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));

        verify(notificationClient, times(1)).buildNotificationContent(bothTypesRequest);
    }

    
    @Test
    void takeUserDetails_BothTypesButMissingEmailSubject_BadRequest() 
    {
        // Arrange
        NotificationRequest bothTypesInvalidRequest = createBothTypesInvalidEmailRequest();

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(bothTypesInvalidRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("error", result.getBody().get("status"));

        verify(notificationClient, never()).buildNotificationContent(any(NotificationRequest.class));
    }

    
    @Test
    void takeUserDetails_BothTypesButMissingPushDeviceId_BadRequest() 
    {
        // Arrange
        NotificationRequest bothTypesInvalidPushRequest = createBothTypesInvalidPushRequest();

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(bothTypesInvalidPushRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("error", result.getBody().get("status"));

        verify(notificationClient, never()).buildNotificationContent(any(NotificationRequest.class));
    }

    
    
    
    
    // ================= FEIGN CLIENT TESTING SCENARIOS =================
    
    
    

    @Test
    void takeUserDetails_FeignClientReturnsCreated_Success() 
    {
        // Arrange
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(validEmailRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));
        assertEquals("User info received successfully", result.getBody().get("message"));

        verify(notificationClient, times(1)).buildNotificationContent(validEmailRequest);
    }

    @Test
    void takeUserDetails_FeignClientReturnsAccepted_Success() 
    {
        // Arrange
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.accepted().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(validPushRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));
        assertEquals("User info received successfully", result.getBody().get("message"));

        verify(notificationClient, times(1)).buildNotificationContent(validPushRequest);
    }

    
    @Test
    void takeUserDetails_FeignClientReturns500_ExceptionPropagated() 
    {
        // Arrange
        Request request = Request.create(Request.HttpMethod.POST, "/test", new HashMap<>(), null, new RequestTemplate());
        FeignException.InternalServerError feignException = new FeignException.InternalServerError(
                "Internal Server Error", request, null, null);
        
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenThrow(feignException);

        // Act & Assert
        assertThrows(FeignException.InternalServerError.class, () -> 
        {
            userDetailsService.takeUserDetails(validEmailRequest);
        });

        verify(notificationClient, times(1)).buildNotificationContent(validEmailRequest);
    }

    
    @Test
    void takeUserDetails_FeignClientTimeout_ExceptionPropagated() 
    {
        // Arrange
        Request request = Request.create(Request.HttpMethod.POST, "/test", new HashMap<>(), null, new RequestTemplate());
        FeignException.ServiceUnavailable serviceUnavailable = new FeignException.ServiceUnavailable(
                "Service Unavailable", request, null, null);
        
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenThrow(serviceUnavailable);

        // Act & Assert
        assertThrows(FeignException.ServiceUnavailable.class, () -> 
        {
            userDetailsService.takeUserDetails(validPushRequest);
        });

        verify(notificationClient, times(1)).buildNotificationContent(validPushRequest);
    }

    
    @Test
    void takeUserDetails_FeignClientReturnsBadRequest_ExceptionPropagated() 
    {
        // Arrange
        Request request = Request.create(Request.HttpMethod.POST, "/test", new HashMap<>(), null, new RequestTemplate());
        FeignException.BadRequest badRequest = new FeignException.BadRequest(
                "Bad Request", request, null, null);
        
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenThrow(badRequest);

        // Act & Assert
        assertThrows(FeignException.BadRequest.class, () -> 
        {
            userDetailsService.takeUserDetails(validEmailRequest);
        });

        verify(notificationClient, times(1)).buildNotificationContent(validEmailRequest);
    }

    
    @Test
    void takeUserDetails_FeignClientWithResponseBody_Success() 
    {
        // Arrange
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
            .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(validEmailRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));
        assertEquals("User info received successfully", result.getBody().get("message"));

        verify(notificationClient, times(1)).buildNotificationContent(validEmailRequest);
    }
    
    
    

    // ================= EDGE CASES FOR NOTIFICATION TYPES =================

    @Test
    void takeUserDetails_OnlyEmailType_WithSubject_Success() 
    {
        // Arrange
        NotificationRequest emailOnlyRequest = createEmailOnlyRequest();
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(emailOnlyRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));

        verify(notificationClient, times(1)).buildNotificationContent(emailOnlyRequest);
    }

    @Test
    void takeUserDetails_OnlyPushType_WithDeviceId_Success() 
    {
        // Arrange
        NotificationRequest pushOnlyRequest = createPushOnlyRequest();
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(pushOnlyRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));

        verify(notificationClient, times(1)).buildNotificationContent(pushOnlyRequest);
    }

    @Test
    void takeUserDetails_MultipleNotificationTypes_WithSMS_Success() 
    {
        // Arrange
        NotificationRequest multiTypeRequest = createMultiTypeRequest();
        when(notificationClient.buildNotificationContent(any(NotificationRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<Map<String, Object>> result = userDetailsService.takeUserDetails(multiTypeRequest);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));

        verify(notificationClient, times(1)).buildNotificationContent(multiTypeRequest);
    }

    // ================= HELPER METHODS =================

    
    private void setupValidEmailRequest() 
    {
        validEmailRequest = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithSubject();
        
        validEmailRequest.setUserInfo(userInfo);
        validEmailRequest.setNotificationBody(notificationBody);
    }

    private void setupValidPushRequest() 
    {
        validPushRequest = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithoutSubject();
        
        validPushRequest.setUserInfo(userInfo);
        validPushRequest.setNotificationBody(notificationBody);
    }

    private void setupInvalidEmailRequest() 
    {
        invalidEmailRequest = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithoutSubject();
        
        invalidEmailRequest.setUserInfo(userInfo);
        invalidEmailRequest.setNotificationBody(notificationBody);
    }

    
    private void setupInvalidPushRequest() 
    {
        invalidPushRequest = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        // Missing deviceId for PUSH type
        
        NotificationBody notificationBody = createNotificationBodyWithoutSubject();
        
        invalidPushRequest.setUserInfo(userInfo);
        invalidPushRequest.setNotificationBody(notificationBody);
    }

    
    private NotificationRequest createBothTypesValidRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL", "PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private NotificationRequest createBothTypesInvalidEmailRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL", "PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithoutSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private NotificationRequest createBothTypesInvalidPushRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL", "PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        // Missing deviceId for PUSH type
        
        NotificationBody notificationBody = createNotificationBodyWithSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private NotificationRequest createEmailOnlyRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private NotificationRequest createPushOnlyRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("PUSH"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithoutSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private NotificationRequest createMultiTypeRequest() 
    {
        NotificationRequest request = new NotificationRequest();
        
        UserInfo userInfo = createUserInfo();
        UserPreferences preferences = new UserPreferences();
        preferences.setNotificationType(List.of("EMAIL", "PUSH", "SMS"));
        preferences.setNotificationEnabled(true);
        userInfo.setPreferences(preferences);
        userInfo.setDeviceId("device-123");
        
        NotificationBody notificationBody = createNotificationBodyWithSubject();
        
        request.setUserInfo(userInfo);
        request.setNotificationBody(notificationBody);
        
        return request;
    }

    
    private UserInfo createUserInfo() 
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("user123");
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");
        return userInfo;
    }
    

    private NotificationBody createNotificationBodyWithSubject() 
    {
        NotificationBody notificationBody = new NotificationBody();
        notificationBody.setHeader("This is a test notification header");
        notificationBody.setSubject("This is a test subject for email notification validation");
        notificationBody.setMesssage("This is a test message content that meets the minimum character requirement for notification body validation rules and should be between 150 and 200 characters");
        return notificationBody;
    }

    
    private NotificationBody createNotificationBodyWithoutSubject() 
    {
        NotificationBody notificationBody = new NotificationBody();
        notificationBody.setHeader("This is a test notification header");
        // No subject set
        notificationBody.setMesssage("This is a test message content that meets the minimum character requirement for notification body validation rules and should be between 150 and 200 characters");
        return notificationBody;
    }
}



