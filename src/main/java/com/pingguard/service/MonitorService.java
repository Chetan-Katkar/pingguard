package com.pingguard.service;

import com.pingguard.dto.MonitorRequest;
import com.pingguard.dto.MonitorResponse;
import com.pingguard.entity.Monitor;
import com.pingguard.entity.User;
import com.pingguard.exception.MonitorLimitExceededException;
import com.pingguard.exception.ResourceNotFoundException;
import com.pingguard.repository.MonitorRepository;
import com.pingguard.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;

    public MonitorService(MonitorRepository monitorRepository, UserRepository userRepository) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @CacheEvict(value = "monitors", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public MonitorResponse createMonitor(MonitorRequest request) {
        User user = getCurrentUser();

        // Enforce limits
        long currentMonitors = monitorRepository.countByUserId(user.getId());
        if ("FREE".equals(user.getPlan()) && currentMonitors >= 5) {
            throw new MonitorLimitExceededException("Free plan allows a maximum of 5 monitors. Please upgrade.");
        }

        Monitor monitor = new Monitor();
        monitor.setUser(user);
        monitor.setName(request.getName());
        monitor.setUrl(request.getUrl());
        monitor.setCheckInterval(request.getCheckInterval());
        monitor.setExpectedStatus(request.getExpectedStatus());
        monitor.setMethod(request.getMethod());
        monitor.setTimeoutMs(request.getTimeoutMs());
        
        Monitor saved = monitorRepository.save(monitor);
        return MonitorResponse.fromEntity(saved);
    }

    @Cacheable(value = "monitors", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public List<MonitorResponse> getAllMonitors() {
        User user = getCurrentUser();
        return monitorRepository.findAllByUserId(user.getId())
                .stream()
                .map(MonitorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MonitorResponse getMonitorById(Long id) {
        User user = getCurrentUser();
        Monitor monitor = monitorRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found"));
        return MonitorResponse.fromEntity(monitor);
    }

    @CacheEvict(value = "monitors", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public MonitorResponse updateMonitor(Long id, MonitorRequest request) {
        User user = getCurrentUser();
        Monitor monitor = monitorRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found"));

        monitor.setName(request.getName());
        monitor.setUrl(request.getUrl());
        monitor.setCheckInterval(request.getCheckInterval());
        monitor.setExpectedStatus(request.getExpectedStatus());
        monitor.setMethod(request.getMethod());
        monitor.setTimeoutMs(request.getTimeoutMs());

        Monitor updated = monitorRepository.save(monitor);
        return MonitorResponse.fromEntity(updated);
    }

    @CacheEvict(value = "monitors", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteMonitor(Long id) {
        User user = getCurrentUser();
        Monitor monitor = monitorRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found"));
        
        monitorRepository.delete(monitor);
    }
}
