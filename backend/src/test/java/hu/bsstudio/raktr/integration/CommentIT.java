package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;

@Sql("/comment/test-data.sql")
public class CommentIT extends RaktrIT {

    @Test
    void testDeleteCommentAsAdmin() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/comment/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM comments WHERE id = 1")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteOwnComment() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/comment/2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM comments WHERE id = 2")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteOtherUserComment() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/comment/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM comments WHERE id = 1")
                .assertRowCount()
                .isEqualTo(1);
    }

}
