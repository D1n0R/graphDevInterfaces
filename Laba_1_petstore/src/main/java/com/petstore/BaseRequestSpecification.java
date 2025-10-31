package com.petstore;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseRequestSpecification {
    public RequestSpecification getSpecification() {
        RequestSpecification request = RestAssured.given();
        request.baseUri("https://petstore.swagger.io");
        request.header("accept", "application/json");
        request.header("Content-Type", "application/json");
        return request;
    }

}