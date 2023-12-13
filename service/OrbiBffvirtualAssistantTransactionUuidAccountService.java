package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class OrbiBffvirtualAssistantTransactionUuidAccountService extends BaseService {
    private static final String ORBI_ACCOUNT_TRANSACTIONS_UUIDACCOUNT = "/orbi/account/transactions/{uuidAccount}";
    private static final String BFF_BASEURI_PROP = "orbi.bff.virtualassistant.baseURI";
    private static final String BFF_XAPIKEY_PROP = "orbi.bff.virtualassistant.xApiKey";
    JsonUtil jsonUtil;

    public OrbiBffvirtualAssistantTransactionUuidAccountService() throws IOException {
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

    public Response getAccountTransactionUuidAccount(String uuidAccount) {
        return doGetRequestOneAttribute(ORBI_ACCOUNT_TRANSACTIONS_UUIDACCOUNT, uuidAccount);
    }

    public Response getUuidAccountNoAuth(String uuidAccount, String xApiKey) throws IOException {
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                xApiKey
        );
        RequestManager.shared().setRequest(req);

        return doGetRequestOneAttribute(ORBI_ACCOUNT_TRANSACTIONS_UUIDACCOUNT, uuidAccount);
    }
}
