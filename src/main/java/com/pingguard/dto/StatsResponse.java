package com.pingguard.dto;

import lombok.Data;

@Data
public class StatsResponse {
    private Double uptimePercentage24h;
    private Double uptimePercentage7d;
    private Double uptimePercentage30d;
    
    private Integer totalPings24h;
    private Integer failedPings24h;
}
