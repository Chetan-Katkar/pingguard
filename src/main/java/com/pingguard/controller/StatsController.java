package com.pingguard.controller;

import com.pingguard.dto.StatsResponse;
import com.pingguard.entity.Incident;
import com.pingguard.repository.IncidentRepository;
import com.pingguard.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitors/{monitorId}/stats")
public class StatsController {

    private final StatsService statsService;
    private final IncidentRepository incidentRepository;

    public StatsController(StatsService statsService, IncidentRepository incidentRepository) {
        this.statsService = statsService;
        this.incidentRepository = incidentRepository;
    }

    @GetMapping
    public ResponseEntity<StatsResponse> getMonitorStats(@PathVariable Long monitorId) {
        // NOTE: In a real app we should verify the monitor belongs to the current user
        return ResponseEntity.ok(statsService.getMonitorStats(monitorId));
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getMonitorIncidents(@PathVariable Long monitorId) {
        // NOTE: In a real app we should verify the monitor belongs to the current user
        return ResponseEntity.ok(incidentRepository.findByMonitorIdOrderByStartedAtDesc(monitorId));
    }
}
