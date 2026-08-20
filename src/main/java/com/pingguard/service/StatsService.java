package com.pingguard.service;

import com.pingguard.dto.StatsResponse;
import com.pingguard.repository.PingLogRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatsService {

    private final PingLogRepository pingLogRepository;

    public StatsService(PingLogRepository pingLogRepository) {
        this.pingLogRepository = pingLogRepository;
    }

    @Cacheable(value = "stats", key = "#monitorId")
    public StatsResponse getMonitorStats(Long monitorId) {
        StatsResponse stats = new StatsResponse();
        LocalDateTime now = LocalDateTime.now();

        stats.setUptimePercentage24h(calculateUptime(monitorId, now.minusHours(24)));
        stats.setUptimePercentage7d(calculateUptime(monitorId, now.minusDays(7)));
        stats.setUptimePercentage30d(calculateUptime(monitorId, now.minusDays(30)));
        
        long total24h = pingLogRepository.countByMonitorIdAndCheckedAtAfter(monitorId, now.minusHours(24));
        long successful24h = pingLogRepository.countByMonitorIdAndIsUpAndCheckedAtAfter(monitorId, true, now.minusHours(24));
        
        stats.setTotalPings24h((int) total24h);
        stats.setFailedPings24h((int) (total24h - successful24h));

        return stats;
    }

    private Double calculateUptime(Long monitorId, LocalDateTime after) {
        long totalPings = pingLogRepository.countByMonitorIdAndCheckedAtAfter(monitorId, after);
        if (totalPings == 0) return 100.0; // Assume 100% if no data yet

        long successfulPings = pingLogRepository.countByMonitorIdAndIsUpAndCheckedAtAfter(monitorId, true, after);
        
        return Math.round(((double) successfulPings / totalPings) * 10000.0) / 100.0;
    }
}
