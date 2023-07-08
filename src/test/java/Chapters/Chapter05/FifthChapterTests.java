package Chapters.Chapter05;

import builders.CategoryBuilder;
import builders.CreatePetRequestBuilder;
import builders.TagsBuilder;
import entities.requests.Category;
import entities.requests.CreatePetRequest;
import entities.requests.Tags;
import entities.responses.CreatePetResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.BaseTest;
import utils.RequestHelper;
import utils.ResourceHelper;
import utils.ResponseHelper;

import static io.restassured.RestAssured.given;

import java.io.IOException;

public class FifthChapterTests extends BaseTest {

	@Test
	public void chainingRequestsAndValidatingTheResponse() throws IOException {// Creating the Category Object
		Category category = new CategoryBuilder().withId(1).withName("Cats").build();

		// Creating the Tags Object
		Tags tags = new TagsBuilder().withId(1).withName("Tag 1").build();

		// Inserting the above created Tags object in the TagsList, because main object
		// accepts as an array
		Tags[] tagsList = new Tags[1];
		tagsList[0] = tags;

		// Creating the Main Object - CreatePetRequest
		String[] photoUrls = { "Photo Url" }; // Create an array with some URL's
		CreatePetRequest createPetRequest = new CreatePetRequestBuilder().withCategory(category).withTags(tagsList)
				.withPhotoUrls(photoUrls).withId(get3DigitRandomInt()) // This `get3DigitRandomInt()` will generate the
																		// random 3 digit number, coming from BaseTest
				.withName("Testing + " + get3DigitRandomInt()).withStatus("available").build();

		// Sending a Request
		String url = propertiesReader.getEndPointUrl("create_pet"); // Fetching url from Properties file
		String json = RequestHelper.getJsonString(createPetRequest); // Convert above created object into a String
		Response response = ResourceHelper.create(url, json);

		// Handle Redirection
		if (response.getStatusCode() == 301 || response.getStatusCode() == 302) {
			String redirectedUrl = response.getHeader("Location");
			response = given().contentType(ContentType.JSON).body(json).when().post(redirectedUrl);
		}

		// Validating the Response Code
		Assert.assertEquals(response.getStatusCode(), 200);

		// Validating the RequestBody & ResponseBody
		String responseBody = response.getBody().asString();
		Assert.assertTrue(responseBody.contains(createPetRequest.getName()));
		Assert.assertTrue(responseBody.contains(createPetRequest.getStatus()));
		Assert.assertTrue(responseBody.contains(createPetRequest.getTags()[0].getName()));

		// Verifying the Created Pet using GET Method
		String url1 = propertiesReader.getEndPointUrl("get_animal_based_on_pet_id") + createPetRequest.getId();// Concatenating
																												// the
																												// EndPoint
																												// &
																												// PetId
		Response findSpecificPetResponse = ResourceHelper.get(url1);

		// Handle Redirection
		if (findSpecificPetResponse.getStatusCode() == 301 || findSpecificPetResponse.getStatusCode() == 302) {
			String redirectedUrl = findSpecificPetResponse.getHeader("Location");
			findSpecificPetResponse = given().contentType(ContentType.JSON).when().get(redirectedUrl);
		}
		String findSpecificPetResponseBody = findSpecificPetResponse.getBody().asString();
		Assert.assertEquals(findSpecificPetResponse.getStatusCode(), 200);

		Assert.assertTrue(findSpecificPetResponseBody.contains(String.valueOf(createPetRequest.getId())));
		Assert.assertTrue(findSpecificPetResponseBody.contains(createPetRequest.getName()));
	}
}
