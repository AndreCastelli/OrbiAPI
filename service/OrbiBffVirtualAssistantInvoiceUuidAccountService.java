package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class OrbiBffVirtualAssistantInvoiceUuidAccountService extends BaseService {
    private static final String ORBI_CARDS_INVOICE_CURRENT_UUIDACCOUNT = "/orbi/cards/invoice/current/{uuidAccount}";
    private static final String BFF_BASEURI_PROP = "orbi.bff.virtualassistant.baseURI";
    private static final String BFF_XAPIKEY_PROP = "orbi.bff.virtualassistant.xApiKey";

    public OrbiBffVirtualAssistantInvoiceUuidAccountService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                propertiesUtil.getPropertyByName(BFF_XAPIKEY_PROP)
        );
        RequestManager.shared().setRequest(req);
    }

    public RequestSpecification buildRequest(String baseURI, String xApiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addHeader("x-api-key", xApiKey)
                .build();
    }

    public Response getInvoiceCurrentUuidAccount(String uuidAccount) {
        return doGetRequestOneAttribute(ORBI_CARDS_INVOICE_CURRENT_UUIDACCOUNT, uuidAccount);
    }

    public Response getUuidAccountNoAuth(String uuidAccount, String xApiKey) {
        RequestSpecification req = this.buildRequest(
                propertiesUtil.getPropertyByName(BFF_BASEURI_PROP),
                xApiKey
        );
        RequestManager.shared().setRequest(req);

        return doGetRequestOneAttribute(ORBI_CARDS_INVOICE_CURRENT_UUIDACCOUNT, uuidAccount);
    }
}
