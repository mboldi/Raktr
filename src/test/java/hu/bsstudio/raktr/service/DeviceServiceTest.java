package hu.bsstudio.raktr.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceTest {
    private DeviceService underTest = new DeviceService();

    @Test
    void helloWorld() {
        //given

        //when
        String result = underTest.helloWorld("Bela");
        //then
        assertEquals("Hello Bela!", result);
    }
}