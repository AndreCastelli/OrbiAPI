package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiCrkConnectorService extends BaseService {

    private static final String ORBI_CRK_CONNECTOR_ENDPOINT = "/crk/keys";
    private static final String ORBI_QRCODESTATIC_CRK_ENDPOINT = "/crk/payments/qrcode/static";
    private static final String ORBI_QRCODE_CRK_ENDPOINT = "/crk/payments/qrcode";
    private static final String CRK_ACCOUNT_ENDPOINT = "/crk/accounts";
    private static final String ISPB_NUMBER = "27351731";
    private static final String NATURAL_PERSON = "NATURAL_PERSON";
    private static final String ACCOUNT_TYPE = "PAYMENT";
    private static final String ACCOUNT_BRANCH = "accountBranch";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String BRANCH_NUMBER = "0001";
    private static final String TYPE_CPF = "CPF";
    private static final String TYPE_EMAIL = "EMAIL";
    private static final String TYPE_PHONE = "PHONE";
    private static final String TYPE_ISPB = "ispbNumber";
    private static final String CPF = "cpfCnpj";
    private static final String PERSONDOCUMENT = "personDocument";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String KEYVALUE = "keyValue";
    private static final String KEYTYPE = "keyType";
    private static final String REASON = "operationReason";

    public OrbiCrkConnectorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.crk.baseURI"));
    }

    public String getKeyValuePix(String keyType) {
        String keyValue;

        switch (keyType) {
            case TYPE_CPF:
                keyValue = RequestManager.shared().getHelpJsonObject().get(CPF).toString();
                break;
            case TYPE_EMAIL:
                String email = RequestManager.shared().getHelpJsonObject().get(EMAIL).toString();
                String normalized = Normalizer.normalize(email, Normalizer.Form.NFD);
                keyValue = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                break;
            case TYPE_PHONE:
                keyValue = RequestManager.shared().getHelpJsonObject().get(PHONE).toString();
                break;
            default:
                keyValue = "";
                break;
        }

        return keyValue;
    }

    public String payloadPixWithKey(String keyType, String name, String accountNumber, String cpf, String keyValue) {
        JSONObject requestBody = new JSONObject();

        requestBody.put(ACCOUNT_BRANCH, BRANCH_NUMBER);
        requestBody.put(ACCOUNT_NUMBER, accountNumber);
        requestBody.put("accountType", ACCOUNT_TYPE);
        requestBody.put(TYPE_ISPB, ISPB_NUMBER);
        requestBody.put(KEYTYPE, keyType);
        requestBody.put(KEYVALUE, keyValue);
        requestBody.put(PERSONDOCUMENT, cpf);
        requestBody.put("personFantasyName", name);
        requestBody.put("personName", name);
        requestBody.put("personType", NATURAL_PERSON);

        return requestBody.toString();
    }

    public Response createPixCrkWithKey(
            String keyType, String name, String accountNumber, String cpf, String keyValue) {
        return doPostRequestJson(payloadPixWithKey(
                keyType, name, accountNumber, cpf, keyValue), ORBI_CRK_CONNECTOR_ENDPOINT);
    }

    public Response getPixCrk(String accountNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_BRANCH, BRANCH_NUMBER);
        params.put(ACCOUNT_NUMBER, accountNumber);
        params.put(TYPE_ISPB, ISPB_NUMBER);

        return doGetRequestWithListParams(params, ORBI_CRK_CONNECTOR_ENDPOINT);
    }

    public Map<String, Object> createParamsDeletePixKey(String keyValue, String keyType, String operationReason) {
        Map<String, Object> payload = new HashMap<>();

        payload.put(TYPE_ISPB, ISPB_NUMBER);
        payload.put(REASON, operationReason);
        payload.put(KEYTYPE, keyType);
        payload.put(KEYVALUE, keyValue);

        return payload;
    }

    public Response deletePixKey(String keyValue, String keyType, String operationReason) {
        return doDeleteRequestWithListParams(
                createParamsDeletePixKey(keyValue, keyType, operationReason), ORBI_CRK_CONNECTOR_ENDPOINT);
    }

    public Map<String, Object> consultPixAccount(String keyValue, String keyType, String personDocument) {
        Map<String, Object> payload = new LinkedHashMap();

        payload.put(PERSONDOCUMENT, personDocument);
        payload.put(KEYTYPE, keyType);
        payload.put(KEYVALUE, keyValue);
        payload.put(TYPE_ISPB, ISPB_NUMBER);

        return payload;
    }

    public Response getPixKeyAccount(String keyValue, String keyType, String personDocument) {
        return doGetRequestQuery(consultPixAccount(keyValue, keyType, personDocument), CRK_ACCOUNT_ENDPOINT);
    }

    public Response createQrCode(String keyValue) {
        Map<String, Object> params = new HashMap<>();
        params.put("cityName", "Porto Alegre");
        params.put("description", "teste");
        params.put(TYPE_ISPB, ISPB_NUMBER);
        params.put(KEYVALUE, keyValue);
        params.put("value", "100.23");

        return doPostRequestMap(params, ORBI_QRCODESTATIC_CRK_ENDPOINT);
    }

    public Response getQrCode(String emv) {
        Map<String, Object> params = new HashMap<>();
        params.put(TYPE_ISPB, ISPB_NUMBER);
        params.put("emv", emv);

        return doGetRequestWithListParams(params, ORBI_QRCODE_CRK_ENDPOINT);
    }
}
