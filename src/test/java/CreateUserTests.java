import User.User;
import User.UserClient;
import User.UserGeneration;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateUserTests {
    private final UserClient userClient = new UserClient();
    private final User randomUser = UserGeneration.createRandomUser();
    private final User randomUserWithoutName = UserGeneration.createRandomUserWithoutName();
    private int actualStatusCode;
    private String actualMessage;
    private String accessToken;


    @Test
    @DisplayName("Создаем уникального пользователя")
    public void createNewUserTest() {
        ValidatableResponse createResponse = UserClient.createUser(randomUser);
        actualStatusCode = createResponse.extract().statusCode();
        accessToken = createResponse.extract().path("accessToken");
        Assert.assertEquals(actualStatusCode, SC_OK);
    }

    @Test
    @DisplayName("Cоздаем пользователя без поля name")
    public void createUserWithoutNameTest() {
        ValidatableResponse createResponse = UserClient.createUser(randomUserWithoutName);
        actualStatusCode = createResponse.extract().statusCode();
        actualMessage = createResponse.extract().path("message");
        accessToken = createResponse.extract().path("accessToken");
        Assert.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assert.assertEquals("Email, password and name are required fields", actualMessage);
    }

    @Test
    @DisplayName("Cоздаем ранее зарегистрированного пользователя")
    public void createRepeatUserTest() {
        UserClient.createUser(randomUser);
        User twinUser = new User(randomUser.getEmail(), randomUser.getPassword(), randomUser.getName());
        ValidatableResponse createResponse = UserClient.createUser(twinUser);
        actualStatusCode = createResponse.extract().statusCode();
        actualMessage = createResponse.extract().path("message");
        accessToken = createResponse.extract().path("accessToken");
        Assert.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assert.assertEquals("User already exists", actualMessage);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
