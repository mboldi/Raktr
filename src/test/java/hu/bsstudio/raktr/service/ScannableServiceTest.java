package hu.bsstudio.raktr.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class ScannableServiceTest {

    private static final Long DEVICE_ID = 1L;
    private static final String DEVICE_BARCODE = "barcode-device-01";
    private static final Long COMPOSITE_ID = 2L;
    private static final String COMPOSITE_BARCODE = "barcode-composite-01";

    @Mock
    private DeviceRepository mockDeviceRepository;

    @Mock
    private CompositeItemRepository mockCompositeDao;

    private CompositeItem compositeItem;
    private Device device;

    private ScannableService underTest;

    @BeforeEach
    void init() {
        initMocks(this);

        device = spy(Device.builder()
            .withId(DEVICE_ID)
            .withBarcode(DEVICE_BARCODE)
            .build());

        compositeItem = spy(CompositeItem.builder()
            .withId(COMPOSITE_ID)
            .withBarcode(COMPOSITE_BARCODE)
            .build());

        underTest = new ScannableService(mockDeviceRepository, mockCompositeDao);
    }

    @Test
    void testGetDeviceByBarcode() {
        //given
        given(mockDeviceRepository.findByBarcode(DEVICE_BARCODE)).willReturn(ofNullable(device));

        //when
        Scannable byBarcode = underTest.getByBarcode(DEVICE_BARCODE);

        //then
        assertEquals(device, byBarcode);
    }

    @Test
    void testGetDeviceByBarcodeGetComposite() {
        //given
        given(mockDeviceRepository.findByBarcode(COMPOSITE_BARCODE)).willReturn(empty());
        given(mockCompositeDao.findByBarcode(COMPOSITE_BARCODE)).willReturn(java.util.Optional.ofNullable(compositeItem));

        //when
        Scannable byBarcode = underTest.getByBarcode(COMPOSITE_BARCODE);

        //then
        assertEquals(compositeItem, byBarcode);
    }

    @Test
    void testGetDeviceByBarcodeNothingFound() {
        //given
        given(mockDeviceRepository.findByBarcode(COMPOSITE_BARCODE)).willReturn(empty());
        given(mockCompositeDao.findByBarcode(COMPOSITE_BARCODE)).willReturn(empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.getByBarcode(COMPOSITE_BARCODE));
    }
}
