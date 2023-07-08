package Chapters.Chapter01;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class FirstChapterTests {

    private static final String BASE_URL = "http://petstore.swagger.io/v2";

    // In this test, we are going to test a GET API and validate the Response
    // https://petstore.swagger.io/v2/pet/findByStatus?status=sold
    @Test
    public void sendAGETRequestAndValidateResponse() {
        given()
                .queryParam("status", "sold") // Filtering the data based on
                .when()
                .get(BASE_URL + "/pet/findByStatus") // This will get the data from the given URL
                .then()
                .statusCode(200) // It verify the actual response code with the given code
                .body("[0].id", notNullValue()) // Checking whether value is Not_Null or not
                .body("[0].category.name", notNullValue())
                .body("[0].status", equalTo("sold")); // Checking status is equal to "sold"
    }

    // Here we testing the GET API, by passing the query parameters in the URL
    @Test
    public void sendAGETRequestByPassingQueryParameterInURL() {
        given()
                .when()
                .get(BASE_URL + "/pet/findByStatus?status=sold") // Passing query parameters in URL itself
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].category.name", notNullValue())
                .body("[0].status", equalTo("sold"));
    }

    // In this test we are going to extract single value from the response and assigning the response to a string observe extract
    @Test
    public void sendAGETRequestAndRetrieveValueFromBody() {
        String status = given()
                .queryParam("status", "sold")
                .when()
                .get(BASE_URL + "/pet/findByStatus")
                .then()
                .extract()
                .path("[0].status"); // Extracting the value from JSON and returning it

        if (status == null)
            throw new RuntimeException("Status is Empty!!!");
        
        
        
    }

    // This test will store the entire Response into Response Object
    @Test
    public void sendAGETRequestAndStoreTheResponse() {
        Response response = given()
                .queryParam("status", "sold")
                .when()
                .get(BASE_URL + "/pet/findByStatus"); // Finally it returns the Response

        Assert.assertEquals(response.getStatusCode(), 200);
    }
    
    
    @Test
    public void inspectAllNodesInResponse() {
        String response = given()
                .when()
                .get(BASE_URL + "/pet/findByStatus?status=sold")
                .then()
                .extract()
                .asString();

        JsonPath jsonPath = new JsonPath(response);

        // Assertions using Hamcrest
        assertThat(jsonPath.get("[0].id"), notNullValue());
        assertThat(jsonPath.get("[0].category.name"), notNullValue());
        assertThat(jsonPath.get("[0].status"), equalTo("sold"));
    }
    
    @Test
    public void testPetstoreAPI() {
        RestAssured.baseURI = "http://petstore.swagger.io/v2";

        // Get pets by status
        String response = given()
                .when()
                .get("/pet/findByStatus?status=available")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        // Parse the response using JsonPath
        JsonPath jsonPath = new JsonPath(response);

        // Extract values using JSONPath expressions
        int numberOfPets = jsonPath.getList("$").size();
        String firstPetName = jsonPath.getString("[0].name");
        String firstPetStatus = jsonPath.getString("[0].status");
        int petId = jsonPath.getInt("[0].id");

        
     // Debug statements
        System.out.println("Number of Pets: " + numberOfPets);
        System.out.println("First Pet Name: " + firstPetName);
        System.out.println("First Pet Status: " + firstPetStatus);
        System.out.println("Pet ID: " + petId);
        
        // Assertions using Hamcrest
        //assertThat(petId, greaterThan(0));
        assertThat(firstPetName, notNullValue());
        assertThat(firstPetStatus, equalTo("available"));

        // Other JSONPath usages
        String[] petNames = jsonPath.get("*.name");

        // Additional assertions using Hamcrest
        assertThat(petNames.length, equalTo(numberOfPets));
    }
}