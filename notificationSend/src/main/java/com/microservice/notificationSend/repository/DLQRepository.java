package com.microservice.notificationSend.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;



@Repository
public interface DLQRepository extends JpaRepository<FailedNotificationDummy, String>
{
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO `failed_notifications` "
	            + "(`id`, `channel_type`, `recipient`, `original_payload`, `attempt_count`, `last_attempt`, `failure_reason`, `failed_at`, `status`)"
	            + "VALUES (:id, :channelType, :recipient, :originalPayload, :attemptCount, :lastAttempt, :failureReason, :failedAt, :status)", nativeQuery = true)
	public void insertData(@Param("id") String id, 
	                      @Param("channelType") String channelType, 
	                      @Param("recipient") String recipient, // Keep as String but pass JSON string
	                      @Param("originalPayload") String originalPayload, 
	                      @Param("attemptCount") int attemptCount, 
	                      @Param("lastAttempt") LocalDateTime lastAttempt, 
	                      @Param("failureReason") String failureReason, 
	                      @Param("failedAt") LocalDateTime failedAt, 
	                      @Param("status") String status);
}





