package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiBankAccountManagerAdjustmentsService extends BaseService {

    private static final String ACCOUNTS_ENDPOINT = "/accounts";
    private static final String ADJUSTMENTS_ENDPOINT = "/adjustments";

    public OrbiBankAccountManagerAdjustmentsService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.account.baseURI"));
    }

    public Map<String, Object> createAdjustmentPayload(String date, int adjustmentTypeId,
                                                       BigDecimal value, String uuidAccount) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("adjustmentDate", date);
        queryParams.put("adjustmentTypeId", adjustmentTypeId);
        queryParams.put("adjustmentValue", value);
        queryParams.put("uuidAccount", uuidAccount);

        return queryParams;
    }

    public Response createAdjustmentSucess(String date, int adjustmentTypeId, BigDecimal value, String uuidAccount) {
        Response response = doPostRequestQuery(createAdjustmentPayload(date, adjustmentTypeId,
                value, uuidAccount), ACCOUNTS_ENDPOINT + ADJUSTMENTS_ENDPOINT);

        return response;
    }
}