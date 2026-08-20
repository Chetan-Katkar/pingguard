package com.pingguard.dto;

import com.pingguard.entity.Monitor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorResponse {
    private Long id;
    private String name;
    private String url;
    private Integer checkInterval;
    private String currentStatus;
    private Boolean isActive;
    private LocalDateTime lastCheckedAt;

    public static MonitorResponse fromEntity(Monitor monitor) {
        MonitorResponse response = new MonitorResponse();
        response.setId(monitor.getId());
        response.setName(monitor.getName());
        response.setUrl(monitor.getUrl());
        response.setCheckInterval(monitor.getCheckInterval());
        response.setCurrentStatus(monitor.getCurrentStatus());
        response.setIsActive(monitor.getIsActive());
        response.setLastCheckedAt(monitor.getLastCheckedAt());
        return response;
    }
}
