package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;

@Sql("/test-users.sql")
@Sql("/comment/test-data.sql")
public class CommentIT extends RaktrIT {

    @Test
    void testDeleteComment() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/comment/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM comments WHERE id = 1")
                .assertRowsAsJson()
                .equalTo("[]");
    }

}
