package service;

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

public class VoucherDockWebhookService extends BaseService {
    private static final String SWAGGER_VOUCHER = "/v1/voucher";
    private static final String INVALID_VOUCHER_SWAGGER = "/v/voucher";

    public VoucherDockWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
    }

    public Response sendVoucherConfirmation(String merchantId, int accountId, String orderId)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createVoucherConfirmedPayload(merchantId, accountId, orderId), SWAGGER_VOUCHER);
    }

    public Response createConfirmedUrlIncorreta(String merchantId, int accountId, String orderId)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(
                createVoucherConfirmedPayload(merchantId, accountId, orderId), INVALID_VOUCHER_SWAGGER);
    }

    public String createVoucherConfirmedPayload(String merchantId, int accountId, String orderId)
            throws NoSuchFieldException, IllegalAccessException {
        JsonUtil jsonUtil = new JsonUtil();

        Map<String, Object> payload = new LinkedHashMap();
        payload.put("merchantId", merchantId);
        payload.put("orderId", orderId);
        payload.put("uuid", MathUtil.getRandomUUID());
        payload.put("idAccount", accountId);

        JSONArray itemArray = new JSONArray();
        itemArray.put(payload);

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);
        requestBody.put("origin", "VOUCHER-NOTIFICATION");
        requestBody.put("payload", payload);

        return requestBody.toString();
    }

    public Response executeRequestChangePayload(String payloadString, String attributeName, String attributeValue) {
        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return doPostRequestJson(jsonBody.toString(), SWAGGER_VOUCHER);
    }
}
