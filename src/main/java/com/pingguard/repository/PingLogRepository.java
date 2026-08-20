package com.pingguard.repository;

import com.pingguard.entity.PingLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PingLogRepository extends JpaRepository<PingLog, Long> {

    List<PingLog> findByMonitorIdOrderByCheckedAtDesc(Long monitorId, Pageable pageable);

    long countByMonitorIdAndIsUpAndCheckedAtAfter(Long monitorId, Boolean isUp, LocalDateTime after);

    long countByMonitorIdAndCheckedAtAfter(Long monitorId, LocalDateTime after);
}
