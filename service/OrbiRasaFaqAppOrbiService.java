package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiRasaFaqAppOrbiService extends BaseService {
    private static final String SWAGGER_MODELPARSE = "/model/parse";
    private static final String SWAGGER_WEBHOOK = "/webhooks/rest/webhook";
    private static final String ACTUATOR_HEALTH_ENDPOINT = "/";
    JsonUtil jsonUtil;

    public OrbiRasaFaqAppOrbiService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil
                .getPropertyByName("orbi.rasa.faqapporbi.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response sendPayloadText(String text) {
        return doPostRequestJson(
                createQuestionConfirmedPayload(text), SWAGGER_MODELPARSE);
    }

    public Response sendEmptyPayloadText() {
        return doPostRequestJson(
                createQuestionConfirmedPayloadEmpty(), SWAGGER_MODELPARSE);
    }

    public Response sendQueryParameters() {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("tipo", "PF");
        queryParams.put("orgaoExpedidorIdentidade", "SSP");

        return doPostRequestQuery(queryParams, SWAGGER_MODELPARSE);
    }

    public Response sendWebhookPayloadText(String text) {
        return doPostRequestJson(
                createQuestionConfirmedPayload(text), SWAGGER_WEBHOOK);
    }

    public Response sendIncorrectPayloadText(String text) {
        return doPostRequestJson(
                createIncorrectQuestionConfirmedPayload(text), SWAGGER_MODELPARSE);
    }

    public Response sendIncompletePayloadText(String message) {
        return doPostRequestJson(
                "", SWAGGER_MODELPARSE);
    }

    private String createQuestionConfirmedPayload(String text) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", text);

        return requestBody.toString();
    }

    private String createQuestionConfirmedPayloadEmpty() {
        JSONObject requestBody = new JSONObject();

        return requestBody.toString();
    }

    private String createIncorrectQuestionConfirmedPayload(String text) {
        return text;
    }

    public Response getActuatorHealth() {
        return doGetRequestJson("", ACTUATOR_HEALTH_ENDPOINT);
    }
}
