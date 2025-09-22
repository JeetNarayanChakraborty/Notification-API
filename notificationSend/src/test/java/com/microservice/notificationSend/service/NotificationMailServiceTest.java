package com.microservice.notificationSend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.dto.input_dto.UserInputDTO.NotificationBody;
import com.dto.input_dto.UserInputDTO.NotificationRequest;
import com.dto.input_dto.UserInputDTO.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.notificationSend.repository.DLQRepository;



@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationMailService Comprehensive Tests")
class NotificationMailServiceTest 
{
    @Mock
    private DLQRepository dlqRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationMailService notificationMailService;

    private NotificationRequest notificationRequest;
    private UserInfo userInfo;
    private NotificationBody notificationBody;
    private String htmlString;
    private String apiKey = "test-api-key";

    
    @BeforeEach
    void setUp() throws Exception 
    {
        userInfo = new UserInfo();
        userInfo.setUsername("Test User");
        userInfo.setEmail("test@example.com");

        notificationBody = new NotificationBody();
        notificationBody.setSubject("Test Subject");

        notificationRequest = new NotificationRequest();
        notificationRequest.setUserInfo(userInfo);
        notificationRequest.setNotificationBody(notificationBody);

        htmlString = "<html><body><h1>Test Email</h1></body></html>";

        ReflectionTestUtils.setField(notificationMailService, "apiKey", apiKey);
        ReflectionTestUtils.setField(notificationMailService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(notificationMailService, "objectMapper", objectMapper); // Ensure objectMapper is set

        lenient().when(objectMapper.writeValueAsString(any(NotificationRequest.class)))
            .thenReturn("{\"test\":\"payload\"}");

        lenient().doNothing().when(dlqRepository).insertData(
            anyString(), anyString(), anyString(), anyString(),
            anyInt(), any(LocalDateTime.class), anyString(),
            any(LocalDateTime.class), anyString());
    }


    @Test
    @DisplayName("Should send email successfully")
    void testSendMail_Success() 
    {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("messageId", "msg-12345");
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
            .thenReturn(mockResponse);

        List<String> result = notificationMailService.sendMail(notificationRequest, htmlString);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("Message :msg-12345"));
        assertTrue(result.get(1).contains("Response :"));

        verify(restTemplate, times(1)).postForEntity(
            eq("https://api.brevo.com/v3/smtp/email"),
            any(HttpEntity.class),
            any());
    }


    @Test
    @DisplayName("Should handle null HTML string")
    void testSendMail_NullHTMLString() 
    {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("messageId", "msg-12345");
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
            .thenReturn(mockResponse);

        List<String> result = notificationMailService.sendMail(notificationRequest, null);

        assertNotNull(result);
        assertEquals(2, result.size());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(anyString(), entityCaptor.capture(), any());

        @SuppressWarnings("unchecked")
        Map<String, Object> capturedPayload = (Map<String, Object>) entityCaptor.getValue().getBody();
        assertNull(capturedPayload.get("htmlContent"));
    }

    
    @Test
    @DisplayName("Should handle empty HTML string")
    void testSendMail_EmptyHTMLString() 
    {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("messageId", "msg-12345");
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
            .thenReturn(mockResponse);

        List<String> result = notificationMailService.sendMail(notificationRequest, "");

        assertNotNull(result);
        assertEquals(2, result.size());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(anyString(), entityCaptor.capture(), any());

        @SuppressWarnings("unchecked")
        Map<String, Object> capturedPayload = (Map<String, Object>) entityCaptor.getValue().getBody();
        assertEquals("", capturedPayload.get("htmlContent"));
    }

    
    @Test
    @DisplayName("Should handle response without messageId")
    void testSendMail_ResponseWithoutMessageId() 
    {
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
            .thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        {
            notificationMailService.sendMail(notificationRequest, htmlString);
        });

        assertEquals("Notification mail failed to send", exception.getMessage());
        assertTrue(exception.getCause() instanceof NullPointerException);
    }

    
    @Test
    @DisplayName("Should verify correct headers")
    void testSendMail_CorrectHeaders() 
    {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("messageId", "msg-12345");
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
            .thenReturn(mockResponse);

        notificationMailService.sendMail(notificationRequest, htmlString);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(anyString(), entityCaptor.capture(), any());

        HttpEntity<Map<String, Object>> capturedEntity = entityCaptor.getValue();
        assertEquals(apiKey, capturedEntity.getHeaders().getFirst("api-key"));
        assertTrue(capturedEntity.getHeaders().getContentType().toString().contains("application/json"));
    }
}





