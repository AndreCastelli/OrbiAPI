package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentsDockWebhookService extends BaseService {
    private static final String JSON_FILE_NAME = "dockwebhook//paymentsWebhookPayload.json";
    private static final String SWAGGER_PAYMENTS = "/v1/payments";
    private static final String CONTA = "idAccount";
    JsonUtil jsonUtil;

    public PaymentsDockWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response createPaymentsStatusPAID(int idAccount) throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createPaymentsStatusPAIDPayload(idAccount), SWAGGER_PAYMENTS);
    }

    public Response createPaymentsStatusPAIDWithWrongURL(int idAccount)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createPaymentsStatusPAIDPayload(idAccount), "/v1/paymen");
    }

    public Response createPaymentsStatusPAIDWithInvalidPayload(String idAccount) {
        String json = "{\"payload\":{\"idAccount\":" + idAccount + ",\"amount\": 2}}";

        return doPostRequestJson(json, SWAGGER_PAYMENTS);
    }

    public Response createPaymentsStatusPAIDWithMandatoryData() {
        String json = "{\"payload\":{\"description\":Pagamento solicitado pelo usuário,\"amount\": 2}}";

        return doPostRequestJson(json, SWAGGER_PAYMENTS);
    }

    public String createPaymentsStatusPAIDPayload(int idAccount) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> payload = new LinkedHashMap();
        payload.put(CONTA, idAccount);
        payload.put("barCodeNumber", "23792830800000002003391090159000003900079080");
        JSONArray itemarray = new JSONArray();
        itemarray.put(payload);

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);
        requestBody.put("origin", "PAYMENTS");
        requestBody.put("payload", payload);

        return requestBody.toString();
    }

    public Response changePayloadAttribute(String attributeName, String attributeValue) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME);

        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return doPostRequestJson(jsonBody.toString(), SWAGGER_PAYMENTS);
    }
}
