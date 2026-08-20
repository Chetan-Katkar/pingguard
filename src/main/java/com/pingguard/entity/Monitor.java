package com.pingguard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "monitors")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer checkInterval = 60; // in seconds

    @Column(nullable = false)
    private Integer expectedStatus = 200;

    @Column(nullable = false)
    private String method = "GET";

    @Column(nullable = false)
    private Integer timeoutMs = 10000;

    @Column(nullable = false)
    private String currentStatus = "PENDING"; // UP, DOWN, PENDING, PAUSED

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime lastCheckedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
