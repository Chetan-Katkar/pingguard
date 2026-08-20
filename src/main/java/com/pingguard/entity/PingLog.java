package com.pingguard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ping_logs", indexes = {
        @Index(name = "idx_pinglog_monitor_time", columnList = "monitor_id, checked_at")
})
public class PingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    private Integer statusCode;

    private Integer responseTimeMs;

    @Column(nullable = false)
    private Boolean isUp;

    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    @PrePersist
    protected void onCreate() {
        if (this.checkedAt == null) {
            this.checkedAt = LocalDateTime.now();
        }
    }
}
