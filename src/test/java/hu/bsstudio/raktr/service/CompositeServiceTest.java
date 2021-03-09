package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private static final String TEXT_IDENTIFIER = "textid";

    private static final Long OTHER_ID = 5L;
    private static final String OTHER_NAME = "composite_name2";
    private static final String OTHER_BARCODE = "barcode2";
    private static final String OTHER_TEXT_IDENTIFIER = "other_textid";

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
            .id(DEVICE_ID)
            .build();

        devices = spy(new ArrayList<>());
        devices.add(device);

        given(mockCompositeRequest.getId()).willReturn(ID);
        given(mockCompositeRequest.getName()).willReturn(NAME);
        given(mockCompositeRequest.getBarcode()).willReturn(BARCODE);
        given(mockCompositeRequest.getTextIdentifier()).willReturn(TEXT_IDENTIFIER);
        given(mockCompositeRequest.getLocation()).willReturn(location);
        given(mockCompositeRequest.getDevices()).willReturn(devices);

        defaultBuilder = CompositeItem.builder()
            .withId(ID)
            .withName(NAME)
            .withBarcode(BARCODE)
            .withTextIdentifier(TEXT_IDENTIFIER)
            .withLocation(location)
            .withDevices(devices);

        compositeItem = spy(defaultBuilder.build());
    }

    @Test
    void testCreateCompositeItem() {
        //given
        given(mockCompositeDao.save(mockCompositeRequest)).willReturn(compositeItem);

        //when
        var saved = underTest.create(mockCompositeRequest);

        //then
        verify(mockCompositeDao).save(mockCompositeRequest);
        assertTrue(saved.isPresent());
        assertEquals(compositeItem, saved.get());
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
        given(mockCompositeRequest.getTextIdentifier()).willReturn(OTHER_TEXT_IDENTIFIER);
        given(mockCompositeRequest.getLocation()).willReturn(otherLocation);

        given(mockCompositeDao.findById(ID)).willReturn(Optional.of(compositeItem));
        given(mockCompositeDao.save(any())).willReturn(compositeItem);

        //when
        var updated = underTest.update(mockCompositeRequest);

        //then
        verify(mockCompositeDao).findById(ID);
        verify(mockCompositeDao).save(any());

        verify(compositeItem).setLocation(otherLocation);
        verify(compositeItem).setName(OTHER_NAME);
        verify(compositeItem).setBarcode(OTHER_BARCODE);

        assertTrue(updated.isPresent());
        assertAll(
            () -> assertEquals(updated.get().getName(), OTHER_NAME),
            () -> assertEquals(updated.get().getBarcode(), OTHER_BARCODE),
            () -> assertEquals(updated.get().getTextIdentifier(), OTHER_TEXT_IDENTIFIER),
            () -> assertEquals(updated.get().getLocation(), otherLocation)
        );
    }

    @Test
    void testUpdateCompositeItemNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when
        final var updated = underTest.update(mockCompositeRequest);

        //then
        assertTrue(updated.isEmpty());
    }

    @Test
    void testDeleteCompositeItem() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.of(compositeItem));

        //when
        final var deleted = underTest.delete(mockCompositeRequest);

        //then
        verify(mockCompositeDao).delete(mockCompositeRequest);
        assertTrue(deleted.isPresent());
        assertEquals(mockCompositeRequest.getBarcode(), deleted.get().getBarcode());
    }

    @Test
    void testDeleteCompositeItemFailsNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when
        final var deleted = underTest.delete(mockCompositeRequest);

        //then
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testGetOneCompositeItem() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));

        //when
        final var item = underTest.getById(ID);

        //then
        verify(mockCompositeDao).findById(ID);
        assertTrue(item.isPresent());
        assertEquals(compositeItem, item.get());
    }

    @Test
    void testGetOneCompositeItemThrowsNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when
        final var compositeItem = underTest.getById(ID);

        //then
        assertTrue(compositeItem.isEmpty());
    }

    @Test
    void testAddDevice() {
        //given
        devices.clear();
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));
        given(mockCompositeDao.save(any())).willReturn(compositeItem);
        given(mockDeviceRepository.findById(DEVICE_ID)).willReturn(Optional.of(device));
        given(compositeItem.getDevices()).willReturn(devices);

        //when
        underTest.addDevice(ID, device);

        //then
        assertEquals(1, devices.size());
        assertEquals(device, devices.get(0));
    }

    @Test
    void testAddDeviceCompositeNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when
        final var compositeItem = underTest.addDevice(ID, device);

        //then
        assertTrue(compositeItem.isEmpty());
    }

    @Test
    void testDeleteDevice() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));
        given(mockDeviceRepository.findById(DEVICE_ID)).willReturn(Optional.ofNullable(device));
        given(mockCompositeDao.save(any())).willReturn(compositeItem);

        //when
        final var updatedCompositeItem = underTest.removeDeviceFromComposite(ID, device);

        //then
        assertEquals(0, devices.size());
        assertTrue(updatedCompositeItem.isPresent());
        assertEquals(compositeItem, updatedCompositeItem.get());

        verify(mockCompositeDao).findById(ID);
        verify(devices).remove(device);
        verify(mockCompositeDao).save(this.compositeItem);
    }

    @Test
    void testDeleteDeviceDeviceNotFound() {
        //given
        compositeItem.getDevices().clear();
        given(mockCompositeDao.findById(ID)).willReturn(Optional.ofNullable(compositeItem));

        //when
        final var compositeItem = underTest.removeDeviceFromComposite(ID, device);

        //then
        assertTrue(compositeItem.isEmpty());
    }

    @Test
    void testDeleteDeviceCompositeNotFound() {
        //given
        given(mockCompositeDao.findById(ID)).willReturn(Optional.empty());

        //when
        final var compositeItem = underTest.removeDeviceFromComposite(ID, device);

        //then
        assertTrue(compositeItem.isEmpty());
    }

}
