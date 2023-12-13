package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class OrbiVirtualAssistantEventQueueProducerService extends BaseService {

    private static final String VA_EQP_BASEURI_PROP = "orbi.virtualassistant.eventqueue.producer.baseURI";
    private static final String VA_EQP_XAPIKEY_PROP = "orbi.virtualassistant.eventqueue.producer.xApiKey";
    private static final String VA_EQP_RECEIVER_ENDPOINT = "/receiver/{source}";
    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String X_API_KEY_NAME = "auth";
    JsonUtil jsonUtil;

    public OrbiVirtualAssistantEventQueueProducerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(VA_EQP_BASEURI_PROP),
                X_API_KEY_NAME,
                propertiesUtil.getPropertyByNameBase64(VA_EQP_XAPIKEY_PROP)
        );
        RequestManager.shared().setRequest(req);
        jsonUtil = new JsonUtil();
    }

    public RequestSpecification buildRequest(String baseURI, String xApiKeyName, String xApiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addQueryParam(xApiKeyName, xApiKey)
                .build();
    }

    public Response getActuatorHealth() {
        return doGetRequestJson("", ACTUATOR_HEALTH_ENDPOINT);
    }

    public Response sendEvent(String jsonEventPath, String queueName) throws IOException {
        String payloadString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + jsonEventPath
        );

        return doPostRequestOneAttribute(
                payloadString,
                VA_EQP_RECEIVER_ENDPOINT,
                queueName.toLowerCase()
        );
    }

    public Response sendRawEvent(String event, String queueName, String auth, String endpoint) {
        String authRequest = propertiesUtil.getPropertyByNameBase64(VA_EQP_XAPIKEY_PROP);
        String endpointRequest = VA_EQP_RECEIVER_ENDPOINT;

        if (auth != null) {
            authRequest = auth;
        }

        if (endpoint != null) {
            endpointRequest = endpoint;
        }

        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(VA_EQP_BASEURI_PROP),
                X_API_KEY_NAME,
                authRequest
        );
        RequestManager.shared().setRequest(req);

        return doPostRequestOneAttribute(
                event,
                endpointRequest,
                queueName.toLowerCase()
        );
    }
}
