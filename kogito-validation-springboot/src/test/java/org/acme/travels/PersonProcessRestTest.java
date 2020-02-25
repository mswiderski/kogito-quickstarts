package org.acme.travels;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class PersonProcessRestTest {

    // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }
    
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
