package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static hu.bsstudio.raktr.service.DeviceService.HELLO_WORLD;
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

    @Test
    void testHelloWorld() {
        //given

        //when
        final String result = underTest.helloWorld();

        //then
        assertEquals(HELLO_WORLD, result);
    }

    @Test
    void testHelloWorld1() {
        //given
        final String ANNA = "Anna";
        doReturn(ANNA).when(mockDeviceService).helloWorld(ANNA);

        //when
        final String result = underTest.helloWorld(ANNA);

        //then
        assertEquals(ANNA, result);


    }
}