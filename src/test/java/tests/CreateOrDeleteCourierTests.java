package tests;

import api.RequestAPI;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class CreateOrDeleteCourierTests {
    private Courier courier;
    private RequestAPI requestAPI;



    @Before
    @Step("before setUp")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = CourierGenerator.getRandomCourier("robocop_", "pass_", "alex");
        requestAPI = new RequestAPI();
    }

    @After
    @Step("after cleanUp")
    public void cleanUp() {
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
            ValidatableResponse response = requestAPI.sendCourierDeleteRequest(idValue);
            // логируем успешное удаление
            System.out.println("Delete OK");
        }
    }

    @Test
    @DisplayName("Check of create new courier")
    public void createNewCourierTest() {
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(201)
                .and().body("ok", equalTo(true));
    }



    @Test
    @DisplayName("Check the creating second the same courier")
    public void createTheSameCourierTest() {
        Allure.step("Create first courier");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(201)
                .and().body(notNullValue());

        Allure.step("try create second courier");
        // вызываем метод отправки запроса
        ValidatableResponse secondResponse = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        secondResponse.log().all()
                .assertThat().statusCode(409)
             // тест должен был упасть, так как в документации фраза "Этот логин уже используется",
             // а в ответе сервера "Этот логин уже используется. Попробуйте другой."
                .and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(secondResponse.extract().response().jsonPath().getString("message"));
    }


    @Test
    @DisplayName("check the creating a courier with the same Login")
    // Создание курьера с логином, который уже есть, но с другими паролем и именем
    public void createCourierWithTheSameLoginTest() {
        Allure.step("Create first courier");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(201)
                .and().body(notNullValue());

        Allure.step("try create second courier with the same login");
        String pass = courier.getPassword();
        String name = courier.getFirstName();
        courier.setPassword("anotherPass");
        courier.setFirstName("anotherFirstName");
        // вызываем метод отправки запроса
        ValidatableResponse secondResponse = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        secondResponse.log().all()
                .assertThat().statusCode(409)
                // тест должен был упасть, так как в документации фраза "Этот логин уже используется",
                // а в ответе сервера "Этот логин уже используется. Попробуйте другой."
                .and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        System.out.println(secondResponse.extract().response().jsonPath().getString("message"));

        // теперь возвращаем курьеру прежние пароль и имя, чтобы метод @After смог его удалить.
        courier.setPassword(pass);
        courier.setFirstName(name);
    }



    @Test
    @DisplayName("Check of create new courier without login")
    public void createNewCourierWithoutLoginTest() {
        String loginValue = courier.getLogin();
        // Создаем экземпляр курьера без имени
        courier.setLogin(null);
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(400)
                .and().body("message", equalTo("Недостаточно данных для создания учетной записи"));

        // восстанавливаем login c целью избежать возможных ошибок далее в методе @After
        courier.setLogin(loginValue);
    }


    @Test
    @DisplayName("Check of create new courier without password")
    public void createNewCourierWithoutPasswordTest() {
        String passwordValue = courier.getPassword();
        // Создаем экземпляр курьера без имени
        courier.setPassword(null);
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(400)
                .and().body("message", equalTo("Недостаточно данных для создания учетной записи"));
        // восстанавливаем password c целью избежать возможных ошибок далее в методе @After
        courier.setPassword(passwordValue);
    }



    @Test
    @DisplayName("Check delete of courier")
    public void deleteCourierTest() {
        Allure.step("Create new courier");
        ValidatableResponse response = requestAPI.sendRequest(courier, "/api/v1/courier", "post");

        Allure.step("Check ID");
        String idValue = requestAPI.sendRequest(courier, "/api/v1/courier/login", "post")
                .extract()
                .response()
                .jsonPath()
                .getString("id");
        System.out.println("id = " + idValue);
            ValidatableResponse secondResponse = requestAPI.sendCourierDeleteRequest(idValue);
        // проверка
        secondResponse.log().all()
                .assertThat().statusCode(200)
                .and().body("ok", equalTo(true));
        }

}
