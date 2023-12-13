package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiBffMobileCashbackService  extends BaseService {
    private static final String HOME_ENDPOINT = "/v1/home";
    private static final String ORBI_SHOPPING_ENDPOINT = "/v1/orbi-shopping";

    JsonUtil jsonUtil;

    public OrbiBffMobileCashbackService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.bff.mobile.cashback.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response getHome() {
            Map<String, Object> params = new HashMap<>();
            return doGetRequestWithListParams(params, HOME_ENDPOINT );
    }

    public Response getOrbiShopping() {
        Map<String, Object> params = new HashMap<>();
        return doGetRequestWithListParams(params, ORBI_SHOPPING_ENDPOINT  );
    }
}
