package com.pingguard.alert;

import com.pingguard.entity.Incident;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailAlertStrategy implements AlertStrategy {

    private final JavaMailSender mailSender;

    public EmailAlertStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendDownAlert(Incident incident) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(incident.getMonitor().getUser().getEmail());
        message.setSubject("🚨 UPTIME ALERT: " + incident.getMonitor().getName() + " is DOWN");
        message.setText("Your monitor " + incident.getMonitor().getName() + 
                " (" + incident.getMonitor().getUrl() + ") is currently DOWN.\n\n" +
                "Cause: " + incident.getCause() + "\n" +
                "Started At: " + incident.getStartedAt());
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send DOWN email alert: " + e.getMessage());
        }
    }

    @Override
    public void sendUpAlert(Incident incident) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(incident.getMonitor().getUser().getEmail());
        message.setSubject("✅ RECOVERY ALERT: " + incident.getMonitor().getName() + " is BACK UP");
        message.setText("Your monitor " + incident.getMonitor().getName() + 
                " (" + incident.getMonitor().getUrl() + ") has recovered.\n\n" +
                "Downtime Duration: " + incident.getDurationMins() + " minutes.");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send UP email alert: " + e.getMessage());
        }
    }
}
