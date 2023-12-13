package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class OrbiBffvirtualAssistantBalanceUuidAccountService extends BaseService {

    private static final String ORBI_ACCOUNT_BALANCE_UUIDACCOUNT = "/orbi/account/balance/{uuidAccount}";
    private static final String BFF_BASEURI_PROP = "orbi.bff.virtualassistant.baseURI";
    private static final String BFF_XAPIKEY_PROP = "orbi.bff.virtualassistant.xApiKey";
    JsonUtil jsonUtil;

    public OrbiBffvirtualAssistantBalanceUuidAccountService() throws IOException {
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

    public Response getAccountBalanceUuidAccount(String uuidAccount) {
        return doGetRequestOneAttribute(ORBI_ACCOUNT_BALANCE_UUIDACCOUNT, uuidAccount);
    }

    public Response getUuidAccountNoAuth(String uuidAccount, String xApiKey) throws IOException {
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                xApiKey
        );
        RequestManager.shared().setRequest(req);

        return doGetRequestOneAttribute(ORBI_ACCOUNT_BALANCE_UUIDACCOUNT, uuidAccount);
    }
}
