package hu.bsstudio.raktr.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hu.bsstudio.raktr.RaktrApplication;
import hu.bsstudio.raktr.dao.CategoryDao;
import hu.bsstudio.raktr.dao.CompositeItemDao;
import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceStatus;
import hu.bsstudio.raktr.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = RaktrApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties"
)
@ActiveProfiles("integrationtest")
public class ScannableControllerIntegrationTest {

    private static final String CATEGORY_NAME = "category";
    private static final String LOCATION_NAME = "location";
    public static final String COMPOSITE_BARCODE = "composite_barcode";
    public static final String COMPOSITE_ITEM_NAME = "compositeitem";
    public static final String DEVICE_NAME = "device";
    public static final String DEVICE_BARCODE = "device_barcode";
    public static final int DEVICE_WEIGHT = 1000;
    public static final int DEVICE_VALUE = 1200;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private CompositeItemDao compositeItemDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocationDao locationDao;

    @BeforeEach
    public final void init() {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        category = categoryDao.save(category);

        Location location = Location.builder()
            .withName(LOCATION_NAME)
            .build();

        location = locationDao.save(location);

        Device device = Device.builder()
            .withName(DEVICE_NAME)
            .withBarcode(DEVICE_BARCODE)
            .withMaker("maker")
            .withType("type")
            .withQuantity(1)
            .withSerial("serial")
            .withWeight(DEVICE_WEIGHT)
            .withCategory(category)
            .withLocation(location)
            .withStatus(DeviceStatus.GOOD)
            .withValue(DEVICE_VALUE)
            .build();

        deviceDao.save(device);

        CompositeItem compositeItem = CompositeItem.builder()
            .withBarcode(COMPOSITE_BARCODE)
            .withName(COMPOSITE_ITEM_NAME)
            .withLocation(location)
            .build();

        compositeItemDao.save(compositeItem);
    }

    @AfterEach
    public final void after() {
        deviceDao.deleteAll();
        compositeItemDao.deleteAll();
        categoryDao.deleteAll();
        locationDao.deleteAll();
    }

    @Test
    public void testGetDevice() throws Exception {
        mvc.perform(get("/api/scannable/" + DEVICE_BARCODE)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEVICE_NAME)));
    }

    @Test
    public void testGetCompositeItem() throws Exception {
        mvc.perform(get("/api/scannable/" + COMPOSITE_BARCODE)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_ITEM_NAME)));
    }
}
