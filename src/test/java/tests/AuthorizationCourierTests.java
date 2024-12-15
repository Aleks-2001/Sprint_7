package tests;

import api.RequestAPI;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthorizationCourierTests {

    private Courier courier;
    private RequestAPI requestAPI;
    String loginForAfterMethod;
    String passwordForAfterMethod;

    @Before
    @Step("before setUp")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = CourierGenerator.getRandomCourierWithoutFirstName("batman_", "pass_");
        requestAPI = new RequestAPI();
        // сохраняем в дополнительных переменных login и password
        loginForAfterMethod = courier.getLogin();
        passwordForAfterMethod = courier.getPassword();
        // запрос для создания курьера
        requestAPI.sendRequest(courier, "/api/v1/courier", "post");
    }

    @After
    @Step("after cleanUp")
    public void cleanUp() {
        // возвращаем курьеру параметры, удаленные или измененные в ходе тестов, для корректного удаления курьера
        courier.setLogin(loginForAfterMethod);
        courier.setPassword(passwordForAfterMethod);
        Allure.step("Check ID");
        String idValue = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post")
                .extract()
                .response()
                .jsonPath()
                .getString("id");
        // логируем id
        System.out.println("id = " + idValue);

        if (idValue != null) {
            Allure.step("Delete, if \"id\" is not null");
            requestAPI.sendCourierDeleteRequest(idValue);
            // логируем успешное удаление
            System.out.println("Delete OK");
        }
    }

    @Test
    @Step("Check authorization courier")
    public void authorizationCourierTest() {
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("id", notNullValue());
        // вывод на экран "id" при успешной авторизации
        Allure.step("Checking return \"id\"");
        System.out.println(response.extract().body().asString());
    }

    @Test
    @Step("Check authorization courier without login")
    public void authorizationCourierWithoutLoginTest() {
        // удаляем у курьера параметр login
        courier.setLogin(null);
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(400)
                .and().body("message",  equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Check authorization courier without password")
    public void authorizationCourierWithoutPasswordTest() {
        // удаляем у курьера параметр password
        courier.setPassword(null);
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(400)
                .and().body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    @Step("Check authorization courier with another login")
    public void authorizationCourierWithAnotherLoginTest() {
        // меняем у курьера параметр login
        courier.setLogin("anotherLogin");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(404)
                .and().body("message",  equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Check authorization courier with another password")
    public void authorizationCourierWithAnotherPasswordTest() {
        // меняем у курьера параметр password
        courier.setPassword("anotherPass");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(404)
                .and().body("message",  equalTo("Учетная запись не найдена"));
    }

}
