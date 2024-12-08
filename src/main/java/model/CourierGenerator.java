package model;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class CourierGenerator {
@Step("Generate random courier")
    public static Courier getRandomCourier(String loginParam, String passwordParam, String firstNameParam) {
        String login = loginParam + RandomStringUtils.randomAlphabetic(4);
        String password = passwordParam + RandomStringUtils.randomAlphabetic(4);
        String firstName = firstNameParam + RandomStringUtils.randomAlphabetic(4);
        return new Courier(login, password, firstName);
    }

    @Step("Generate random courier without firstName")
    public static Courier getRandomCourierWithoutFirstName(String loginParam, String passwordParam) {
        String login = loginParam + RandomStringUtils.randomAlphabetic(4);
        String password = passwordParam + RandomStringUtils.randomAlphabetic(4);
        return new Courier(login, password);
    }

}
