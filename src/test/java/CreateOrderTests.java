import Order.Order;
import Order.OrderClient;
import User.User;
import User.UserClient;
import User.UserGeneration;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateOrderTests {
    private Order order;
    private final OrderClient orderClient = new OrderClient();
    private final User randomUser = UserGeneration.createRandomUser();
    private final UserClient userClient = new UserClient();
    private final Faker faker = new Faker();
    private String accessToken;
    private int actualStatusCode;
    private String actualMessage;

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        ValidatableResponse userResponse = UserClient.createUser(randomUser);
        ValidatableResponse response = orderClient.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[0]._id"));
        order = new Order(ingredients);
        accessToken = userResponse.extract().path("accessToken");
        ValidatableResponse responseOrder = orderClient.createOrderByAuthorization(accessToken, order);
        actualStatusCode = responseOrder.extract().statusCode();
        Assert.assertEquals(SC_OK, actualStatusCode);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        ValidatableResponse userResponse = UserClient.createUser(randomUser);
        List<String> ingredients = new ArrayList<>();
        order = new Order(ingredients);
        ValidatableResponse response = orderClient.createOrder(order);
        actualStatusCode = response.extract().statusCode();
        actualMessage = response.extract().path("message");
        accessToken = userResponse.extract().path("accessToken");
        assertEquals(SC_BAD_REQUEST, actualStatusCode);
        assertEquals("Ingredient ids must be provided", actualMessage);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        ValidatableResponse response = orderClient.getIngredients();
        List<String> ingredients = new ArrayList<>();
        ingredients.add(response.extract().path("data[0]._id"));
        Order order = new Order(ingredients);
        ValidatableResponse createResponse = orderClient.createOrderWithoutAuthorization(order);
        actualStatusCode = createResponse.extract().statusCode();
        boolean isSuccess = response.extract().path("success");
        assertEquals(SC_OK, actualStatusCode);
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithWrongHashTest() {
        ValidatableResponse userResponse = UserClient.createUser(randomUser);
        List<String> ingredients = List.of(faker.random().hex(5), faker.random().hex(1),faker.random().hex(6));
        order = new Order(ingredients);
        ValidatableResponse response = orderClient.createOrder(order);
        actualStatusCode = response.extract().statusCode();
        accessToken = userResponse.extract().path("accessToken");
        assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

}
