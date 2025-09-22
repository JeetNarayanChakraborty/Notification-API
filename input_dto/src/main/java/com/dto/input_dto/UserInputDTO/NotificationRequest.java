package com.dto.input_dto.UserInputDTO;





/* This class is the main payload class for sending notification requests.
 * It contains user information, notification body, and an optional HTML string.
 * This class is used to encapsulate all necessary data for sending notifications.
 */



public class NotificationRequest 
{
	private UserInfo userInfo;
	private NotificationBody notificationBody;
	private String HTMLString;
	
	
	public NotificationRequest() {}
	
	public UserInfo getUserInfo() 
	{
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) 
	{
		this.userInfo = userInfo;
	}

	public NotificationBody getNotificationBody() 
	{
		return notificationBody;
	}

	public void setNotificationBody(NotificationBody notificationBody) 
	{
		this.notificationBody = notificationBody;
	}

	public String getHTMLString() 
	{
		return HTMLString;
	}

	public void setHTMLString(String hTMLString) 
	{
		HTMLString = hTMLString;
	}
}







