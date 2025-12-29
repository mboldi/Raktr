package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

@Sql("/category/test-data.sql")
public class CategoryIT extends RaktrIT {

    @Test
    void testListCategories() {
        var response = get("/v1/category")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/category/list-response.json"));
    }

    @Test
    void testCreateCategory() {
        var response = given()
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

        databaseQueryHelper.queryDatabase("SELECT * FROM category WHERE name = 'test-category-99'")
                .assertRowAsJson()
                .excluding("created_at", "updated_at")
                .equalTo(loadFileContent("/category/create-db.json"));
    }

    @Test
    void testCreateCategoryAlreadyExistsError() {
        var response = given()
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

    @Disabled // TODO: Fix after full refactor
    @Test
    void testDeleteCategory() {
        var response = delete("/v1/category/test-category-1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract()
                .asString();
    }

    @Disabled // TODO: Fix after full refactor
    @Test
    void testDeleteCategoryNotEmptyError() {
        var response = delete("/v1/category/test-category-1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();
    }

}
