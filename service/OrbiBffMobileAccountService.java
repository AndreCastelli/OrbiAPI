package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiBffMobileAccountService extends BaseService {
    private static final String ACCOUNT_INFO_ENDPOINT = "/v1/account/info";
    private static final String ACCOUNT_INCOME_STATEMENT_ENDPOINT = "/v1/account/income-statement";
    private static final String BALANCE_ENDPOINT = "/balance";

    public OrbiBffMobileAccountService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.bff.mobile.baseURI"));
    }

    public Response consultAccountInfo() {
        return doForceJobExecute(ACCOUNT_INFO_ENDPOINT);
    }

    public Response consultIncomeStatement(String year) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("baseYear", year);
        return doGetRequestQuery(queryParams, ACCOUNT_INCOME_STATEMENT_ENDPOINT + BALANCE_ENDPOINT);
    }
}