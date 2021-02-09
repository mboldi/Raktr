package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceStatus;
import hu.bsstudio.raktr.model.Location;
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
        final Device saved = underTest.create(deviceRequest);

        //then
        verify(mockDeviceRepository).save(any(Device.class));
        assertAll(
            () -> assertNotNull(saved),
            () -> assertEquals(deviceRequest.getName(), saved.getName()),
            () -> assertEquals(deviceRequest.getMaker(), saved.getMaker()),
            () -> assertEquals(deviceRequest.getType(), saved.getType()),
            () -> assertEquals(deviceRequest.getSerial(), saved.getSerial()),
            () -> assertEquals(deviceRequest.getValue(), saved.getValue()),
            () -> assertEquals(deviceRequest.getBarcode(), saved.getBarcode()),
            () -> assertEquals(deviceRequest.getWeight(), saved.getWeight()),
            () -> assertEquals(deviceRequest.getStatus(), saved.getStatus()),
            () -> assertEquals(deviceRequest.getQuantity(), saved.getQuantity())
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
        final Device updated = underTest.update(deviceRequest);

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

        assertAll(
            () -> assertNotNull(updated),
            () -> assertEquals(OTHER_NAME, updated.getName()),
            () -> assertEquals(OTHER_MAKER, updated.getMaker()),
            () -> assertEquals(OTHER_TYPE, updated.getType()),
            () -> assertEquals(OTHER_SERIAL, updated.getSerial()),
            () -> assertEquals(OTHER_VALUE, updated.getValue()),
            () -> assertEquals(OTHER_BARCODE, updated.getBarcode()),
            () -> assertEquals(OTHER_WEIGHT, updated.getWeight()),
            () -> assertEquals(OTHER_STATUS, updated.getStatus()),
            () -> assertEquals(OTHER_QUANTITY, updated.getQuantity())
        );
    }

    @Test
    void testUpdateFindsNoDeviceToUpdate() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.empty());
        given(mockCategoryRepository.findByName(CATEGORY_NAME)).willReturn(Optional.ofNullable(category));
        given(mockLocationRepository.findByName(LOCATION_NAME)).willReturn(Optional.ofNullable(location));


        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.update(deviceRequest));
    }

    @Test
    void testGetById() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.ofNullable(device));

        //when
        Device foundDevice = underTest.getById(ID);

        //then
        verify(mockDeviceRepository).findById(ID);
        assertEquals(device, foundDevice);
    }

    @Test
    void testGetByIdFindNoDevice() {
        //given
        given(mockDeviceRepository.findById(any())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.getById(ID));
    }

    @Test
    void testDelete() {
        //given

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
