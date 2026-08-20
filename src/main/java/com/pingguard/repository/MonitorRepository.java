package com.pingguard.repository;

import com.pingguard.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    List<Monitor> findAllByUserId(Long userId);

    Optional<Monitor> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
    
    // For the PingScheduler to find active monitors that are due for a check
    // Native query because JPQL doesn't have a clean way to do date math across all DBs easily
    @org.springframework.data.jpa.repository.Query(
            value = "SELECT * FROM monitors m WHERE m.is_active = true " +
                    "AND (m.last_checked_at IS NULL OR " +
                    "EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - m.last_checked_at)) >= m.check_interval)",
            nativeQuery = true)
    List<Monitor> findDueMonitors();
}
