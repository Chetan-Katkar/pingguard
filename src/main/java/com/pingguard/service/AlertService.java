package com.pingguard.service;

import com.pingguard.alert.AlertStrategy;
import com.pingguard.entity.Incident;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final List<AlertStrategy> alertStrategies;

    public AlertService(List<AlertStrategy> alertStrategies) {
        this.alertStrategies = alertStrategies;
    }

    @Async
    public void sendDownAlerts(Incident incident) {
        System.out.println("Dispatching DOWN alerts for incident " + incident.getId());
        for (AlertStrategy strategy : alertStrategies) {
            strategy.sendDownAlert(incident);
        }
    }

    @Async
    public void sendUpAlerts(Incident incident) {
        System.out.println("Dispatching UP alerts for incident " + incident.getId());
        for (AlertStrategy strategy : alertStrategies) {
            strategy.sendUpAlert(incident);
        }
    }
}
