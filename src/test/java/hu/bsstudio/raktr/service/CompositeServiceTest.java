package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class CompositeServiceTest {

    private static final Long ID = 1L;
    private static final String NAME = "composite_name";
    private static final String BARCODE = "barcode";

    private static final Long OTHER_ID = 5L;
    private static final String OTHER_NAME = "composite_name2";
    private static final String OTHER_BARCODE = "barcode2";

    private static final Long LOCATION_ID = 2L;
    private static final Long OTHER_LOCATION_ID = 7L;
    private static final Long DEVICE_ID = 3L;

    @Mock
    private CompositeItemRepository mockCompositeDao;

    @Mock
    private DeviceRepository mockDeviceRepository;

    @Mock
    private LocationRepository mockLocationRepository;

    @Mock
    private CompositeItem mockCompositeRequest;

    private CompositeItem compositeItem;
    private CompositeItem.Builder defaultBuilder;
    private Location location;
    private Device device;
    private CompositeService underTest;
    private List<Device> devices;

    @BeforeEach
    void init() {
        initMocks(this);

        underTest = new CompositeService(mockCompositeDao, mockDeviceRepository, mockLocationRepository);

        location = Location.builder()
            .withId(LOCATION_ID)
            .build();

        device = Device.builder()
            .withId(DEVICE_ID)
            .build();

        devices = spy(new ArrayList<>());
        devices.add(device);

        given(mockCompositeRequest.getId()).willReturn(ID);
        given(mockCompositeRequest.getName()).willReturn(NAME);
        given(mockCompositeRequest.getBarcode()).willReturn(BARCODE);
        given(mockCompositeRequest.getLocation()).willReturn(location);
        given(mockCompositeRequest.getDevices()).willReturn(devices);

        defaultBuilder = CompositeItem.builder()
            .withId(ID)
            .withName(NAME)
            .withBarcode(BARCODE)
            .withLocation(location)
            .withDevices(devices);

        compositeItem = spy(defaultBuilder.build());
    }

    @Test
    void testCreateCompositeItem() {
        //given
        given(mockCompositeDao.save(mockCompositeRequest)).willReturn(compositeItem);

        //when
        CompositeItem saved = underTest.create(mockCompositeRequest);

        //then
        verify(mockCompositeDao).save(mockCompositeRequest);
        assertEquals(compositeItem, saved);
    }

    @Test
    void testGetAll() {
        //given
        List<CompositeItem> compositeItems = new ArrayList<>();
        compositeItems.add(compositeItem);

        given(mockCompositeDao.findAll()).willReturn(compositeItems);

        //when
        List<CompositeItem> items = underTest.getAll();

        //then
        verify(mockCompositeDao).findAll();
        assertEquals(compositeItems, items);
    }

    @Test
    void testUpdateCompositeItem() {
        //given
        Location otherLocation = Location.builder()
            .withId(OTHER_LOCATION_ID)
            .build();

        given(mockCompositeRequest.getName()).willReturn(OTHER_NAME);
        given(mockCompositeRequest.getBarcode()).willReturn(OTHER_BARCODE);
        given(mockCompositeRequest.getLocation()).willReturn(otherLocation);

        given(mockCompositeDao.findById(ID)).willReturn(java.util.Optional.ofNullable(compositeItem));

        //when
        CompositeItem updated = underTest.update(mockCompositeRequest);

        //then
        verify(mockCompositeDao).findById(ID);
        verify(mockCompositeDao).save(any());

        verify(compositeItem).setLocation(otherLocation);
        verify(compositeItem).setName(OTHER_NAME);
        verify(compositeItem).setBarcode(OTHER_BARCODE);
    }

    @Test
    void testUpdateCompositeItemThrowsNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.update(mockCompositeRequest));
    }

    @Test
    void testDeleteCompositeItem() {
        //given

        //when
        CompositeItem deleted = underTest.delete(mockCompositeRequest);

        //then
        verify(mockCompositeDao).delete(mockCompositeRequest);
        assertEquals(mockCompositeRequest, deleted);
    }

    @Test
    void testGetOneCompositeItem() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));

        //when
        CompositeItem item = underTest.getOne(ID);

        //then
        verify(mockCompositeDao).findById(ID);
        assertEquals(compositeItem, item);
    }

    @Test
    void testGetOneCompositeItemThrowsNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.getOne(ID));
    }

    @Test
    void testAddDevice() {
        //given
        devices.clear();
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));
        given(compositeItem.getDevices()).willReturn(devices);

        //when
        underTest.addDevice(ID, device);

        //then
        assertEquals(1, devices.size());
        assertEquals(device, devices.get(0));
    }

    @Test
    void testAddDeviceThrowsNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.addDevice(ID, device));
    }

    @Test
    void testDeleteDevice() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));
        given(mockDeviceRepository.findById(DEVICE_ID)).willReturn(Optional.ofNullable(device));
        given(mockCompositeDao.save(any())).willReturn(compositeItem);

        //when
        CompositeItem updatedCompositeItem = underTest.deleteDevice(ID, device);

        //then
        assertEquals(0, devices.size());
        assertEquals(compositeItem, updatedCompositeItem);

        verify(mockCompositeDao).findById(ID);
        verify(mockDeviceRepository).findById(DEVICE_ID);
        verify(devices).remove(device);
        verify(mockCompositeDao).save(this.compositeItem);
    }

    @Test
    void testDeleteDeviceDeviceNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));
        given(mockDeviceRepository.findById(DEVICE_ID)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.deleteDevice(ID, device));
    }

    @Test
    void testDeleteDeviceCompositeNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());
        given(mockDeviceRepository.findById(DEVICE_ID)).willReturn(Optional.ofNullable(device));

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.deleteDevice(ID, device));
    }

}
