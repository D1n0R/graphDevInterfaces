package com.petstore.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.petstore.BaseRequestSpecification;

public class StoreApi {

    private final RequestSpecification request = new BaseRequestSpecification().getSpecification();

    // GET
    public Response getFindByStatus(String status) {
        return request.get("/v2/pet/findByStatus?status=" + status);
    }

    public Response getPetById(long id) {
        return request.get("/v2/pet/" + id);
    }

    // POST
    public Response postPet(String body) {
        return request.body(body).post("/v2/pet");
    }

    // DELETE
    public Response deletePetById(long id) {
        return request.delete("/v2/pet/" + id);
    }
}