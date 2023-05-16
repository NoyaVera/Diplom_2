package Order;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    private static final String GET_INGREDIENTS = "api/ingredients";
    private static final String ORDER = "api/orders";

    protected static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }

    @Step("Создаем заказ")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post(ORDER)
                .then();
    }

    @Step("Создаем заказ с авторизацией")
    public ValidatableResponse createOrderByAuthorization(String accessToken, Order order) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .post(ORDER)
                .then();
    }

    @Step("Создаем заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .post(ORDER)
                .then();
    }

    @Step("Получаем список ингридиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }

    @Step("Получаем заказ с авторизацией")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .get(ORDER)
                .then();
    }

    @Step("Получаем заказ без авторизации")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getSpec())
                .get(ORDER)
                .then();
    }
}
