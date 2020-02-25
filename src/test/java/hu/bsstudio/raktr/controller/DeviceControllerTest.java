package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

class DeviceControllerTest {

    @Spy
    private DeviceService mockDeviceService;

    private DeviceController underTest;

    DeviceControllerTest() {
        initMocks(this);
        underTest = new DeviceController(mockDeviceService);
    }
}