package hu.bsstudio.raktr.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import hu.bsstudio.raktr.dao.DeviceDao;
import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Category;
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
import org.springframework.web.util.NestedServletException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = RaktrApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties"
)
public class DeviceControllerIntegrationTest {

    private static final String CATEGORY_NAME = "category";
    private static final String LOCATION_NAME = "location";
    public static final String DEVICE_NAME = "device";
    public static final String DEVICE_BARCODE = "device_barcode";
    public static final String DEVICE_NAME_2 = "device2";
    public static final int DEVICE_WEIGHT = 1000;
    public static final int DEVICE_VALUE = 1200;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocationDao locationDao;

    private Device.Builder defaultBuilder;

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

        defaultBuilder = Device.builder()
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
    }

    @AfterEach
    public final void after() {
        deviceDao.deleteAll();
        categoryDao.deleteAll();
        locationDao.deleteAll();
    }

    @Test
    public void testGetDevice() throws Exception {
        Device device = defaultBuilder
            .build();

        deviceDao.save(device);

        mvc.perform(get("/api/device")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(DEVICE_NAME)));
    }

    @Test
    public void testAddDevice() throws Exception {
        Device device = defaultBuilder
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device);

        requestBody = "{ \"Device\": " + requestBody + "}";

        mvc.perform(post("/api/device")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEVICE_NAME)));
    }

    @Test
    public void testAddDeviceFailsDeviceNotUnique() throws Exception {
        Device device = defaultBuilder
            .build();

        deviceDao.save(device);

        Device device2 = defaultBuilder
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device2);

        requestBody = "{ \"Device\": " + requestBody + "}";

        String finalRequestBody = requestBody;
        assertThrows(NestedServletException.class, () ->
            mvc.perform(post("/api/device")
                .contentType(MediaType.APPLICATION_JSON)
                .content(finalRequestBody))
        );
    }

    @Test
    public void testUpdateDevice() throws Exception {
        Device device = defaultBuilder
            .build();

        Device device2 = deviceDao.save(device);
        device2.setName(DEVICE_NAME_2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device2);

        requestBody = "{ \"Device\": " + requestBody + "}";

        mvc.perform(put("/api/device")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEVICE_NAME_2)));

        mvc.perform(get("/api/device")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetDeviceById() throws Exception {
        Device device = defaultBuilder
            .build();

        device = deviceDao.save(device);

        mvc.perform(get("/api/device/" + device.getId()))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEVICE_NAME)));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        Device device = defaultBuilder
            .build();

        device = deviceDao.save(device);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(device);

        requestBody = "{ \"Device\": " + requestBody + "}";

        mvc.perform(delete("/api/device/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEVICE_NAME)));

        mvc.perform(get("/api/device/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
    }

}
