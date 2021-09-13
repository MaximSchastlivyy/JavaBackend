import java.io.File;
import java.io.IOException;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ImgurApiTest extends BaseApiTest {

    private String currentImageHash;
    private String currentDeleteHash;

    public ImgurApiTest() throws IOException {
    }

    RequestSpecification requestSpecification = null;
    ResponseSpecification responseSpecification = null;
    ResponseSpecification responseSpecificationWithBodyNotNullExpect = null;
    ResponseSpecification responseSpecificationWithBodySuccessExpect = null;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = getBaseUri();

        requestSpecification = new RequestSpecBuilder()
                .setAuth(oauth2(getToken()))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.ANY)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .expectHeader("Access-Control-Allow-Credentials", "true")
                .build();

        responseSpecificationWithBodyNotNullExpect = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody("data.id", is(notNullValue()))
                .expectBody("data.deletehash", is(notNullValue()))
                .build();

        responseSpecificationWithBodyNotNullExpect = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody("success", is(true))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Получение информации об аккаунте")
    void testGetAccountBase() {

        given()
                .spec(requestSpecification)
                .expect()
                .body("data.url", is("SchastlivyMax"))
                .log()
                .all()
                .spec(responseSpecification)
                .when()
                .get("3/account/{username}", getUserName());
    }

    @Test
    @DisplayName("Тест загрузки картинки")
    void testImageUpload() throws Exception {

        currentDeleteHash = given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .expect()
                .spec(responseSpecificationWithBodyNotNullExpect)
                .log()
                .all()
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

    }

    @Test
    @DisplayName("Тест ошибки загрузки картинки")
    void testNegativeImageUpload() throws Exception {

         given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .expect()
                .statusCode(400)
                .log()
                .all()
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @DisplayName("Тест получения загруженной картинки")
    void testGetImage() throws Exception {

        currentImageHash = given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.id");

        given()
                .spec(requestSpecification)
                .when()
                .get("3/image/{ImageHash}", currentImageHash)
                .then()
                .spec(responseSpecificationWithBodyNotNullExpect)
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест ошибки получения картинки")
    void testGetImageNotFound() throws Exception {

        given()
                .spec(requestSpecification)
                .when()
                .get("3/image/{ImageHash}", "1111111")
                .then()
                .statusCode(404)
                .log()
                .all();
    }

    @Test
    @DisplayName("Тест ошибки получения картинки")
    void testGetImageBadRequest() throws Exception {

        given()
                .spec(requestSpecification)
                .when()
                .get("3/image/")
                .then()
                .statusCode(400)
                .log()
                .all();
    }

    @Test
    @DisplayName("Тест обновления информации о загруженной картинки")
    void testUpdateImage() throws Exception {

        currentDeleteHash = given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

        given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("title","Heart")
                .when()
                .post("3/image/{imageDeleteHash}", currentDeleteHash)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест неудачного обновления информации о загруженной картинки")
    void testNegativeUpdateImage() throws Exception {

        currentDeleteHash = given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

        given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("","")
                .when()
                .post("3/image/{imageDeleteHash}", "0000000")
                .then()
                .statusCode(404)
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест добавления несуществующей картинки в избранное")
    void testFavouriteImageNotFound() throws Exception {


        given()
                .spec(requestSpecification)
                .when()
                .post("3/image/{imageHash}/favorite", "0000000")
                .then()
                .statusCode(404)
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест добавления картинки в избранное неавторизированным пользователем")
    void testFavouriteImageUnAuth() throws Exception {

        currentImageHash = given()
                .spec(requestSpecification)
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.id");

        given()
                .when()
                .post("3/image/{imageHash}/favorite", currentImageHash)
                .then()
                .statusCode(401)
                .log()
                .all()
                .extract()
                .response();
    }
}