package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiBffVirtualAssistantCpfService extends BaseService {

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    private static final String BFF_BASEURI_PROP = "orbi.bff.virtualassistant.baseURI";
    private static final String BFF_XAPIKEY_PROP = "orbi.bff.virtualassistant.xApiKey";
    private static final String ORBI_ACCOUNT_CPF = "/orbi/account";
    private static final String CPF_PROP = "cpf";
    JsonUtil jsonUtil;

    public OrbiBffVirtualAssistantCpfService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                propertiesUtil.getPropertyByName(BFF_XAPIKEY_PROP)
        );
        RequestManager.shared().setRequest(req);
        jsonUtil = new JsonUtil();
    }

    public RequestSpecification buildRequest(String baseURI, String xApiKey) throws IOException {
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addHeader("x-api-key", xApiKey)
                .build();

        return req;
    }

    public Response getActuatorHealth() throws IOException {
        return doGetRequestJson("", ACTUATOR_HEALTH_ENDPOINT);
    }

    public Response consultClientCpf(String cpf) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(CPF_PROP, cpf);

        return doPostRequestJson(requestBody.toString(), ORBI_ACCOUNT_CPF);
    }
}
