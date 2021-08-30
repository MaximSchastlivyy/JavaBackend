import java.io.File;
import java.io.IOException;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ImgurApiTest extends BaseApiTest {

    private String currentImageHash;
    private String currentDeleteHash;

    public ImgurApiTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = getBaseUri();
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Получение информации об аккаунте")
    void testGetAccountBase() {

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

    @Test
    @DisplayName("Тест загрузки картинки")
    void testImageUpload() throws Exception {

        currentDeleteHash = given()
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .expect()
                .statusCode(200)
                .body("data.id", is(notNullValue()))
                .body("data.deletehash", is(notNullValue()))
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
                .auth()
                .oauth2(getToken())
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
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
                .when()
                .get("3/image/{ImageHash}", currentImageHash)
                .then()
                .statusCode(200)
                .body("data.id", is(notNullValue()))
                .body("data.deletehash", is(notNullValue()))
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест ошибки получения картинки")
    void testGetImageNotFound() throws Exception {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
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
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
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
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
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
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
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
    @DisplayName("Тест добавления картинки в избранное")
    void testFavouriteImage() throws Exception {

        currentImageHash = given()
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
                .when()
                .post("3/image/{imageHash}/favorite", currentImageHash)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .log()
                .all()
                .extract()
                .response();
    }

    @Test
    @DisplayName("Тест добавления несуществующей картинки в избранное")
    void testFavouriteImageNotFound() throws Exception {


        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
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
                .auth()
                .oauth2(getToken())
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

    @Test
    @DisplayName("Тест удаления загруженной картинки")
    void testDeleteImage() throws Exception {

        currentDeleteHash = given()
                .auth()
                .oauth2(getToken())
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("image", new File( "./src/main/resources/1.jpg"))
                .when()
                .post("3/upload")
                .jsonPath()
                .getString("data.deletehash");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth()
                .oauth2(getToken())
                .when()
                .delete("3/image/{imageDeleteHash}", currentDeleteHash)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .log()
                .all()
                .extract()
                .response();
    }
}