package hu.bsstudio.raktr.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class CompositeControllerIntegrationTest {

    private static final String CATEGORY_NAME = "category";
    private static final String LOCATION_NAME = "location";
    private static final String DEVICE_NAME = "device";
    private static final String DEVICE_BARCODE = "device_barcode";
    private static final int DEVICE_WEIGHT = 1000;
    private static final int DEVICE_VALUE = 1200;
    private static final String COMPOSITE_BARCODE = "comp_barcode";
    private static final String COMPOSITE_NAME = "composite";
    private static final String COMPOSITE_NAME_2 = "composite2";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CompositeItemDao compositeItemDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocationDao locationDao;

    private Device.Builder defaultDeviceBuilder;
    private CompositeItem.Builder defaultBuilder;

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

        defaultDeviceBuilder = Device.builder()
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
            .withValue(DEVICE_VALUE);

        defaultBuilder = CompositeItem.builder()
            .withBarcode(COMPOSITE_BARCODE)
            .withLocation(location)
            .withName(COMPOSITE_NAME);
    }

    @AfterEach
    public final void after() {
        compositeItemDao.deleteAll();
        deviceDao.deleteAll();
        categoryDao.deleteAll();
        locationDao.deleteAll();
    }

    @Test
    public void testGetCompositeItem() throws Exception {
        compositeItemDao.save(defaultBuilder.build());

        mvc.perform(get("/api/composite")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(COMPOSITE_NAME)));
    }

    @Test
    public void testAddCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(compositeItem);

        requestBody = "{ \"CompositeItem\": " + requestBody + "}";

        mvc.perform(post("/api/composite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));

        assertEquals(1, compositeItemDao.findAll().size());
    }

    @Test
    public void testUpdateCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();

        CompositeItem compositeToUpdate = compositeItemDao.save(compositeItem);
        compositeToUpdate.setName(COMPOSITE_NAME_2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(compositeToUpdate);

        requestBody = "{ \"CompositeItem\": " + requestBody + "}";

        mvc.perform(put("/api/composite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME_2)));

        mvc.perform(get("/api/composite")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(COMPOSITE_NAME_2)));
    }

    @Test
    public void testGetCompositeItemById() throws Exception {
        CompositeItem compositeItem = compositeItemDao.save(defaultBuilder.build());

        mvc.perform(get("/api/composite/" + compositeItem.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));
    }

    @Test
    public void testGetCompositeItemByIdFails() throws Exception {
        mvc.perform(get("/api/composite/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCompositeItem() throws Exception {
        CompositeItem compositeItem = compositeItemDao.save(defaultBuilder.build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(compositeItem);

        requestBody = "{ \"CompositeItem\": " + requestBody + "}";

        mvc.perform(delete("/api/composite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));

        assertEquals(0, compositeItemDao.findAll().size());
    }

    @Test
    public void testAddDeviceToCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();
        Device device = defaultDeviceBuilder.build();

        compositeItem = compositeItemDao.save(compositeItem);

        device = deviceDao.save(device);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device);

        requestBody = "{ \"Device\": " + requestBody + "}";

        mvc.perform(put("/api/composite/" + compositeItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));

        mvc.perform(get("/api/composite")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].devices", hasSize(1)));
    }

    @Test
    public void testDeleteDeviceFromCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();
        Device device = defaultDeviceBuilder.build();

        compositeItem = compositeItemDao.save(compositeItem);

        device = deviceDao.save(device);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device);

        requestBody = "{ \"Device\": " + requestBody + "}";

        mvc.perform(put("/api/composite/" + compositeItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));

        mvc.perform(get("/api/composite")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].devices", hasSize(1)));

        mvc.perform(delete("/api/composite/" + compositeItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(COMPOSITE_NAME)));

        mvc.perform(get("/api/composite")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].devices", hasSize(0)));
    }
}
