package com.dto.input_dto.UserInputDTO;

import java.util.List;
import jakarta.validation.constraints.NotNull;


/*
 * UserPreferences class represents user preferences with validation constraints.
 * It includes fields for notification type and notification enabled status.
 * 
 * Validation Rules:
 * 
 * - notification type: must not be null.
 * - notification enabled: must not be null
 */



public class UserPreferences 
{
	@NotNull(message = "NotificationType is required")
	private List<String> notificationType;
	
	@NotNull(message = "NotificationEnabled is required")
	private Boolean notificationEnabled;
	
	
	
	public UserPreferences() {}
	
	UserPreferences(List<String> notificationType, Boolean notificationEnabled)
	{
		this.notificationType = notificationType;
		this.notificationEnabled = notificationEnabled;
	}

	public List<String> getNotificationType() 
	{
		return notificationType;
	}


	public void setNotificationType(List<String> notificationType) 
	{
		this.notificationType = notificationType;
	}


	public Boolean getNotificationEnabled() 
	{
		return notificationEnabled;
	}


	public void setNotificationEnabled(Boolean notificationEnabled) 
	{
		this.notificationEnabled = notificationEnabled;
	}
}















