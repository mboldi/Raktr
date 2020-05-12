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
import hu.bsstudio.raktr.model.Category;
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
@ActiveProfiles("integrationtest")
public class CategoryControllerIntegrationTest {

    private static final String CATEGORY_NAME = "category";
    public static final String CATEGORY_NAME_2 = "category2";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryDao categoryDao;

    @BeforeEach
    public final void init() {
        categoryDao.deleteAll();
    }

    @AfterEach
    public final void after() {
        categoryDao.deleteAll();
    }

    @Test
    public void testGetCategory() throws Exception {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        categoryDao.save(category);

        mvc.perform(get("/api/category")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name", is(CATEGORY_NAME)));
    }

    @Test
    public void testCreateCategory() throws Exception {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(category);

        requestBody = "{ \"Category\": " + requestBody + "}";

        mvc.perform(post("/api/category")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(CATEGORY_NAME)));

        mvc.perform(get("/api/category")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(CATEGORY_NAME)));
    }

    @Test
    public void testCreateCategoryFailsNameNotUnique() throws Exception {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(category);

        requestBody = "{ \"Category\": " + requestBody + "}";

        mvc.perform(post("/api/category")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(CATEGORY_NAME)));

        String finalRequestBody = requestBody;
        assertThrows(NestedServletException.class, () ->
            mvc.perform(post("/api/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(finalRequestBody))
        );
    }

    @Test
    public void testUpdateCategory() throws Exception {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        Category category2 = categoryDao.save(category);

        category2.setName(CATEGORY_NAME_2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

        String updateBody = writer.writeValueAsString(category2);
        updateBody = "{ \"Category\": " + updateBody + "}";

        mvc.perform(put("/api/category")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(CATEGORY_NAME_2)));

        mvc.perform(get("/api/category")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name", is(CATEGORY_NAME_2)));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Category category = Category.builder()
            .withName(CATEGORY_NAME)
            .build();

        category = categoryDao.save(category);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestBody = writer.writeValueAsString(category);

        requestBody = "{ \"Category\": " + requestBody + "}";

        mvc.perform(delete("/api/category")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(CATEGORY_NAME)));

        mvc.perform(get("/api/category")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
    }


}
