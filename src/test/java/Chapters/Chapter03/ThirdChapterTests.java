package Chapters.Chapter03;

import builders.CategoryBuilder;
import builders.CreatePetRequestBuilder;
import builders.TagsBuilder;
import entities.requests.Category;
import entities.requests.CreatePetRequest;
import entities.requests.Tags;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.BaseTest;
import utils.RequestHelper;
import utils.ResourceHelper;
import static io.restassured.RestAssured.given;
public class ThirdChapterTests extends BaseTest {

    @Test
    public void sendAPOSTRequestAndValidateTheResponseCode() {

        // Creating the Category Object
        Category category = new CategoryBuilder()
                .withId(1)
                .withName("Cats")
                .build();

        // Creating the Tags Object
        Tags tags = new TagsBuilder()
                .withId(1)
                .withName("Tag 1")
                .build();

        // Inserting the above created Tags object in the TagsList, because main object accepts as an array
        Tags[] tagsList = new Tags[1];
        tagsList[0] = tags;

        // Creating the Main Object - CreatePetRequest
        String[] photoUrls = {"Photo Url"}; // Create an array with some URL's
        CreatePetRequest createPetRequest = new CreatePetRequestBuilder()
                .withCategory(category)
                .withTags(tagsList)
                .withPhotoUrls(photoUrls)
                .withId(get3DigitRandomInt()) // This `get3DigitRandomInt()` will generate the random 3 digit number, coming from BaseTest
                .withName("Testing + " + get3DigitRandomInt())
                .withStatus("available")
                .build();

        // Sending a Request
        String url = propertiesReader.getEndPointUrl("create_pet"); // Fetching url from Properties file
        String json = RequestHelper.getJsonString(createPetRequest); // Convert above created object into a String
       // Response response = ResourceHelper.create(url, json);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(url);
        
        // Handle Redirection
        if (response.getStatusCode() == 301 || response.getStatusCode() == 302) {
            String redirectedUrl = response.getHeader("Location");
            response = given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post(redirectedUrl);
        }
        
        // Validating the Response Code
        Assert.assertEquals(response.getStatusCode(), 200);

    }
}
