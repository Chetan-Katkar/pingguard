package com.pingguard.scheduler;

import com.pingguard.entity.Monitor;
import com.pingguard.entity.PingLog;
import com.pingguard.repository.MonitorRepository;
import com.pingguard.repository.PingLogRepository;
import com.pingguard.service.PingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class PingScheduler {

    private final MonitorRepository monitorRepository;
    private final PingService pingService;
    private final PingLogRepository pingLogRepository;
    private final com.pingguard.service.IncidentService incidentService;

    public PingScheduler(MonitorRepository monitorRepository, PingService pingService, 
                         PingLogRepository pingLogRepository, com.pingguard.service.IncidentService incidentService) {
        this.monitorRepository = monitorRepository;
        this.pingService = pingService;
        this.pingLogRepository = pingLogRepository;
        this.incidentService = incidentService;
    }

    // Runs every 10 seconds to check if any monitor is due
    @Scheduled(fixedRate = 10000)
    public void runPingChecks() {
        List<Monitor> dueMonitors = monitorRepository.findDueMonitors();
        
        if (dueMonitors.isEmpty()) {
            return;
        }

        System.out.println("Executing pings for " + dueMonitors.size() + " monitors...");

        // Fire off pings concurrently using the custom thread pool
        for (Monitor monitor : dueMonitors) {
            CompletableFuture.supplyAsync(() -> pingService.pingMonitor(monitor))
                    .thenAccept(this::processPingResult)
                    .exceptionally(ex -> {
                        System.err.println("Failed to process monitor " + monitor.getId() + ": " + ex.getMessage());
                        return null;
                    });
        }
    }

    @Transactional
    public void processPingResult(PingLog result) {
        // 1. Save the ping log
        pingLogRepository.save(result);

        // 2. Update the monitor's last checked time and current status
        Monitor monitor = result.getMonitor();
        monitor.setLastCheckedAt(result.getCheckedAt());
        monitor.setCurrentStatus(result.getIsUp() ? "UP" : "DOWN");
        monitorRepository.save(monitor);

        // 3. Process Incident Logic (3-strike rule)
        incidentService.processIncidentLogic(monitor, result);
    }
}
