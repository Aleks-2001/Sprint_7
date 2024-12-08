package api;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.OrderLombok;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

public class RequestAPI {

    @Step("Send {method} request to {api} with courier: {courier}")
    public ValidatableResponse sendRequest(Courier courier, String api, String method) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .body(courier)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request(method, api)
                .then();
    }

    @Step("Send CourierDelete request")
    public ValidatableResponse sendCourierDeleteRequest(String idValue) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .body("{\"id\": " + idValue + "}") // Подставляем JSON с id
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request("delete", "/api/v1/courier/" + idValue)
                .then();
    }

    @Step("Send Order request")
    public ValidatableResponse sendOrderRequest(OrderLombok orderLombok, String api, String method) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .body(orderLombok)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request(method, api)
                .then();
    }


    @Step("Send OrderDelete request")
    public ValidatableResponse sendOrderDeleteRequest(int trackValue, String api, String method) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .body("{\"track\": " + trackValue + "}") // Подставляем JSON с треком
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request(method, api)
                .then();
    }

    @Step("Send request  'get list of orders'")
    public ValidatableResponse sendListOfOrdersRequest(String api, String method) {

        return given()
                .header("Content-type", "application/json")
                .when()
                .body("")
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request(method, api)
                .then();
    }
}
