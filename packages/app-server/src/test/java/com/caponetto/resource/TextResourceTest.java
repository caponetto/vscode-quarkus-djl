package com.caponetto.resource;

import javax.ws.rs.core.Response;

import com.caponetto.model.text.TextRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class TextResourceTest extends BaseTest {

    @Test
    void testGetModels() {
        given()
                .get(TextResource.BASE_PATH + TextResource.MODELS_PATH)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(equalTo(Response.Status.OK.getStatusCode()))
                .body("size()", is(1));
    }

    @Test
    void testSentimentPositive() {
        final TextRequest textRequest = new TextRequest("The cat loves tuna and salmon!");

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(textRequest).toString())
                .post(TextResource.BASE_PATH + TextResource.SENTIMENT_PATH)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(equalTo(Response.Status.OK.getStatusCode()))
                .body("positive", equalTo(true));
    }

    @Test
    void testSentimentNegative() {
        final TextRequest textRequest = new TextRequest("The cat hates taking a bath in the shower.");

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(textRequest).toString())
                .post(TextResource.BASE_PATH + TextResource.SENTIMENT_PATH)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(equalTo(Response.Status.OK.getStatusCode()))
                .body("positive", equalTo(false));
    }

    @Test
    void testEmptyText() {
        final TextRequest textRequest = new TextRequest("");

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(textRequest).toString())
                .post(TextResource.BASE_PATH + TextResource.SENTIMENT_PATH)
                .then()
                .statusCode(equalTo(Response.Status.BAD_REQUEST.getStatusCode()));
    }
}
