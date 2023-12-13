package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiMarketplaceManagerService extends BaseService {
    private static final String MARKETPLACE_ENDPOINT = "/v1/offers";
    private static final String ID = "id";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String LOGO_URI = "logoUri";
    private static final String STORE = "store";
    private static final String CATEGORIES = "categories";
    private static final String TITLE = "title";
    private static final String CASHBACK = "cashback";
    private static final String PROGRAMMED_ID = "programmeId";
    private static final String OFFER_TYPE = "offerType";
    private static final String PARTICIPANT_ID = "participantId";

    JsonUtil jsonUtil;

    public OrbiMarketplaceManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.marketplace.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response getOffers(String participantId) {
        Map<String, Object> params = new HashMap<>();
        params.put("participantId", participantId);

        return doGetRequestWithListParams(params, MARKETPLACE_ENDPOINT );
    }
}




