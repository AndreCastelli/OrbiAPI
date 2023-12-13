package base.util;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import service.BaseService;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class DockClientUtil extends BaseService {

    public DockClientUtil() throws IOException {
        propertiesUtil = new PropertiesUtil();
    }

    public RequestSpecification getAuthenticateToTheDockAPI() {
        Response response = given().auth().preemptive().basic(
                propertiesUtil.getPropertyByNameBase64("dock.security.oauth2.client.client-id"),
                propertiesUtil.getPropertyByNameBase64("dock.security.oauth2.client.client-secret"))
                .formParam("grant_type", propertiesUtil.getPropertyByName("dock.security.oauth2.client.grant-type"))
                .when()
                .post(propertiesUtil.getPropertyByName("dock.security.oauth2.client.access-token-uri"));

        return new RequestSpecBuilder().addHeader(
                "Authorization", "Bearer " + response.then().extract().path("access_token")).build();
    }
}
