import java.io.File;
import java.io.IOException;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ImgurApiTest extends BaseApiTest {

    public ImgurApiTest() throws IOException {
    }
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = getBaseUri();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    @DisplayName("Получение информации об аккаунте")
    public void testGetAccountBase() {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
                .expect()
                .body("data.url", is("SchastlivyMax"))
                .log()
                .all()
                .statusCode(200)
                .when()
                .get("3/account/{username}", getUserName());
    }
}