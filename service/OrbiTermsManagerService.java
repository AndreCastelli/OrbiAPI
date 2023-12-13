package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.DocumentUtil;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OrbiTermsManagerService extends BaseService {
    private static final String TERMS_POLICY_ENDPOINT = "/v2/term/{type}";
    private static final String ACCEPT_THE_TERM_ENDPOINT = "/v2/term/{type}/accept";
    private static final String PAYLOAD_TERM_REGISTRATION = "orbitermsmanager/termspolicy.txt";

    private static final String TERMS_AND_PRIVACY = "TERMS_AND_PRIVACY";
    private static final String CLOSING_ACCOUNT = "CLOSING_ACCOUNT";

    String deviceId = "TestdeviceId";
    JsonUtil jsonUtil;

    public OrbiTermsManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.terms.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response consultTheTermAndPolicy(String termType) {
        return doGetRequestOneAttribute(TERMS_POLICY_ENDPOINT, termType);
    }

    public Response termRegistrationPayload() throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_TERM_REGISTRATION);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        return doPutRequestOneAttributeAndBody(TERMS_POLICY_ENDPOINT, TERMS_AND_PRIVACY, data);
    }

    public Response updatePayload(String attribute, String attributeValue) {
        if (attribute.equalsIgnoreCase("text"))
            return doPutRequestOneAttributeAndBody(TERMS_POLICY_ENDPOINT, TERMS_AND_PRIVACY, attributeValue);
         else
            return doPutRequestOneAttribute(TERMS_POLICY_ENDPOINT, attributeValue);
    }

    public JSONObject createTermAcceptancePayload() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cpf", new DocumentUtil().getRandomValidCPF(false));
        jsonBody.put("deviceId", deviceId);
        jsonBody.put("operatingSystem", "create_an_account");
        jsonBody.put("origin", "app");

        return jsonBody;
    }

    public JSONObject createClosingAccountTermAcceptancePayload() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cpf", new DocumentUtil().getRandomValidCPF(false));
        jsonBody.put("deviceId", deviceId);
        jsonBody.put("operatingSystem", "close_an_account");
        jsonBody.put("origin", "app");
        jsonBody.put("termType", CLOSING_ACCOUNT);

        return jsonBody;
    }

    public Response acceptTerm(JSONObject payload, String type) {
        return doPostRequestOneAttributeAndBody(ACCEPT_THE_TERM_ENDPOINT, type, payload.toString());
    }

    public JSONObject updateAcceptedPayload(String attribute, String attributeValue, JSONObject payload) {
        payload.put(attribute, attributeValue);

        return payload;
    }
}
