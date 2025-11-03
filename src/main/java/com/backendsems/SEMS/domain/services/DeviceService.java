package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;
import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.infrastructure.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    
    public List<Device> getAllDevicesByUserId(Long userId) {
        List<DeviceAggregate> aggregates = deviceRepository.findByUserId(userId);
        return aggregates.stream()
                .map(aggregate -> Device.fromAggregate(aggregate, null)) // null como user temporal
                .collect(Collectors.toList());
    }
    
    public List<Device> getActiveDevicesByUserId(Long userId) {
        List<DeviceAggregate> aggregates = deviceRepository.findByUserId(userId);
        return aggregates.stream()
                .filter(DeviceAggregate::isActive)
                .map(aggregate -> Device.fromAggregate(aggregate, null))
                .collect(Collectors.toList());
    }
    
    public Optional<Device> getDeviceById(Long deviceId) {
        Optional<DeviceAggregate> aggregate = deviceRepository.findById(deviceId);
        return aggregate.map(dev -> Device.fromAggregate(dev, null));
    }
    
    public Device createDevice(Long userId, Device device) {
        UserAggregate userAggregate = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        DeviceAggregate deviceAggregate = device.toAggregate();
        deviceAggregate.setUser(userAggregate);
        DeviceAggregate savedAggregate = deviceRepository.save(deviceAggregate);
        return Device.fromAggregate(savedAggregate, null);
    }
    
    public Device updateDevice(Long deviceId, Device deviceUpdate) {
        DeviceAggregate existingAggregate = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        
        // Actualizar campos básicos del agregado
        existingAggregate.setName(deviceUpdate.getName());
        existingAggregate.setType(DeviceAggregate.DeviceType.valueOf(deviceUpdate.getType().name()));
        
        DeviceAggregate savedAggregate = deviceRepository.save(existingAggregate);
        return Device.fromAggregate(savedAggregate, null);
    }
    
    public void deleteDevice(Long deviceId) {
        deviceRepository.deleteById(deviceId);
    }
    
    public List<Device> getDevicesByCategory(Long userId, String category) {
        List<DeviceAggregate> aggregates = deviceRepository.findByUserId(userId);
        return aggregates.stream()
                .map(aggregate -> Device.fromAggregate(aggregate, null))
                .filter(device -> category.equals(device.getCategory()))
                .collect(Collectors.toList());
    }
    
    public Device toggleDeviceStatus(Long deviceId) {
        DeviceAggregate deviceAggregate = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        
        // Usar métodos del dominio
        if (deviceAggregate.isActive()) {
            deviceAggregate.turnOff();
        } else {
            deviceAggregate.turnOn();
        }
        
        DeviceAggregate savedAggregate = deviceRepository.save(deviceAggregate);
        return Device.fromAggregate(savedAggregate, null);
    }
}