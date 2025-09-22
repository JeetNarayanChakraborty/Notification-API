package com.dto.input_dto.UserInputDTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



/* 
 * NotificationBody class represents the body of a notification message.
 * It includes fields for the header, subject, and message content.
 * Validation constraints are applied to ensure data integrity.
 * 
 * Validation Rules:
 * 
 * - header: Must be between 30 and 50 characters long and cannot be null.
 * - subject: Optional field, must be between 60 and 80 characters long if provided.
 * - message: Must be between 150 and 200 characters long and cannot be null.
 */




public class NotificationBody 
{
	@Size(min=30, max=50, message="Subject must be between 30 and 50 characters long")
	@NotNull(message="Header cannot be null")
	String header;
	
	@Size(min=60, max=80, message ="Subject must be between 60 and 80 characters long")
	@Nullable
	String subject;
	
	@Size(min=150, max=200, message="Subject must be between 150 and 200 characters long")
	@NotNull(message="Message cannot be null")
	String messsage;
	
	
	
	public NotificationBody() {}
	
	
	public NotificationBody(String header, String subject, String messsage) 
	{
		this.header = header;
		this.subject = subject;
		this.messsage = messsage;
	}
	
	public String getHeader() 
	{
		return header;
	}

	public void setHeader(String header) 
	{
		this.header = header;
	}

	public String getSubject() 
	{
		return subject;
	}

	public void setSubject(String subject) 
	{
		this.subject = subject;
	}

	public String getMesssage() 
	{
		return messsage;
	}

	public void setMesssage(String messsage) 
	{
		this.messsage = messsage;
	}
}







