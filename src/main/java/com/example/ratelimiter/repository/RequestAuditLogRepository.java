package com.example.ratelimiter.repository;

import com.example.ratelimiter.entity.RequestAuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestAuditLogRepository extends JpaRepository<RequestAuditLog, Long> {

    long countByAllowed(boolean allowed);

    @Query("SELECT r.clientId, COUNT(r) FROM RequestAuditLog r GROUP BY r.clientId ORDER BY COUNT(r) DESC")
    List<Object[]> findTopClients();

    @Query("SELECT r.endpoint, COUNT(r) FROM RequestAuditLog r GROUP BY r.endpoint ORDER BY COUNT(r) DESC")
    List<Object[]> findTopEndpoints();
}
