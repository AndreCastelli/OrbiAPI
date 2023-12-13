package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransferDockWebhookService extends BaseService {
    private static final String JSON_FILE_NAME = "dockwebhook//transferWebhookPayload.json";
    private static final int RANGE = 999;
    private static final String RESOURCE = "/v1/transfer";
    private static final String INVALID_RESOURCE = "/v/transfer";
    JsonUtil jsonUtil;

    public TransferDockWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response createTransferOut(int idOriginAccount) throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createTransferOutPayload(idOriginAccount), RESOURCE);
    }

    public Response createTransferOutWithWrongURL(int idOriginAccount)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createTransferOutPayload(idOriginAccount), INVALID_RESOURCE);
    }

    public Response createTransferOutWithMandatoryData(int idOriginAccount) {
        String json = "{\"payload\":{\"value\":0.1}}";

        return doPostRequestJson(json, RESOURCE);
    }

    public Response createTransferWithInvalidPayload() {
        String json = "{\"payload\":{\"idOriginAccount\":XyZ,\"value\":0.01}}";

        return doPostRequestJson(json, RESOURCE);
    }

    public String createTransferOutPayload(int idOriginAccount) throws NoSuchFieldException, IllegalAccessException {
        Map payload = new LinkedHashMap();
        payload.put("idOriginAccount", idOriginAccount);
        payload.put("identificator", MathUtil.getRandomNumber(RANGE));
        JSONArray itemarray = new JSONArray();
        itemarray.put(payload);

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);
        requestBody.put("origin", "BANKTRANSFER");
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

        return doPostRequestJson(jsonBody.toString(), RESOURCE);
    }
}
