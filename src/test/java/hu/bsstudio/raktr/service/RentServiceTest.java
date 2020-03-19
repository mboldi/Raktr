package hu.bsstudio.raktr.service;

import static hu.bsstudio.raktr.model.BackStatus.BACK;
import static hu.bsstudio.raktr.model.BackStatus.OUT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.DeviceRentItemDao;
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.BackStatus;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceRentItem;
import hu.bsstudio.raktr.model.Rent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class RentServiceTest {

    private static final long DEVICE_ID = 1L;

    public static final long RENT_ID = 2L;
    public static final BackStatus RENT_ITEM_BACK_STATUS = OUT;
    public static final int RENT_ITEM_OUT_QUANTITY = 1;
    public static final String DESTINATION = "destination";
    public static final String ISSUER = "issuer";
    public static final String OUTDATE = "outdate";
    public static final String EXPBACKDATE = "expbackdate";
    public static final String ACTBACKDATE = "actbackdate";

    public static final long OTHER_RENT_ID = 3L;
    public static final BackStatus OTHER_RENT_ITEM_BACK_STATUS = BACK;
    public static final int OTHER_RENT_ITEM_OUT_QUANTITY = 2;
    public static final String OTHER_DESTINATION = "destination2";
    public static final String OTHER_ISSUER = "issuer2";
    public static final String OTHER_OUTDATE = "outdate2";
    public static final String OTHER_EXPBACKDATE = "expbackdate2";
    public static final String OTHER_ACTBACKDATE = "actbackdate2";
    public static final int OTHER_OUT_QUANTITY = 2;
    public static final int OUT_QUANTITY = 1;
    public static final int ZERO_OUT_QUANTITY = 0;

    @Mock
    private RentDao mockRentDao;

    @Mock
    private DeviceRentItemDao mockDeviceRentItemDao;

    @Mock
    private DeviceDao mockDeviceDao;

    @Mock
    private DeviceRentItem mockDeviceRentItemRequest;

    @Mock
    private Rent mockRentRequest;

    private RentService underTest;
    private Device device;
    private Rent rent;
    private Rent.Builder defaultBuilder;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new RentService(mockRentDao, mockDeviceRentItemDao, mockDeviceDao));

        device = Device.builder()
            .withId(DEVICE_ID)
            .withQuantity(1)
            .build();

        doReturn(device).when(mockDeviceDao).getOne(any());

        given(mockDeviceRentItemRequest.getId()).willReturn(DEVICE_ID);
        given(mockDeviceRentItemRequest.getBackStatus()).willReturn(RENT_ITEM_BACK_STATUS);
        given(mockDeviceRentItemRequest.getDevice()).willReturn(device);
        given(mockDeviceRentItemRequest.getOutQuantity()).willReturn(RENT_ITEM_OUT_QUANTITY);

        defaultBuilder = Rent.builder()
            .withId(RENT_ID)
            .withDestination(DESTINATION)
            .withIssuer(ISSUER)
            .withOutDate(OUTDATE)
            .withExpBackDate(EXPBACKDATE)
            .withActBackDate(ACTBACKDATE);

        given(mockRentRequest.getId()).willReturn(RENT_ID);
        given(mockRentRequest.getIssuer()).willReturn(ISSUER);
        given(mockRentRequest.getOutDate()).willReturn(OUTDATE);
        given(mockRentRequest.getDestination()).willReturn(DESTINATION);
        given(mockRentRequest.getExpBackDate()).willReturn(EXPBACKDATE);
        given(mockRentRequest.getActBackDate()).willReturn(ACTBACKDATE);

        rent = spy(defaultBuilder.build());
    }

    @Test
    void checkIfAvailableTrue() {
        //given
        doReturn(new ArrayList<DeviceRentItem>()).when(mockDeviceRentItemDao).findAll();
        doReturn(1).when(mockDeviceRentItemRequest).getOutQuantity();
        doReturn(device).when(mockDeviceRentItemRequest).getDevice();


        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest, null);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void checkIfAvailableFalseTooManyDevicesRequested() {
        //given
        doReturn(new ArrayList<DeviceRentItem>()).when(mockDeviceRentItemDao).findAll();
        doReturn(2).when(mockDeviceRentItemRequest).getOutQuantity();
        doReturn(device).when(mockDeviceRentItemRequest).getDevice();

        //when
        boolean isAvailable = underTest.checkIfAvailable(mockDeviceRentItemRequest, null);

        //then
        assertFalse(isAvailable);
    }

    @Test
    void checkIfAvailableFalseMaxAmountIsOut() {
        //given
        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(OUT)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();

        //when
        boolean isAvailable = underTest.checkIfAvailable(rentItem, null);

        //then
        assertFalse(isAvailable);
    }

    @Test
    void checkIfAvailableTrueMaxAmountIsOutButBack() {
        //given
        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(BACK)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();

        //when
        boolean isAvailable = underTest.checkIfAvailable(rentItem, null);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void checkIfAvailableTrueSomeIsOutButNotAll() {
        //given
        Device deviceMultiple = Device.builder()
            .withId(DEVICE_ID)
            .withQuantity(2)
            .build();

        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withBackStatus(OUT)
            .withDevice(deviceMultiple)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(deviceMultiple).when(mockDeviceDao).getOne(any());
        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();

        //when
        boolean isAvailable = underTest.checkIfAvailable(rentItem, null);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void checkIfAvailableTrueRentItemToUpdateNotNull() {
        //given
        DeviceRentItem rentItem = DeviceRentItem.builder()
            .withId(2L)
            .withBackStatus(OUT)
            .withDevice(device)
            .withOutQuantity(1)
            .build();

        ArrayList<DeviceRentItem> deviceRentItems = new ArrayList<>();
        deviceRentItems.add(rentItem);

        doReturn(deviceRentItems).when(mockDeviceRentItemDao).findAll();

        //when
        boolean isAvailable = underTest.checkIfAvailable(rentItem, rentItem);

        //then
        assertTrue(isAvailable);
    }

    @Test
    void testCreateRent() {
        //given
        doReturn(rent).when(mockRentDao).save(mockRentRequest);

        //when
        final Rent saved = underTest.create(mockRentRequest);

        //then
        verify(mockRentDao).save(mockRentRequest);
        assertAll(
            () -> assertNotNull(saved),
            () -> assertEquals(mockRentRequest.getId(), RENT_ID),
            () -> assertEquals(mockRentRequest.getActBackDate(), ACTBACKDATE),
            () -> assertEquals(mockRentRequest.getDestination(), DESTINATION),
            () -> assertEquals(mockRentRequest.getExpBackDate(), EXPBACKDATE),
            () -> assertEquals(mockRentRequest.getIssuer(), ISSUER),
            () -> assertEquals(mockRentRequest.getOutDate(), OUTDATE)
        );
    }

    @Test
    void testUpdateRent() {
        //given
        final Rent updatedRent = Rent.builder()
            .withId(OTHER_RENT_ID)
            .withIssuer(OTHER_ISSUER)
            .withDestination(OTHER_DESTINATION)
            .withOutDate(OTHER_OUTDATE)
            .withExpBackDate(OTHER_EXPBACKDATE)
            .withActBackDate(OTHER_ACTBACKDATE)
            .build();

        given(mockRentDao.save(rent)).willReturn(updatedRent);
        given(mockRentDao.findById(mockRentRequest.getId())).willReturn(java.util.Optional.ofNullable(rent));

        //when
        final Rent updated = underTest.update(mockRentRequest);

        //then
        verify(mockRentRequest).getActBackDate();
        verify(mockRentRequest).getDestination();
        verify(mockRentRequest).getExpBackDate();
        verify(mockRentRequest).getIssuer();
        verify(mockRentRequest).getOutDate();

        verify(rent).setActBackDate(any());
        verify(rent).setDestination(any());
        verify(rent).setExpBackDate(any());
        verify(rent).setIssuer(any());
        verify(rent).setOutDate(any());

        assertAll(
            () -> assertNotNull(updated),
            () -> assertEquals(updated.getActBackDate(), OTHER_ACTBACKDATE),
            () -> assertEquals(updated.getDestination(), OTHER_DESTINATION),
            () -> assertEquals(updated.getExpBackDate(), OTHER_EXPBACKDATE),
            () -> assertEquals(updated.getIssuer(), OTHER_ISSUER),
            () -> assertEquals(updated.getOutDate(), OTHER_OUTDATE)
        );
    }

    @Test
    void testUpdateFindsNoRentToUpdate() {
        //given
        given(mockRentDao.findById(any())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.update(mockRentRequest));
    }

    @Test
    void testGetRentById() {
        //given
        given(mockRentDao.findById(RENT_ID)).willReturn(Optional.ofNullable(rent));

        //when
        Rent foundRent = underTest.getById(RENT_ID);

        //then
        assertNotNull(foundRent);
        assertEquals(rent, foundRent);
    }

    @Test
    void testGetRentByIdFindsNoDevice() {
        //given
        given(mockRentDao.findById(any())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.getById(RENT_ID));
    }

    @Test
    void testDeleteRent() {
        //given

        //when
        Rent deleted = underTest.delete(mockRentRequest);

        //then
        verify(mockRentDao).delete(mockRentRequest);
        assertEquals(mockRentRequest, deleted);
    }

    @Test
    void testGetAllRents() {
        //given
        ArrayList<Rent> rents = new ArrayList<>();
        rents.add(rent);

        given(mockRentDao.findAll()).willReturn(rents);

        //when
        List<Rent> gotRents = underTest.getAll();

        //then
        verify(mockRentDao).findAll();
        assertEquals(rents, gotRents);
    }

    @Test
    void testAddDeviceToRentBadId() {
        //given
        given(mockRentDao.findById(any())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.updateDeviceInRent(RENT_ID, mockDeviceRentItemRequest));
    }

    @Test
    void testAddDeviceToRentNotAvailableQuantity() {
        //given
        given(mockRentDao.findById(any())).willReturn(Optional.ofNullable(rent));
        doReturn(false).when(underTest).checkIfAvailable(any(), any());
        doReturn(mockDeviceRentItemRequest).when(rent).getRentItemOfDevice(any());

        //when

        //then
        assertThrows(NotAvailableQuantityException.class, () -> underTest.updateDeviceInRent(RENT_ID, mockDeviceRentItemRequest));
    }

    @Test
    void testAddDeviceToRentNewDevice() {
        //given
        given(mockRentDao.findById(any())).willReturn(Optional.ofNullable(rent));
        given(mockRentDao.save(any())).willReturn(rent);
        given(mockDeviceRentItemDao.save(any())).willReturn(mockDeviceRentItemRequest);
        doReturn(true).when(underTest).checkIfAvailable(any(), any());
        doReturn(null).when(rent).getRentItemOfDevice(any());

        ArrayList<DeviceRentItem> rentItems = spy(new ArrayList<>());
        doReturn(rentItems).when(rent).getRentItems();

        //when
        Rent updatedRent = underTest.updateDeviceInRent(RENT_ID, mockDeviceRentItemRequest);

        //then
        assertNotNull(updatedRent);
        assertEquals(rent, updatedRent);

        verify(mockDeviceRentItemDao).save(mockDeviceRentItemRequest);
        verify(rentItems).add(mockDeviceRentItemRequest);
        assertEquals(1, rentItems.size());
    }

    @Test
    void testAddDeviceToRentUpdateOutQuantity() {
        //given
        given(mockDeviceRentItemRequest.getBackStatus()).willReturn(OTHER_RENT_ITEM_BACK_STATUS);
        given(mockDeviceRentItemRequest.getOutQuantity()).willReturn(OTHER_RENT_ITEM_OUT_QUANTITY);

        DeviceRentItem rentItemToUpdate = spy(DeviceRentItem.builder()
            .withOutQuantity(OUT_QUANTITY)
            .withBackStatus(BACK)
            .build());

        given(mockRentDao.findById(any())).willReturn(Optional.ofNullable(rent));
        given(mockRentDao.save(any())).willReturn(rent);
        given(mockDeviceRentItemDao.save(any())).willReturn(mockDeviceRentItemRequest);
        doReturn(true).when(underTest).checkIfAvailable(any(), any());
        doReturn(rentItemToUpdate).when(rent).getRentItemOfDevice(any());

        ArrayList<DeviceRentItem> rentItems = spy(new ArrayList<>());
        rentItems.add(rentItemToUpdate);
        doReturn(rentItems).when(rent).getRentItems();

        //when
        Rent updatedRent = underTest.updateDeviceInRent(RENT_ID, mockDeviceRentItemRequest);

        //then
        verify(rentItemToUpdate).setBackStatus(OTHER_RENT_ITEM_BACK_STATUS);
        verify(rentItemToUpdate).setOutQuantity(OTHER_RENT_ITEM_OUT_QUANTITY);
        assertEquals(OTHER_RENT_ITEM_BACK_STATUS, updatedRent.getRentItems().get(0).getBackStatus());
        assertEquals(OTHER_RENT_ITEM_OUT_QUANTITY, updatedRent.getRentItems().get(0).getOutQuantity());
    }

    @Test
    void testAddDeviceToRentOutQuantityZero() {
        //given
        given(mockDeviceRentItemRequest.getOutQuantity()).willReturn(ZERO_OUT_QUANTITY);

        DeviceRentItem rentItemToUpdate = spy(DeviceRentItem.builder()
            .withOutQuantity(OUT_QUANTITY)
            .withBackStatus(BACK)
            .build());

        given(mockRentDao.findById(any())).willReturn(Optional.ofNullable(rent));
        given(mockRentDao.save(any())).willReturn(rent);
        given(mockDeviceRentItemDao.save(any())).willReturn(mockDeviceRentItemRequest);
        doReturn(true).when(underTest).checkIfAvailable(any(), any());
        doReturn(rentItemToUpdate).when(rent).getRentItemOfDevice(any());

        ArrayList<DeviceRentItem> rentItems = spy(new ArrayList<>());
        rentItems.add(rentItemToUpdate);
        doReturn(rentItems).when(rent).getRentItems();

        //when
        Rent updatedRent = underTest.updateDeviceInRent(RENT_ID, mockDeviceRentItemRequest);

        //then
        assertEquals(0, updatedRent.getRentItems().size());
        verify(mockDeviceRentItemDao).delete(any());
        verify(mockRentDao).save(rent);
    }

}
