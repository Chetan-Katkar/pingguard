package com.pingguard.service;

import com.pingguard.entity.Monitor;
import com.pingguard.entity.PingLog;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Service
public class PingService {

    private final WebClient webClient;

    public PingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public PingLog pingMonitor(Monitor monitor) {
        long startTime = System.currentTimeMillis();
        PingLog pingLog = new PingLog();
        pingLog.setMonitor(monitor);
        pingLog.setCheckedAt(LocalDateTime.now());

        try {
            HttpMethod method = HttpMethod.valueOf(monitor.getMethod().toUpperCase());

            var responseSpec = webClient.method(method)
                    .uri(monitor.getUrl())
                    .retrieve()
                    // By default, WebClient throws exceptions for 4xx/5xx errors.
                    // We want to capture the status code instead of throwing.
                    .onStatus(status -> true, response -> Mono.empty())
                    .toBodilessEntity()
                    .timeout(Duration.ofMillis(monitor.getTimeoutMs()))
                    .block(); // block() because PingScheduler will call this asynchronously per monitor

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            pingLog.setResponseTimeMs((int) responseTime);
            
            if (responseSpec != null) {
                int statusCode = responseSpec.getStatusCode().value();
                pingLog.setStatusCode(statusCode);
                
                if (statusCode == monitor.getExpectedStatus()) {
                    pingLog.setIsUp(true);
                } else {
                    pingLog.setIsUp(false);
                    pingLog.setErrorMessage("Status " + statusCode + " did not match expected " + monitor.getExpectedStatus());
                }
            } else {
                pingLog.setIsUp(false);
                pingLog.setErrorMessage("No response received");
            }

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            pingLog.setResponseTimeMs((int) (endTime - startTime));
            pingLog.setIsUp(false);
            
            if (e.getCause() instanceof TimeoutException) {
                pingLog.setErrorMessage("Connection timed out after " + monitor.getTimeoutMs() + "ms");
            } else {
                pingLog.setErrorMessage(e.getMessage());
            }
        }

        return pingLog;
    }
}
