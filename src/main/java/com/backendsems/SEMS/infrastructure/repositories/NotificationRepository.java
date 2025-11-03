package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.aggregates.NotificationAggregate;
import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NotificationRepository
 * Repositorio para el agregado de Notificación
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationAggregate, Long> {
    
    List<NotificationAggregate> findByUser(UserAggregate user);
    
    @Query("SELECT n FROM NotificationAggregate n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<NotificationAggregate> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT n FROM NotificationAggregate n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<NotificationAggregate> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
    
    @Query("SELECT n FROM NotificationAggregate n WHERE n.user.id = :userId AND n.status = 'UNREAD'")
    List<NotificationAggregate> findUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT n FROM NotificationAggregate n WHERE n.user.id = :userId AND n.status = :status")
    List<NotificationAggregate> findByUserIdAndIsRead(@Param("userId") Long userId, @Param("status") String status);
    
    @Query("SELECT COUNT(n) FROM NotificationAggregate n WHERE n.user.id = :userId AND n.status = 'UNREAD'")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM NotificationAggregate n WHERE n.user.id = :userId AND n.status = :status")
    Long countByUserIdAndIsRead(@Param("userId") Long userId, @Param("status") String status);
}