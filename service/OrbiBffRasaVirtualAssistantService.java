package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiBffRasaVirtualAssistantService extends BaseService {

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String BFF_RASA_BASEURI_PROP = "orbi.bff.rasa.virtualassistant.baseURI";
    private static final String BFF_RASA_XAPIKEY_PROP = "orbi.bff.rasa.virtualassistant.xApiKey";
    private static final String BFF_RASA_XAPIKEY_NAME = "x-api-key";
    JsonUtil jsonUtil;

    public OrbiBffRasaVirtualAssistantService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_RASA_BASEURI_PROP),
                BFF_RASA_XAPIKEY_NAME
        );
        RequestManager.shared().setRequest(req);
        jsonUtil = new JsonUtil();
    }

    public RequestSpecification buildRequest(String baseURI, String xApiKeyName) throws IOException {
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addHeader(xApiKeyName, "ey6c09cb2767293e153c7061730b948b")
                .build();

        return req;
    }

    public RequestSpecification buildRequest(String baseURI) throws IOException {
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .build();

        return req;
    }

    private String createJSONRequest(String text) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", text);

        return requestBody.toString();
    }

    public Response sendText(String text, String endpoint) {
        return doPostRequestJson(this.createJSONRequest(text), endpoint);
    }

    public Response sendInvalidRequest(String endpoint) {
        return doPostRequestJson(this.createJSONRequest(null), endpoint);
    }

    public Response sendTextNoAuth(String text, String xApiName, String endpoint) throws IOException {
        RequestSpecification req = this.buildRequest(propertiesUtil.getPropertyByName(BFF_RASA_BASEURI_PROP),
                xApiName);
        RequestManager.shared().setRequest(req);

        return doPostRequestJson(this.createJSONRequest(text), endpoint);
    }

    public Response sendTextNoAuth(String text, String endpoint) throws IOException {
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_RASA_BASEURI_PROP)
        );
        RequestManager.shared().setRequest(req);

        return doPostRequestJson(this.createJSONRequest(text), endpoint);
    }

    public Response getActuatorHealth() throws IOException {
        return doGetRequestJson("", ACTUATOR_HEALTH_ENDPOINT);
    }
}
