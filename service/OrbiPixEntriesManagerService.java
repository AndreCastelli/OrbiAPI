package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class OrbiPixEntriesManagerService extends BaseService {

    private static final String PIX_KEYS = "/keys";
    private static final String PIX_ACCOUNT_ENDPOINT = "/v1/pix-account";
    private static final String PIX_AVAILABLE_ENDPOINT = "/keys/available";
    private static final String PIX_QRCODE = "/v1/payment/qrcode";
    private static final String KEY_TOKEN_REQUEST_ENDPOINT = "/key/ownership/request";
    private static final String KEY_TOKEN_VALIDATE_ENDPOINT = "/key/ownership/validate";
    private static final String TYPE_CPF = "CPF";
    private static final String TYPE_EMAIL = "EMAIL";
    private static final String TYPE_PHONE = "PHONE";
    private static final String CPF = "cpfCnpj";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String KEYVALUE = "keyValue";
    private static final String KEYTYPE = "keyType";
    private static final String REASON = "operationReason";
    private static final String VERSION = "/v1/";
    private static final String DATETIME = "transactionDate";
    private static final String ACCOUNTID = "consultantAccountId";
    private static final String REQUESTDATETIME = "requestDateTime";
    private static final String TOKEN = "token";
    DateTimeUtil dateTimeUtil;
    JsonUtil jsonUtil;

    public OrbiPixEntriesManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.entries.manager.baseURI"));
        jsonUtil = new JsonUtil();
        dateTimeUtil = new DateTimeUtil();
    }

    public String getDateTime() {
        return dateTimeUtil.getDateFormattedIso8601();
    }

    public Response createPixWithKeyCpf(String keyType, String keyValue, String accountId) {
        String date = getDateTime();
        JSONObject requestBody = new JSONObject();

        if (Objects.equals(keyType, "EVP")) {
            requestBody.put(KEYTYPE, keyType);
            requestBody.put(REQUESTDATETIME, date);
        } else {
            requestBody.put(KEYTYPE, keyType);
            requestBody.put(KEYVALUE, keyValue);
            requestBody.put(REQUESTDATETIME, date);
        }

        return doPostRequestJson(requestBody.toString(), VERSION + accountId + PIX_KEYS);
    }

    public Response getPixWithAccountId(String accountId) {
        String date = getDateTime();
        Map<String, Object> params = new HashMap<>();
        params.put(DATETIME, date);

        return doGetRequestWithListParams(params, VERSION + accountId + PIX_KEYS);
    }

    public Map<String, Object> createParamsDeletePixKey(String keyValue, String keyType, String operationReason) {
        String date = getDateTime();
        Map<String, Object> payload = new HashMap<>();

        payload.put(REASON, operationReason);
        payload.put(KEYTYPE, keyType);
        payload.put(KEYVALUE, keyValue);
        payload.put(DATETIME, date);

        return payload;
    }

    public Response deletePixKey(String keyValue, String keyType, String accountId, String operationReason) {
        return doDeleteRequestWithListParams(
                createParamsDeletePixKey(keyValue, keyType, operationReason), VERSION + accountId + PIX_KEYS);
    }

    public Response getPixKeyAvailable(String accountId) {
        return doGetWithoutParameters(VERSION + accountId + PIX_AVAILABLE_ENDPOINT);
    }

    public String getKeyValuePix(String keyType) {
        String keyValue;
        int num = MathUtil.getRandomNumber(MathUtil.TEN_MILLION);

        switch (keyType) {
            case TYPE_CPF:
                keyValue = RequestManager.shared().getHelpJsonObject().get(CPF).toString();
                break;
            case TYPE_EMAIL:
                String emailAccent = RequestManager.shared().getHelpJsonObject().get(EMAIL).toString();
                String normalized = Normalizer.normalize(emailAccent, Normalizer.Form.NFD);
                String email = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                keyValue = num + email;
                break;
            case TYPE_PHONE:
                keyValue = RequestManager.shared().getHelpJsonObject().get(PHONE).toString();
                break;
            default:
                keyValue = "ENV";
                break;
        }

        return keyValue;
    }

    public String payloadQRCode(String keyValue, String keyType) {
        JSONObject payload = new JSONObject();

        payload.put("description", "teste");
        payload.put(KEYVALUE, keyValue);
        payload.put(KEYTYPE, keyType);
        payload.put("qrCodeType", "STATIC");
        payload.put("value", 2.00);

        return payload.toString();
    }

    public Response createQrCode(String keyValue, String keyType, String accountId) {
        Map<String, String> params = new HashMap<>();
        params.put("accountId", accountId);

        return doPostRequestMapAndQueryParamsTypeJson(payloadQRCode(keyValue, keyType), params, PIX_QRCODE);
    }

    public Response consultPixAccountWithContext(String keyValue, String keyType, String accountId, String context) {
        Map header = new LinkedHashMap();
        header.put("X-Context", context);
        RequestManager.shared().setHeaders(header);

        Map<String, Object> payload = new HashMap<>();
        payload.put(ACCOUNTID, accountId);
        payload.put(KEYTYPE, keyType);
        payload.put(KEYVALUE, keyValue);

        return doGetRequestQuery(payload, PIX_ACCOUNT_ENDPOINT);
    }

    public Response requestKeyOwnershipConfirmationToken(String keyType, String keyValue, String accountId) {
        String date = getDateTime();
        Map<String, Object> requestToken = new HashMap<>();
        requestToken.put(KEYTYPE, keyType);
        requestToken.put(KEYVALUE, keyValue);
        requestToken.put(REQUESTDATETIME, date);

        return doPostRequestMap(requestToken, VERSION + accountId + KEY_TOKEN_REQUEST_ENDPOINT);
    }

    public Response validateKeyOwnershipConfirmationToken(Integer token, String accountId) {
        String strToken = token.toString();

        JSONObject validateToken = new JSONObject();
        validateToken.put(TOKEN, strToken);

        return doPutRequestJson(validateToken.toString(), VERSION + accountId + KEY_TOKEN_VALIDATE_ENDPOINT);
    }
}
