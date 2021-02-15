package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceStatus;
import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class DeviceServiceTest {

    private static final Long ID = 1L;
    private static final String DEVICE_NAME = "device";
    private static final String MAKER = "maker";
    private static final String TYPE = "type";
    private static final String SERIAL = "serial";
    private static final Integer VALUE = 100;
    private static final String BARCODE = "barcode";
    private static final Integer WEIGHT = 1000;
    private static final String LOCATION_NAME = "location";
    private static final DeviceStatus STATUS = DeviceStatus.GOOD;
    private static final String CATEGORY_NAME = "category";
    public static final int QUANTITY = 1;

    private static final String OTHER_NAME = "device2";
    private static final String OTHER_MAKER = "maker2";
    private static final String OTHER_TYPE = "type2";
    private static final String OTHER_SERIAL = "serial2";
    private static final Integer OTHER_VALUE = 102;
    private static final String OTHER_BARCODE = "barcode2";
    private static final Integer OTHER_WEIGHT = 1002;
    private static final DeviceStatus OTHER_STATUS = DeviceStatus.NEEDS_REPAIR;
    public static final int OTHER_QUANTITY = 2;

    @Mock
    private DeviceRepository mockDeviceRepository;
    @Mock
    private Device deviceRequest;

    @Mock
    private CategoryRepository mockCategoryRepository;

    @Mock
    private LocationRepository mockLocationRepository;

    private Device device;
    private Location location;
    private Category category;
    private Device.Builder defaultBuilder;
    private DeviceService underTest;

    @BeforeEach
    void init() {
        initMocks(this);

        underTest = new DeviceService(mockDeviceRepository, mockCategoryRepository, mockLocationRepository);

        location = Location.builder()
            .withName(LOCATION_NAME)
            .build();
        category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        defaultBuilder = Device.builder()
            .withName(DEVICE_NAME)
            .withMaker(MAKER)
            .withType(TYPE)
            .withSerial(SERIAL)
            .withValue(VALUE)
            .withBarcode(BARCODE)
            .withWeight(WEIGHT)
            .withStatus(STATUS)
            .withLocation(location)
            .withCategory(category)
            .withQuantity(QUANTITY);

        deviceRequest = spy(defaultBuilder.build());

        device = spy(defaultBuilder.build());
    }

    @Test
    void testCreateDevice() {
        //given
        doReturn(device).when(mockDeviceRepository).save(deviceRequest);
        given(mockCategoryRepository.findByName(CATEGORY_NAME)).willReturn(Optional.ofNullable(category));
        given(mockLocationRepository.findByName(LOCATION_NAME)).willReturn(Optional.ofNullable(location));

        //when
        final var saved = underTest.create(deviceRequest);

        //then
        verify(mockDeviceRepository).save(any(Device.class));
        assertAll(
            () -> assertTrue(saved.isPresent()),
            () -> assertEquals(deviceRequest.getName(), saved.get().getName()),
            () -> assertEquals(deviceRequest.getMaker(), saved.get().getMaker()),
            () -> assertEquals(deviceRequest.getType(), saved.get().getType()),
            () -> assertEquals(deviceRequest.getSerial(), saved.get().getSerial()),
            () -> assertEquals(deviceRequest.getValue(), saved.get().getValue()),
            () -> assertEquals(deviceRequest.getBarcode(), saved.get().getBarcode()),
            () -> assertEquals(deviceRequest.getWeight(), saved.get().getWeight()),
            () -> assertEquals(deviceRequest.getStatus(), saved.get().getStatus()),
            () -> assertEquals(deviceRequest.getQuantity(), saved.get().getQuantity())
        );
    }

    @Test
    void testUpdateDevice() {
        //given
        deviceRequest = spy(Device.builder()
            .withName(DEVICE_NAME)
            .withMaker(MAKER)
            .withType(TYPE)
            .withSerial(SERIAL)
            .withValue(VALUE)
            .withBarcode(BARCODE)
            .withWeight(WEIGHT)
            .withStatus(STATUS)
            .withQuantity(QUANTITY)
            .withCategory(category)
            .withLocation(location)
            .build());

        final Device updatedDevice = Device.builder()
            .withName(OTHER_NAME)
            .withMaker(OTHER_MAKER)
            .withType(OTHER_TYPE)
            .withSerial(OTHER_SERIAL)
            .withValue(OTHER_VALUE)
            .withBarcode(OTHER_BARCODE)
            .withWeight(OTHER_WEIGHT)
            .withStatus(OTHER_STATUS)
            .withQuantity(OTHER_QUANTITY)
            .build();

        given(mockDeviceRepository.save(device)).willReturn(updatedDevice);
        given(mockDeviceRepository.findById(any())).willReturn(java.util.Optional.ofNullable(device));

        given(mockCategoryRepository.findByName(CATEGORY_NAME)).willReturn(Optional.ofNullable(category));
        given(mockLocationRepository.findByName(LOCATION_NAME)).willReturn(Optional.ofNullable(location));

        //when
        final var updated = underTest.update(deviceRequest);

        //then
        verify(deviceRequest).getName();
        verify(deviceRequest).getMaker();
        verify(deviceRequest).getType();
        verify(deviceRequest).getSerial();
        verify(deviceRequest).getValue();
        verify(deviceRequest).getBarcode();
        verify(deviceRequest).getWeight();
        verify(deviceRequest).getStatus();
        verify(deviceRequest).getQuantity();

        verify(device).setName(any());
        verify(device).setMaker(any());
        verify(device).setType(any());
        verify(device).setSerial(any());
        verify(device).setValue(any());
        verify(device).setBarcode(any());
        verify(device).setWeight(any());
        verify(device).setStatus(any());
        verify(device).setQuantity(any());

        assertTrue(updated.isPresent());

        assertAll(
            () -> assertEquals(OTHER_NAME, updated.get().getName()),
            () -> assertEquals(OTHER_MAKER, updated.get().getMaker()),
            () -> assertEquals(OTHER_TYPE, updated.get().getType()),
            () -> assertEquals(OTHER_SERIAL, updated.get().getSerial()),
            () -> assertEquals(OTHER_VALUE, updated.get().getValue()),
            () -> assertEquals(OTHER_BARCODE, updated.get().getBarcode()),
            () -> assertEquals(OTHER_WEIGHT, updated.get().getWeight()),
            () -> assertEquals(OTHER_STATUS, updated.get().getStatus()),
            () -> assertEquals(OTHER_QUANTITY, updated.get().getQuantity())
        );
    }

    @Test
    void testUpdateFindsNoDeviceToUpdate() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.empty());
        given(mockCategoryRepository.findByName(CATEGORY_NAME)).willReturn(Optional.ofNullable(category));
        given(mockLocationRepository.findByName(LOCATION_NAME)).willReturn(Optional.ofNullable(location));


        //when
        final var device = underTest.update(deviceRequest);
        //then
        assertTrue(device.isEmpty());
    }

    @Test
    void testGetById() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.ofNullable(device));

        //when
        final var foundDevice = underTest.getById(ID);

        //then
        verify(mockDeviceRepository).findById(ID);
        assertTrue(foundDevice.isPresent());
        assertEquals(device, foundDevice.get());
    }

    @Test
    void testGetByIdFindNoDevice() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.empty());

        //when
        final var device = underTest.getById(ID);
        //then
        assertTrue(device.isEmpty());
    }

    @Test
    void testDelete() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.of(device));

        //when
        underTest.delete(deviceRequest);

        //then
        verify(mockDeviceRepository).delete(deviceRequest);
    }

    @Test
    void testGetAll() {
        //given
        List<Device> devices = new ArrayList<>();
        devices.add(device);
        given(mockDeviceRepository.findAll()).willReturn(devices);

        //when
        List<Device> all = underTest.getAll();

        //then
        assertEquals(devices, all);
    }
}
