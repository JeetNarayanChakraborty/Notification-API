package com.dto.input_dto.UserInputDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.lang.Nullable;


/* 
 * UserInfo class represents user information with validation constraints.
 * It includes fields for userId, username, email, deviceId, and preferences.
 * 
 * Validation Rules:
 * 
 * - userId: Must not be null.
 * - username: Must not be blank.
 * - email: Must not be blank and must follow a valid email format.
 * - deviceId: Optional field, must match the specified pattern if provided.
 * - preferences: Must not be null.
 * 
 */




public class UserInfo 
{
	@NotNull(message = "UserId is required")
	private String userId;
	
	@NotBlank(message = "Username is required")
    private String username;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Invalid device ID")
    private String deviceId;

    @NotNull(message = "Preferences must not be null")
    private UserPreferences preferences;
    
    
    
    public UserInfo() {}
    
    UserInfo(String username, String deviceId, UserPreferences preferences)
	{
		this.username = username;
		this.deviceId = deviceId;
		this.preferences = preferences;
	}
    
    public String getEmail() 
	{
		return email;
	}
    	
    public void setEmail(String email) 
	{
		this.email = email;
	}
    
    public String getUserId() 
	{
		return userId;
	}
    
    public void setUserId(String userId) 
	{
		this.userId = userId;
	}

	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	public String getDeviceId() 
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId) 
	{
		this.deviceId = deviceId;
	}

	public UserPreferences getPreferences() 
	{
		return preferences;
	}

	public void setPreferences(UserPreferences preferences) 
	{
		this.preferences = preferences;
	}
}











