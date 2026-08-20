package com.pingguard.alert;

import com.pingguard.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class WebhookAlertStrategy implements AlertStrategy {

    // For now, this is a placeholder. Webhook logic can be added later.
    // In Week 3, this would use WebClient to POST a JSON payload to a user-defined URL.

    @Override
    public void sendDownAlert(Incident incident) {
        System.out.println("Webhook: Monitor DOWN -> " + incident.getMonitor().getName());
    }

    @Override
    public void sendUpAlert(Incident incident) {
        System.out.println("Webhook: Monitor UP -> " + incident.getMonitor().getName());
    }
}
