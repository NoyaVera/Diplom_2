package User;

import com.github.javafaker.Faker;

public class UserGeneration {
    private final static Faker faker = new Faker();

    public static User createRandomUser() {
        return new User(faker.internet().emailAddress(), faker.name().firstName(), faker.internet().password(6,20));
    }

    public static User createRandomUserWithoutName() {
        return new User(faker.internet().emailAddress(), null, faker.internet().password(6,20));
    }
}
