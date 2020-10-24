package com.caponetto.resource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import com.caponetto.model.ImageDescriptor;
import com.caponetto.model.ImageItem;
import com.caponetto.model.ImageRequest;
import com.caponetto.utils.ImageUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ImageResourceTest {

    @Test
    void testClassifySuccess() {
        final String filePath = getPathFromTestFile("cat.jpg");
        final ImageRequest imageRequest = new ImageRequest(filePath, 1, 50);
        final List<ImageItem> items = Stream.of(new ImageItem("tabby, tabby cat",
                                                              57,
                                                              null)).collect(Collectors.toList());
        final ImageDescriptor imageDescriptor = new ImageDescriptor(filePath, items);

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(imageRequest).toString())
                .post(ImageResource.CLASSIFY_PATH)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(equalTo(Status.OK.getStatusCode()))
                .body(is(JsonObject.mapFrom(imageDescriptor).toString()));
    }

    @Test
    void testClassifyInvalidPath() {
        final ImageRequest imageRequest = new ImageRequest("an/invalid/path", 1, 50);

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(imageRequest).toString())
                .post(ImageResource.CLASSIFY_PATH)
                .then()
                .statusCode(equalTo(Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    void testDetectSuccess() {
        final String filePath = getPathFromTestFile("cat.jpg");
        final ImageRequest imageRequest = new ImageRequest(filePath, 1, 50);

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(imageRequest).toString())
                .post(ImageResource.DETECT_PATH)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(equalTo(Status.OK.getStatusCode()))
                .body("path", containsString(ImageUtils.TEMP_SUFFIX));
    }

    @Test
    void testDetectInvalidPath() {
        final ImageRequest imageRequest = new ImageRequest("an/invalid/path", 1, 50);

        given()
                .contentType(ContentType.JSON)
                .body(JsonObject.mapFrom(imageRequest).toString())
                .post(ImageResource.DETECT_PATH)
                .then()
                .statusCode(equalTo(Status.BAD_REQUEST.getStatusCode()));
    }

    private String getPathFromTestFile(final String filename) {
        return new File(getClass()
                                .getClassLoader()
                                .getResource(filename)
                                .getFile())
                .getAbsolutePath();
    }
}