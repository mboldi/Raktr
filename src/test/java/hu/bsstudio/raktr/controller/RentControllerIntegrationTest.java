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
import hu.bsstudio.raktr.dao.RentDao;
import hu.bsstudio.raktr.model.BackStatus;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.DeviceStatus;
import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import hu.bsstudio.raktr.pdfgeneration.RentPdfRequest;
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
public class RentControllerIntegrationTest {

    private static final String CATEGORY_NAME = "category";
    private static final String LOCATION_NAME = "location";
    private static final String DEVICE_NAME = "device";
    private static final String DEVICE_BARCODE = "device_barcode";
    private static final int DEVICE_WEIGHT = 1000;
    private static final int DEVICE_VALUE = 1200;
    private static final String COMPOSITE_BARCODE = "comp_barcode";
    private static final String COMPOSITE_NAME = "composite";
    public static final String DESTINATION = "destination";
    public static final String EXP_BACK_DATE = "2020.03.01.";
    public static final String ISSUER = "issuer";
    public static final String RENTER = "renter";
    public static final String OUT_DATE = "2020.02.22.";
    public static final String ACT_BACK_DATE = "actback";
    public static final String DESTINATION_2 = "destination2";
    public static final int INVALID_OUT_QUANTITY = 3;
    public static final String RENTER_NAME = "Renter Name";
    public static final String TEAM_LEADER = "Team Leader";
    public static final String RENTER_ID = "112233RN";
    public static final String TEAM_NAME = "Team team";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CompositeItemDao compositeItemDao;

    @Autowired
    private RentDao rentDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocationDao locationDao;

    private Rent.Builder defaultBuilder;
    private Device.Builder defaultDeviceBuilder;
    private CompositeItem.Builder defaultCompositeBuilder;

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
            .withQuantity(2)
            .withSerial("serial")
            .withWeight(DEVICE_WEIGHT)
            .withCategory(category)
            .withLocation(location)
            .withStatus(DeviceStatus.GOOD)
            .withValue(DEVICE_VALUE);

        defaultCompositeBuilder = CompositeItem.builder()
            .withBarcode(COMPOSITE_BARCODE)
            .withLocation(location)
            .withName(COMPOSITE_NAME);

        defaultBuilder = Rent.builder()
            .withDestination(DESTINATION)
            .withExpBackDate(EXP_BACK_DATE)
            .withIssuer(ISSUER)
            .withRenter(RENTER)
            .withOutDate(OUT_DATE)
            .withActBackDate(ACT_BACK_DATE);
    }

    @AfterEach
    public final void after() {
        rentDao.deleteAll();
        compositeItemDao.deleteAll();
        deviceDao.deleteAll();
        categoryDao.deleteAll();
        locationDao.deleteAll();
    }

    @Test
    public void testGetRent() throws Exception {
        rentDao.save(defaultBuilder.build());

        mvc.perform(get("/api/rent")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].destination", is(DESTINATION)));
    }

    @Test
    public void testAddRent() throws Exception {
        Rent rent = defaultBuilder.build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rent);

        requestBody = "{ \"Rent\": " + requestBody + "}";

        mvc.perform(post("/api/rent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.destination", is(DESTINATION)));

        assertEquals(1, rentDao.findAll().size());
    }

    @Test
    public void testUpdateRent() throws Exception {
        Rent rent = defaultBuilder.build();

        Rent rent2 = rentDao.save(rent);
        rent2.setDestination(DESTINATION_2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rent2);

        requestBody = "{ \"Rent\": " + requestBody + "}";

        mvc.perform(put("/api/rent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.destination", is(DESTINATION_2)));

        mvc.perform(get("/api/rent")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].destination", is(DESTINATION_2)));
    }

    @Test
    public void testGetRentById() throws Exception {
        Rent savedRent = rentDao.save(defaultBuilder.build());

        mvc.perform(get("/api/rent/" + savedRent.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.destination", is(DESTINATION)));
    }

    @Test
    public void testGetRentByIdFails() throws Exception {
        mvc.perform(get("/api/rent/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRent() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rent);

        requestBody = "{ \"Rent\": " + requestBody + "}";

        mvc.perform(delete("/api/rent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.destination", is(DESTINATION)));

        assertEquals(0, rentDao.findAll().size());
    }

    @Test
    public void testAddItemToRent() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());
        Device device = deviceDao.save(defaultDeviceBuilder.build());

        RentItem rentItem = RentItem.builder()
            .withOutQuantity(1)
            .withScannable(device)
            .withBackStatus(BackStatus.OUT)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentItem);

        requestBody = "{ \"RentItem\": " + requestBody + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(rent.getId().intValue())))
            .andExpect(jsonPath("$.rentItems", hasSize(1)));
    }

    @Test
    public void testUpdateItemInRent() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());
        Device device = deviceDao.save(defaultDeviceBuilder.build());

        RentItem rentItem = RentItem.builder()
            .withOutQuantity(1)
            .withScannable(device)
            .withBackStatus(BackStatus.OUT)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentItem);

        requestBody = "{ \"RentItem\": " + requestBody + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(rent.getId().intValue())))
            .andExpect(jsonPath("$.rentItems", hasSize(1)));

        rentItem.setOutQuantity(2);

        requestBody = "{ \"RentItem\": " + writer.writeValueAsString(rentItem) + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(rent.getId().intValue())))
            .andExpect(jsonPath("$.rentItems", hasSize(1)))
            .andExpect(jsonPath("$.rentItems[0].outQuantity", is(2)));
    }

    @Test
    public void testRemoveItemFromRent() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());
        Device device = deviceDao.save(defaultDeviceBuilder.build());

        RentItem rentItem = RentItem.builder()
            .withOutQuantity(1)
            .withScannable(device)
            .withBackStatus(BackStatus.OUT)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentItem);

        requestBody = "{ \"RentItem\": " + requestBody + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(rent.getId().intValue())))
            .andExpect(jsonPath("$.rentItems", hasSize(1)));

        rentItem.setOutQuantity(0);

        requestBody = "{ \"RentItem\": " + writer.writeValueAsString(rentItem) + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(rent.getId().intValue())))
            .andExpect(jsonPath("$.rentItems", hasSize(0)));
    }

    @Test
    public void testAddItemToRentFailsTooMany() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());
        Device device = deviceDao.save(defaultDeviceBuilder.build());

        RentItem rentItem = RentItem.builder()
            .withOutQuantity(INVALID_OUT_QUANTITY)
            .withScannable(device)
            .withBackStatus(BackStatus.OUT)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentItem);

        requestBody = "{ \"RentItem\": " + requestBody + "}";

        mvc.perform(put("/api/rent/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isConflict());
    }

    @Test
    public void testGetRentPdf() throws Exception {
        Rent rent = rentDao.save(defaultBuilder.build());

        RentPdfRequest rentPdfRequest = RentPdfRequest.builder()
            .withRenterFullName(RENTER_NAME)
            .withTeamLeaderName(TEAM_LEADER)
            .withRenterId(RENTER_ID)
            .withTeamName(TEAM_NAME)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentPdfRequest);

        requestBody = "{ \"RentPdfRequest\": " + requestBody + "}";

        mvc.perform(post("/api/rent/pdf/" + rent.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    public void testGetRentPdfFailsInvalidId() throws Exception {
        RentPdfRequest rentPdfRequest = RentPdfRequest.builder()
            .withRenterFullName("Renter Name")
            .withTeamLeaderName("Team Leader2")
            .withRenterId("112233RN")
            .withTeamName("Team team")
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(rentPdfRequest);

        requestBody = "{ \"RentPdfRequest\": " + requestBody + "}";

        mvc.perform(post("/api/rent/pdf/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isNotFound());
    }
}
