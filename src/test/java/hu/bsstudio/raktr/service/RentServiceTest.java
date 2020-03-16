package hu.bsstudio.raktr.service;

import static hu.bsstudio.raktr.model.BackStatus.BACK;
import static hu.bsstudio.raktr.model.BackStatus.OUT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.DeviceRentItemDao;
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceRentItem;
import hu.bsstudio.raktr.model.Rent;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class RentServiceTest {

    public static final long REQUEST_ID = 1L;
    @Mock
    private RentDao mockRentDao;

    @Mock
    private DeviceRentItemDao mockDeviceRentItemDao;

    @Mock
    private DeviceDao mockDeviceDao;

    @Mock
    private DeviceRentItem mockDeviceRentItemRequest;


    private Rent rent;
    private RentService underTest;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new RentService(mockRentDao, mockDeviceRentItemDao, mockDeviceDao));
    }

    @Test
    void checkIfAvailableTrue() {
        //given
        Device device = Device.builder()
            .withId(REQUEST_ID)
            .withQuantity(1)
            .build();

        doReturn(new ArrayList<DeviceRentItem>()).when(mockDeviceRentItemDao).findAll();
        doReturn(device).when(mockDeviceDao).getOne(any());
        doReturn(1).when(mockDeviceRentItemRequest).getOutQuantity();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void checkIfAvailableFalseTooManyDevicesRequested() {
        //given
        Device device = Device.builder()
            .withId(REQUEST_ID)
            .withQuantity(1)
            .build();

        doReturn(device).when(mockDeviceDao).getOne(any());
        doReturn(new ArrayList<DeviceRentItem>()).when(mockDeviceRentItemDao).findAll();
        doReturn(2).when(mockDeviceRentItemRequest).getOutQuantity();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest);

        //then
        assertFalse(isAvailable);
    }

    @Test
    void checkIfAvailableFalseMaxAmountIsOut() {
        //given
        Device device = Device.builder()
            .withId(REQUEST_ID)
            .withQuantity(1)
            .build();

        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(OUT)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(device).when(mockDeviceDao).getOne(any());
        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();
        doReturn(REQUEST_ID).when(mockDeviceRentItemRequest).getId();
        doReturn(1).when(mockDeviceRentItemRequest).getOutQuantity();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest);

        //then
        assertFalse(isAvailable);
    }

    @Test
    void checkIfAvailableTrueMaxAmountIsOutButBack() {
        //given
        Device device = Device.builder()
            .withId(REQUEST_ID)
            .withQuantity(1)
            .build();

        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(BACK)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(device).when(mockDeviceDao).getOne(any());
        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();
        doReturn(REQUEST_ID).when(mockDeviceRentItemRequest).getId();
        doReturn(1).when(mockDeviceRentItemRequest).getOutQuantity();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void checkIfAvailableTrueSomeIsOutButNotAll() {
        //given
        Device device = Device.builder()
            .withId(REQUEST_ID)
            .withQuantity(2)
            .build();

        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(OUT)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(device).when(mockDeviceDao).getOne(any());
        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();
        doReturn(REQUEST_ID).when(mockDeviceRentItemRequest).getId();
        doReturn(1).when(mockDeviceRentItemRequest).getOutQuantity();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest);

        //then
        assertTrue(isAvailable);
    }

}
