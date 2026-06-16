package com.backendsems.SEMS.domain.model.events;

public class ConsumptionRecordedEvent {
    private final String deviceId;
    private final Double consumption;
    private final String timestamp;

    public ConsumptionRecordedEvent(String deviceId, Double consumption, String timestamp) {
        this.deviceId = deviceId;
        this.consumption = consumption;
        this.timestamp = timestamp;
    }

    public String getDeviceId() { return deviceId; }
    public Double getConsumption() { return consumption; }
    public String getTimestamp() { return timestamp; }
}

