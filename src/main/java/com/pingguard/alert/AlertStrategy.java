package com.pingguard.alert;

import com.pingguard.entity.Incident;

public interface AlertStrategy {
    void sendDownAlert(Incident incident);
    void sendUpAlert(Incident incident);
}
