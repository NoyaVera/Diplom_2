import User.User;
import User.UserClient;
import User.UserCredentials;
import User.UserGeneration;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class ChangeUserTests {
    private final UserClient userClient = new UserClient();
    private final User randomUser = UserGeneration.createRandomUser();
    private int actualStatusCode;
    private String accessToken;

    @Test
    @DisplayName("Меняем данные юзера")
    public void changeUserDataTest() {
        ValidatableResponse createResponse = UserClient.createUser(randomUser);
        accessToken = createResponse.extract().path("accessToken");
        ValidatableResponse response = UserClient.changeUserData(randomUser, accessToken);
        randomUser.setEmail("newEmail");
        randomUser.setPassword("newPassword");
        randomUser.setName("newName");
        actualStatusCode = response.extract().statusCode();
        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertEquals("newEmail", randomUser.getEmail());
        Assert.assertEquals("newPassword", randomUser.getPassword());
        Assert.assertEquals("newName", randomUser.getName());
    }

    @Test
    @DisplayName("Меняем данные юзера без авторизации")
    public void changeUserDataWithoutAuthTest() {
        String newEmail = randomUser.getEmail() + "newEmail";
        randomUser.setName(newEmail);
        ValidatableResponse responseUpdate = userClient.changeUserDataWithoutAuth(randomUser);
        String actualEmail = responseUpdate.extract().path("user.name");
        actualStatusCode = responseUpdate.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        Assert.assertNotEquals(newEmail, actualEmail);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
