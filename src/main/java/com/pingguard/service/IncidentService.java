package com.pingguard.service;

import com.pingguard.entity.Incident;
import com.pingguard.entity.Monitor;
import com.pingguard.entity.PingLog;
import com.pingguard.repository.IncidentRepository;
import com.pingguard.repository.PingLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final PingLogRepository pingLogRepository;
    private final AlertService alertService;

    public IncidentService(IncidentRepository incidentRepository, PingLogRepository pingLogRepository, AlertService alertService) {
        this.incidentRepository = incidentRepository;
        this.pingLogRepository = pingLogRepository;
        this.alertService = alertService;
    }

    @Transactional
    public void processIncidentLogic(Monitor monitor, PingLog latestPing) {
        Optional<Incident> openIncidentOpt = incidentRepository.findOpenIncidentByMonitorId(monitor.getId());

        if (latestPing.getIsUp()) {
            // If the monitor is UP, check if we need to resolve an open incident
            if (openIncidentOpt.isPresent()) {
                Incident incident = openIncidentOpt.get();
                incident.setResolvedAt(latestPing.getCheckedAt());
                
                long minutes = Duration.between(incident.getStartedAt(), incident.getResolvedAt()).toMinutes();
                incident.setDurationMins((int) minutes);
                
                incidentRepository.save(incident);
                
                // Trigger "Monitor is back UP" alert
                alertService.sendUpAlerts(incident);
            }
        } else {
            // If the monitor is DOWN, apply the 3-strike rule before opening an incident
            if (openIncidentOpt.isEmpty()) {
                // Fetch the last 3 pings
                List<PingLog> last3Pings = pingLogRepository.findByMonitorIdOrderByCheckedAtDesc(
                        monitor.getId(), PageRequest.of(0, 3));
                
                // Check if we have 3 pings and all of them are failures (isUp == false)
                if (last3Pings.size() == 3 && last3Pings.stream().noneMatch(PingLog::getIsUp)) {
                    Incident incident = new Incident();
                    incident.setMonitor(monitor);
                    // The incident actually started at the time of the *first* failed ping of the 3
                    incident.setStartedAt(last3Pings.get(2).getCheckedAt());
                    incident.setCause(latestPing.getErrorMessage());
                    incident.setAlertSent(true);
                    
                    incidentRepository.save(incident);
                    
                    // Trigger "Monitor is DOWN" alert
                    alertService.sendDownAlerts(incident);
                }
            }
        }
    }
}
