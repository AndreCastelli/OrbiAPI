package service;

import base.RequestManager;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiTransactionLimitsManagerService extends BaseService {

    private static final String
        PATH_LIMITS_PIX_TRANSACTION_AVAILABILITY = "/v1/limits/pix/transaction/availability/{accountId}";

    public OrbiTransactionLimitsManagerService() throws IOException {
        PropertiesUtil propertiesUtil = new PropertiesUtil();

        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(
            "orbi.transaction.limits.manager.baseURI"));
    }

    public Response getLimitsPixTransactionAvailability(final String accountId, final String amount,
            final String transactionId, final String transactionTime) {
        Map<String, String> params = new HashMap<>();
        params.put("amount", amount);
        params.put("transactionId", transactionId);
        params.put("transactionTime", transactionTime);

        return doGetRequestWithListParams(
            params, PATH_LIMITS_PIX_TRANSACTION_AVAILABILITY.replace("{accountId}", accountId));
    }

    public Response getLimitsPixTransactionAvailability(final String accountId, final String amount,
            final String transactionTime) {
        return getLimitsPixTransactionAvailability(accountId, amount, MathUtil.getRandomUUID(), transactionTime);
    }
}