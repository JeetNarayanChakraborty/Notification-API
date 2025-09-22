package com.microservice.notificationSend.repository;

import jakarta.persistence.*;


/** 
 * Entity class representing a dummy failed notification record.
 * This class is used to log failed notification attempts into the 'failed_notifications' table.
 */


@Entity
@Table(name = "failed_notifications")
public class FailedNotificationDummy 
{
    @Id
    private String id;

    public FailedNotificationDummy() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}