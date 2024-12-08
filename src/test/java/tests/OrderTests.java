package tests;

import api.RequestAPI;
import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.OrderLombok;
import org.junit.Test;
import static org.hamcrest.Matchers.notNullValue;

public class OrderTests {
    private RequestAPI requestAPI;

    @Test
    @DisplayName("Check creation of orders with different colors")
    public void createOrdersWithDifferentColorsTest() {
        requestAPI = new RequestAPI();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Массивы цветов для тестов
        String[][] colorsArray = {
                {"BLACK"},
                {"GREY"},
                {"BLACK", "GREY"},
                {}
        };

        for (String[] color : colorsArray) {
            System.out.println("Testing with colors: " + String.join(", ", color));

            // Создаём заказ с текущими цветами
            OrderLombok order = new OrderLombok(
                    "Иван",
                    "Иванов",
                    "Божедомка, 7",
                    "Комсомольская",
                    "81234567890",
                    5,
                    "2024-12-20",
                    "Заплачу чаевые",
                    color
            );

            Allure.step("Make order");
            // вызываем метод отправки запроса
            ValidatableResponse response = requestAPI.sendOrderRequest(order, "/api/v1/orders", "post");
            // проверка
            response.log().all()
                    .assertThat().statusCode(201)
                    .and().body("track", notNullValue());

            // удаляем (отменяем) заказ после теста
            Allure.step("Delete order after test");
            int trackValue = response.extract().path("track");
            // Логируем track
            System.out.println("Track value: " + trackValue);
            // запрос на отмену
            ValidatableResponse responseDelete = requestAPI.sendOrderDeleteRequest(trackValue, "/api/v1/orders/cancel", "put");
        }
    }

    @Test
    @DisplayName("Check return list of orders")
    public void returnListOfOrdersTest() {
        requestAPI = new RequestAPI();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        // вызываем метод отправки запроса
        // запрос на 5 доступных заказов. Если посылать пустой запрос - заказов очень много и сервер очень долго отвечает.
        Allure.step("Request to list of orders (5)");
        ValidatableResponse response = requestAPI.sendListOfOrdersRequest("/api/v1/orders?limit=5&page=0&nearestStation=[\"110\"]", "get");
        // проверка
        response.log().all()
                .assertThat().statusCode(200)
                .and().body(notNullValue());
    }
}