package com.pingguard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

@Data
public class MonitorRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "URL is required")
    @URL(message = "Must be a valid URL")
    private String url;

    @Min(value = 30, message = "Check interval must be at least 30 seconds")
    @Max(value = 86400, message = "Check interval must be less than 24 hours")
    private Integer checkInterval = 60;

    private Integer expectedStatus = 200;

    private String method = "GET";

    @Min(value = 1000, message = "Timeout must be at least 1 second")
    @Max(value = 30000, message = "Timeout must be at most 30 seconds")
    private Integer timeoutMs = 10000;
}
