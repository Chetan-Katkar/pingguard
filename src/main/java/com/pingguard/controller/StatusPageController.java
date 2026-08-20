package com.pingguard.controller;

import com.pingguard.entity.Monitor;
import com.pingguard.repository.MonitorRepository;
import com.pingguard.service.StatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StatusPageController {

    private final MonitorRepository monitorRepository;
    private final StatsService statsService;

    public StatusPageController(MonitorRepository monitorRepository, StatsService statsService) {
        this.monitorRepository = monitorRepository;
        this.statsService = statsService;
    }

    @GetMapping("/status/{monitorId}")
    public String getStatusPage(@PathVariable Long monitorId, Model model) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid monitor Id:" + monitorId));
        
        model.addAttribute("monitor", monitor);
        model.addAttribute("stats", statsService.getMonitorStats(monitorId));
        
        return "status-page";
    }
}
