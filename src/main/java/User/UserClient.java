package User;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    private static final String REGISTER_PATH = "api/auth/register";
    private static final String AUTH_PATH = "api/auth/login";
    private static final String USER_DATA_PATH = "api/auth/user";

    protected static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }

    @Step("Создаем нового юзера")
    public static ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Авторизируемся")
    public static ValidatableResponse authUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(AUTH_PATH)
                .then();
    }

    @Step("Меняем данные юзера")
    public static ValidatableResponse changeUserData(User user, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER_DATA_PATH)
                .then();
    }

    @Step("Меняем данные неавторизированного юзера")
    public ValidatableResponse changeUserDataWithoutAuth(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .patch(USER_DATA_PATH)
                .then();
    }

    @Step("Удаляем ранее созданного юзера")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_DATA_PATH)
                .then();
    }
}
