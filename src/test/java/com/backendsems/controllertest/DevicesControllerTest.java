package com.backendsems.controllertest;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.commands.DeleteDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.UpdateDeviceCommand;
import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.queries.GetAllDevicesQuery;
import com.backendsems.SEMS.domain.model.queries.GetDeviceByIdQuery;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceCategory;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceLocation;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceName;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceStatus;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DeviceCommandService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.interfaces.rest.DevicesController;
import com.backendsems.SEMS.interfaces.rest.resources.CreateDeviceResource;
import com.backendsems.SEMS.interfaces.rest.resources.DeviceResource;
import com.backendsems.SEMS.interfaces.rest.resources.UpdateDeviceResource;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class DevicesControllerTest {

    @Mock
    private DeviceCommandService deviceCommandService;

    @Mock
    private DeviceQueryService deviceQueryService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ProfilesContextFacade profilesContextFacade;

    @InjectMocks
    private DevicesController devicesController;

    @Test
    void createDevice_Success() {
        CreateDeviceResource resource = new CreateDeviceResource("Sensor", "HVAC", "Online", "Living Room", true);
        String authHeader = "Bearer dummy.token";

        Mockito.when(tokenService.getEmailFromToken("dummy.token")).thenReturn("user@test.com");
        Mockito.when(profilesContextFacade.fetchProfileIdByEmail("user@test.com")).thenReturn(1L);
        Mockito.when(deviceCommandService.handle(any(AddDeviceCommand.class), any(UserId.class))).thenReturn(100L);

        Device device = new Device(new UserId(1L), new DeviceName("Sensor"), new DeviceCategory("HVAC"),
                new DeviceStatus("Online"), new DeviceLocation("Living Room"), true);
        Mockito.when(deviceQueryService.handle(any(GetDeviceByIdQuery.class))).thenReturn(device);

        ResponseEntity<DeviceResource> response = devicesController.createDevice(resource, authHeader);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sensor", response.getBody().nombre());
    }

    @Test
    void createDevice_BadRequest_InvalidProfile() {
        CreateDeviceResource resource = new CreateDeviceResource("Sensor", "HVAC", "Online", "Living Room", true);
        String authHeader = "Bearer dummy.token";

        Mockito.when(tokenService.getEmailFromToken("dummy.token")).thenReturn("user@test.com");
        Mockito.when(profilesContextFacade.fetchProfileIdByEmail("user@test.com")).thenReturn(null);

        ResponseEntity<DeviceResource> response = devicesController.createDevice(resource, authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllDevices_Success() {
        // 1. Add a mock authorization header string
        String authHeader = "Bearer dummy.token";

        // 2. Mock the token parsing (optional but recommended if your controller uses it to fetch specific user devices)
        Mockito.when(tokenService.getEmailFromToken("dummy.token")).thenReturn("user@test.com");
        Mockito.when(profilesContextFacade.fetchProfileIdByEmail("user@test.com")).thenReturn(1L);

        Device device = new Device(new UserId(1L), new DeviceName("Sensor"), new DeviceCategory("HVAC"),
                new DeviceStatus("Online"), new DeviceLocation("Living Room"), true);

        Mockito.when(deviceQueryService.handle(any(GetAllDevicesQuery.class)))
                .thenReturn(Collections.singletonList(device));

        // 3. Pass the authHeader to the controller method
        ResponseEntity<List<DeviceResource>> response = devicesController.getAllDevices(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getDeviceById_Success() {
        Device device = new Device(new UserId(1L), new DeviceName("Sensor"), new DeviceCategory("HVAC"),
                new DeviceStatus("Online"), new DeviceLocation("Living Room"), true);
        Mockito.when(deviceQueryService.handle(any(GetDeviceByIdQuery.class))).thenReturn(device);

        ResponseEntity<DeviceResource> response = devicesController.getDeviceById(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sensor", response.getBody().nombre());
    }

    @Test
    void getDeviceById_NotFound() {
        Mockito.when(deviceQueryService.handle(any(GetDeviceByIdQuery.class))).thenReturn(null);

        ResponseEntity<DeviceResource> response = devicesController.getDeviceById(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteDevice_Success() {
        Mockito.doNothing().when(deviceCommandService).handle(any(DeleteDeviceCommand.class));

        ResponseEntity<String> response = devicesController.deleteDevice(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Device with given id successfully deleted", response.getBody());
    }
}
