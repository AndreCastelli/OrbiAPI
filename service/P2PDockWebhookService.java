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

public class P2PDockWebhookService extends BaseService {
    private static final String JSON_FILE_NAME = "dockwebhook//p2pWebhookPayload.json";
    private static final String SWAGGER_P2P = "/v1/p2p";
    JsonUtil jsonUtil;

    public P2PDockWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response createTransfer(int destinationAccount) throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createP2PTransferPayload(destinationAccount), SWAGGER_P2P);
    }

    public Response createTransferWithInvalidPayload(int destinationAccount) {
        String json = "{\"payload\":{\"originalAccount\":11,\"destinationAccount\": " + destinationAccount + "}}";

        return doPostRequestJson(json, SWAGGER_P2P);
    }

    public Response createTransferWithWrongURL(int destinationAccount)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(
                createP2PTransferPayload(destinationAccount), "/v1/p2");
    }

    public String createP2PTransferPayload(int destinationAccount) throws NoSuchFieldException, IllegalAccessException {
        Map payload = new LinkedHashMap();

        payload.put("originalAccount", "11");
        payload.put("destinationAccount", destinationAccount);
        payload.put("amount", "0.01");
        JSONArray itemarray = new JSONArray();
        itemarray.put(payload);

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);
        requestBody.put("origin", "P2P-TRANSFER");
        requestBody.put("payload", payload);

        return requestBody.toString();
    }

    public Response executeRequestChangePayload(String attributeName, String attributeValue) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME);

        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return doPostRequestJson(jsonBody.toString(), SWAGGER_P2P);
    }
}
