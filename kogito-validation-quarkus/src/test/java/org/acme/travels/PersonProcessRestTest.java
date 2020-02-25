package org.acme.travels;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class PersonProcessRestTest {

    
    @Test
    public void testValidPersonRequest() {

        // test adding new person
        String addPayload = "{\"person\" : {\"name\" : \"john\", \"age\" : 3, \"email\" : \"user@email.com\"}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test to ensure no instances are present
        given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId).then()
        .statusCode(200).body("id", is(firstCreatedId));
        
        given().accept(ContentType.JSON).when().delete("/persons/" + firstCreatedId).then().statusCode(200);

    }
    
    @Test
    public void testInvalidPersonRequest() {

        // test adding new person
        String addPayload = "{\"person\" : {\"name\" : \"john\", \"age\" : 3, \"email\" : \"user\"}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPayload).when()
                .post("/persons").then().statusCode(400);

    }
}
