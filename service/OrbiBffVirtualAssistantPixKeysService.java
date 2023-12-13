package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiBffVirtualAssistantPixKeysService extends BaseService {

    private static final String BFF_BASEURI_PROP = "orbi.bff.virtualassistant.baseURI";
    private static final String BFF_XAPIKEY_PROP = "orbi.bff.virtualassistant.xApiKey";
    private static final String ORBI_PIX_KEYS_ENDPOINT = "/orbi/pix/keys";
    private static final String X_API_KEY_NAME = "x-api-key";
    private static final String CPF_PROP = "cpf";
    JsonUtil jsonUtil;

    public OrbiBffVirtualAssistantPixKeysService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                X_API_KEY_NAME,
                propertiesUtil.getPropertyByNameBase64(BFF_XAPIKEY_PROP)
        );
        RequestManager.shared().setRequest(req);
        jsonUtil = new JsonUtil();
    }

    public RequestSpecification buildRequest(String baseURI, String xApiKeyName, String xApiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addHeader(xApiKeyName, xApiKey)
                .build();
    }

    public Response consultClientPixKeysByCpf(String cpf) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(CPF_PROP, cpf);

        return doPostRequestJson(requestBody.toString(), ORBI_PIX_KEYS_ENDPOINT);
    }

    public Response consultNoAuth(String cpf, String xApiKeyName, String xApiKey) {
        String xApiKeyNameRequest = xApiKeyName;
        String xApiKeyRequest = xApiKey;

        if (xApiKeyNameRequest == null) {
            xApiKeyNameRequest = X_API_KEY_NAME;
        }

        if (xApiKeyRequest == null) {
            xApiKeyRequest = propertiesUtil.getPropertyByNameBase64(BFF_XAPIKEY_PROP);
        }

        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                xApiKeyNameRequest,
                xApiKeyRequest
        );

        RequestManager.shared().setRequest(req);
        JSONObject requestBody = new JSONObject();
        requestBody.put(CPF_PROP, cpf);

        return doPostRequestJson(requestBody.toString(), ORBI_PIX_KEYS_ENDPOINT);
    }
}
