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
import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
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
    private CompositeItemRepository compositeItemRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    private Device.DeviceBuilder defaultDeviceBuilder;
    private CompositeItem.CompositeItemBuilder defaultBuilder;

    @BeforeEach
    public final void init() {
        Category category = Category.builder()
            .name(CATEGORY_NAME)
            .build();

        category = categoryRepository.save(category);

        Location location = Location.builder()
            .withName(LOCATION_NAME)
            .build();

        location = locationRepository.save(location);

        defaultDeviceBuilder = Device.builder()
            .name(DEVICE_NAME)
            .barcode(DEVICE_BARCODE)
            .textIdentifier("text_id")
            .maker("maker")
            .type("type")
            .quantity(1)
            .serial("serial")
            .weight(DEVICE_WEIGHT)
            .category(category)
            .location(location)
            .status(DeviceStatus.GOOD)
            .value(DEVICE_VALUE);

        defaultBuilder = CompositeItem.builder()
            .barcode(COMPOSITE_BARCODE)
            .location(location)
            .name(COMPOSITE_NAME);
    }

    @AfterEach
    public final void after() {
        compositeItemRepository.deleteAll();
        deviceRepository.deleteAll();
        categoryRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testGetCompositeItem() throws Exception {
        compositeItemRepository.save(defaultBuilder.build());

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

        assertEquals(1, compositeItemRepository.findAll().size());
    }

    @Test
    public void testUpdateCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();

        CompositeItem compositeToUpdate = compositeItemRepository.save(compositeItem);
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
        CompositeItem compositeItem = compositeItemRepository.save(defaultBuilder.build());

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
        CompositeItem compositeItem = compositeItemRepository.save(defaultBuilder.build());

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

        assertEquals(0, compositeItemRepository.findAll().size());
    }

    @Test
    public void testAddDeviceToCompositeItem() throws Exception {
        CompositeItem compositeItem = defaultBuilder.build();
        Device device = defaultDeviceBuilder.build();

        compositeItem = compositeItemRepository.save(compositeItem);

        device = deviceRepository.save(device);

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

        compositeItem = compositeItemRepository.save(compositeItem);

        device = deviceRepository.save(device);

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
