package com.pingguard.repository;

import com.pingguard.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("SELECT i FROM Incident i WHERE i.monitor.id = :monitorId AND i.resolvedAt IS NULL")
    Optional<Incident> findOpenIncidentByMonitorId(Long monitorId);

    List<Incident> findByMonitorIdOrderByStartedAtDesc(Long monitorId);
}
