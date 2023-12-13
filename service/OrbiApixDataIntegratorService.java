package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiApixDataIntegratorService extends BaseService {

    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    DateTimeUtil dateTimeUtil;

    public OrbiApixDataIntegratorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.apix.data.integrator.baseURI"));
    }

    public Response incidentReferenceDate() {
        Response response = doGetRequestQuery(consultlist(), "/v1/availability/incidents");

        return response;
    }

    public Map<String, Object> consultlist() {
        Map queryParams = new LinkedHashMap();
        queryParams.put("referenceDate",
                new DateTimeUtil().addOrSubtractDaysInADate(-1, "yyyy-MM-dd"));

        return queryParams;
    }

    public Response listDictClaimTransactions() {
        Response response = doGetRequestQuery(consultlist(), "/v1/dict/claim/transactions");

        return response;
    }

    public Response listDictKeyCount() {
        Response response = doGetRequestQuery(consultlist(), "/v1/dict/internal-search/count");

        return response;
    }
}
