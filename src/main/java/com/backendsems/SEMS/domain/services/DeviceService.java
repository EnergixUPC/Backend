package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.interfaces.rest.dto.CreateDeviceDto;
import com.backendsems.SEMS.infrastructure.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    
    public List<Device> getAllDevicesByUserId(Long userId) {
        return deviceRepository.findByUserId(userId);
    }
    
    public List<Device> getActiveDevicesByUserId(Long userId) {
        return deviceRepository.findByUserIdAndIsActive(userId);
    }
    
    public Optional<Device> getDeviceById(Long deviceId) {
        return deviceRepository.findById(deviceId);
    }
    
    public Device createDevice(Long userId, CreateDeviceDto deviceDto) {
        // Verificar que el usuario existe
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Device.DeviceType deviceType = Device.DeviceType.fromString(deviceDto.getType());
        Device.DeviceStatus deviceStatus = Device.DeviceStatus.OFF;
        
        // Si el status viene en el DTO, usarlo
        if (deviceDto.getStatus() != null && !deviceDto.getStatus().trim().isEmpty()) {
            try {
                deviceStatus = Device.DeviceStatus.valueOf(deviceDto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                deviceStatus = Device.DeviceStatus.OFF;
            }
        }
        
        Device device = Device.builder()
                .name(deviceDto.getName())
                .category(deviceDto.getCategory() != null ? deviceDto.getCategory() : deviceType.getDisplayName())
                .type(deviceType)
                .location(deviceDto.getLocation())
                .brand(deviceDto.getBrand())
                .model(deviceDto.getModel())
                .status(deviceStatus)
                .userId(userId)
                .isActive(deviceStatus == Device.DeviceStatus.ON)
                .realTimeStatus(deviceStatus.getDisplayName())
                .lastActive(deviceStatus == Device.DeviceStatus.ON ? "Now" : "Never")
                .alertHistory("No alerts")
                .energyConsumption("0 kWh this week")
                .consumptionKwh(0.0)
                .efficiencyRating(85)
                .build();
        
        // Establecer la categoría basada en el tipo si no se proporciona
        if (device.getCategory() == null || device.getCategory().equals(deviceType.name())) {
            device.setCategory(device.getCategoryFromType());
        }
        
        Device savedDevice = deviceRepository.save(device);
        
        // Actualizar contador de dispositivos activos del usuario
        if (savedDevice.getIsActive()) {
            user.setActiveDevicesCount(user.getActiveDevicesCount() + 1);
            userRepository.save(user);
        }
        
        return savedDevice;
    }
    
    public Device updateDevice(Long deviceId, Device deviceUpdate) {
        Device existingDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        // Actualizar campos
        if (deviceUpdate.getName() != null) {
            existingDevice.setName(deviceUpdate.getName());
        }
        if (deviceUpdate.getType() != null) {
            existingDevice.setType(deviceUpdate.getType());
            existingDevice.setCategory(existingDevice.getCategoryFromType());
        }
        if (deviceUpdate.getLocation() != null) {
            existingDevice.setLocation(deviceUpdate.getLocation());
        }
        if (deviceUpdate.getBrand() != null) {
            existingDevice.setBrand(deviceUpdate.getBrand());
        }
        if (deviceUpdate.getModel() != null) {
            existingDevice.setModel(deviceUpdate.getModel());
        }
        
        return deviceRepository.save(existingDevice);
    }
    
    public void deleteDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        // Actualizar contador si el dispositivo estaba activo
        if (device.getIsActive()) {
            var user = userRepository.findById(device.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setActiveDevicesCount(Math.max(0, user.getActiveDevicesCount() - 1));
            userRepository.save(user);
        }
        
        deviceRepository.deleteById(deviceId);
    }
    
    public List<Device> getDevicesByCategory(Long userId, String category) {
        return deviceRepository.findByUserIdAndCategory(userId, category);
    }
    
    public Device toggleDeviceStatus(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        boolean wasActive = device.getIsActive();
        
        if (device.getIsActive()) {
            device.turnOff();
        } else {
            device.turnOn();
        }
        
        Device savedDevice = deviceRepository.save(device);
        
        // Actualizar contador del usuario si cambió el estado activo
        if (wasActive != savedDevice.getIsActive()) {
            var user = userRepository.findById(device.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (savedDevice.getIsActive()) {
                user.setActiveDevicesCount(user.getActiveDevicesCount() + 1);
            } else {
                user.setActiveDevicesCount(Math.max(0, user.getActiveDevicesCount() - 1));
            }
            userRepository.save(user);
        }
        
        return savedDevice;
    }
}