package tests;

import api.RequestAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierGenerator;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class AuthorizationNonExistingCourierTest {
    private Courier courier;
    private RequestAPI requestAPI;

    @Test
    @DisplayName("Check authorization of a non-existent user")
    public void authorizationNonExistUserTest() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        requestAPI = new RequestAPI();
        // создаем несуществующего курьера
        courier = CourierGenerator.getRandomCourierWithoutFirstName("exist_", "existpass_");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));
    }
}
