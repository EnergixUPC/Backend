package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.Alert;
import com.backendsems.SEMS.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repositorio Spring Data JPA para la entidad Alert.
 *
 * Proporciona operaciones CRUD heredadas de JpaRepository y consultas derivadas usadas
 * por el servicio de notificaciones:
 *  - findByUser(User user): devuelve las alertas asociadas a una instancia User.
 *  - findByUserId(Long userId): devuelve las alertas de un usuario por su id.
 *  - findByUserIdOrderByTimestampDesc(Long userId): devuelve alertas del usuario ordenadas por timestamp (desc).
 *  - findByUserIdAndIsRead(Long userId, Boolean isRead): filtra alertas por estado leído/no leído.
 *  - countByUserIdAndIsRead(Long userId, Boolean isRead): cuenta las alertas según el estado leído.
 *
 * Requiere que Alert tenga los campos esperados: user (relación), timestamp e isRead.
 */

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUser(User user);
    List<Alert> findByUserId(Long userId);
    List<Alert> findByUserIdOrderByTimestampDesc(Long userId);
    List<Alert> findByUserIdAndIsRead(Long userId, Boolean isRead);
    Long countByUserIdAndIsRead(Long userId, Boolean isRead);
}