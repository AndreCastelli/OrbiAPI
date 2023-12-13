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

public class CardsWebhookService extends BaseService {

    private static final String JSON_FILE_NAME = "dockwebhook//cardsWebhookPayload.json";
    private static final String JSON_ENCRYPTED_FILE_NAME = "dockwebhook//cardsWebhookPayloadEncrypted.json";
    private static final String JSON_ENCRYPTED_ORIGIN_INVALID
            = "dockwebhook//cardsWebhookPayloadEncryptedOriginInvalid.json";
    private static final String JSON_ENCRYPTED_PAYLOAD_INVALID
            = "dockwebhook//cardsWebhookPayloadEncryptedPayloadInvalid.json";
    private static final String JSON_ENCRYPTED_OUT = "dockwebhook//cardsWebhookPayloadEncryptedOut.json";
    private static final String X_CONTENT_TYPE = "x-Content-Type";
    private static final String JSON_ENCRYPTED = "application/json+encrypted";
    private static final int PURCHASE_ID = 999;
    private static final int CARD_ID = 5099;
    private static final int ACCOUNT_ID = 5099;
    String resource = "v1/cards";
    String invalidResource = "v/cards";
    JsonUtil jsonUtil;

    public CardsWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response createPurchaseNotification() throws IOException {
        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME), resource);
    }

    public Response createPurchaseEncryptedNotification() throws IOException {
        RequestManager.shared().setHeaders(Map.of(X_CONTENT_TYPE, JSON_ENCRYPTED));

        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_ENCRYPTED_FILE_NAME), resource);
    }

    public Response createPurchaseEncryptedNotificationOriginInvalid() throws IOException {
        RequestManager.shared().setHeaders(Map.of(X_CONTENT_TYPE, JSON_ENCRYPTED));

        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_ENCRYPTED_ORIGIN_INVALID), resource);
    }

    public Response createPurchaseEncryptedNotificationPayloadInvalid() throws IOException {
        RequestManager.shared().setHeaders(Map.of(X_CONTENT_TYPE, JSON_ENCRYPTED));

        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_ENCRYPTED_PAYLOAD_INVALID), resource);
    }

    public Response createPurchaseEncryptedNotificationOut() throws IOException {
        RequestManager.shared().setHeaders(Map.of(X_CONTENT_TYPE, JSON_ENCRYPTED));
        
        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_ENCRYPTED_OUT), resource);
    }

    public Response createPurchaseNotificationWithWrongURL() throws IOException {
        return doPostRequestJson(jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME), invalidResource);
    }

    public Response createPurchaseNotificationWithoutMandatoryData(String mandatoryField)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createInvalidPayload(mandatoryField), resource);
    }

    public Response createPurchaseNotificationWithWrongPayload() {
        String json = "{\"origin\":\"DATALAKE-AUTHORIZATION-PURCHASE-NOTIFICATION\",\"payload\":"
                + "{\"accountId\":teste,\"purchaseId\":999,\"cardId\":5099}}";

        return doPostRequestJson(json, resource);
    }

    public String createInvalidPayload(String mandatoryField) throws NoSuchFieldException, IllegalAccessException {
        Map payload = new LinkedHashMap();

        String payloadAccountIdField = "accountId";
        String payloadCardIdField = "cardId";
        String payloadPurchaseId = "purchaseId";

        payload.put(payloadPurchaseId, PURCHASE_ID);

        if (payloadAccountIdField.equals(mandatoryField)) {
            payload.put(payloadCardIdField, CARD_ID);
        } else if (payloadCardIdField.equals(mandatoryField)) {
            payload.put(payloadAccountIdField, ACCOUNT_ID);
        }

        JSONArray itemarray = new JSONArray();
        itemarray.put(payload);

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);
        requestBody.put("origin", "DATALAKE-AUTHORIZATION-PURCHASE-NOTIFICATION");
        requestBody.put("payload", payload);

        return requestBody.toString();
    }

    public Response changePayload(String attributeName, String attributeValue) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME);

        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split("\\.");

            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return doPostRequestJson(jsonBody.toString(), resource);
    }
}
