package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/category/test-data.sql")
public class CategoryIT extends RaktrIT {

    @Test
    void testListCategories() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/category")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/category/list-response.json"));
    }

    @Test
    void testCreateCategory() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/category/create-request.json"))
                .when()
                .post("/v1/category")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/category/create-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM categories WHERE name = 'test-category-99'")
                .assertRowAsJson()
                .excluding("created_at", "updated_at")
                .equalTo(loadFileContent("/category/create-db.json"));
    }

    @Test
    void testCreateCategoryAlreadyExistsError() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/category/create-already-exists-error-request.json"))
                .when()
                .post("/v1/category")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/category/create-already-exists-error-response.json"));
    }

    @Test
    void testDeleteCategory() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/category/test-category-2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM categories WHERE name = 'test-category-2'")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteCategoryNotEmptyError() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/category/test-category-1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/category/delete-not-empty-error-response.json"));

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM categories WHERE name = 'test-category-1'")
                .assertRowCount()
                .isEqualTo(1);
    }

}
