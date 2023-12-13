package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrbiX2XTransactionManagerService extends BaseService {
    private static final String TRANSACTION_X2X_ENDPOINT = "/v1/transactions";
    private static final String SYMBOL_SLASH = "/";
    private static final String CHARGEBACK_X2X_ENDPOINT = "/chargeback";
    private static final String ADDITIONAL_PROP_1 = "additionalProp1";
    private static final String ADDITIONAL_PROP_2 = "additionalProp2";
    private static final String ADDITIONAL_PROP_3 = "additionalProp3";
    private static final String DESTINATION_ACCOUNT_ID = "destinationAccountId";
    private static final String ORIGIN_ACCOUNT_ID = "originAccountId";
    private static final String BACKEND = "BACKEND";
    private static final String CHANNEL = "CHANNEL";
    private static final String TRANSACTION_AMOUNT = "transactionAmount";
    private static final String TRANSACTION_DATE = "transactionDate";
    private static final String TRANSACTION_DESCRIPTION = "transactionDescription";
    private static final String TRANSACTION_METADATA = "transactionMetadata";
    private static final String TRANSACTION_STATUS = "transactionStatus";
    private static final String INITIAL_DATE = "initialDate";
    private static final String FINAL_DATE = "finalDate";
    private static final String BASE_URI = "orbi.x2x.transaction.manager.baseURI";
    DateTimeUtil dateTimeUtil;

    public OrbiX2XTransactionManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));
        dateTimeUtil = new DateTimeUtil();
    }

    public String getDateTime() {
        return dateTimeUtil.getDateFormattedIso8601();
    }

    public String getDateTime(String methodToSubtract, int numberOf) {
        return dateTimeUtil.getPreviousDateFormatted(methodToSubtract, numberOf);
    }

    public String getDate() {
        return dateTimeUtil.getDateFormatted();
    }

    public Response sendTransactionX2X(String originalAccountId, String destinationAccountId,
                                       BigDecimal value, String service) {
        Map<String, String> params = new HashMap<>();
        params.put(CHANNEL, BACKEND);

        JSONObject transactionMetadataProp = new JSONObject();
        transactionMetadataProp.put(ADDITIONAL_PROP_1, "");
        transactionMetadataProp.put(ADDITIONAL_PROP_2, "");
        transactionMetadataProp.put(ADDITIONAL_PROP_3, "");

        JSONObject payload = new JSONObject();
        payload.put(ORIGIN_ACCOUNT_ID, originalAccountId);
        payload.put(DESTINATION_ACCOUNT_ID, destinationAccountId);
        payload.put(TRANSACTION_AMOUNT, value);
        payload.put(TRANSACTION_DATE, getDateTime());
        payload.put(TRANSACTION_DESCRIPTION, "transferencia x2x entre contas");
        payload.put(TRANSACTION_METADATA, transactionMetadataProp);
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doPostRequestJsonWithHeaderParam(payload.toString(),
                TRANSACTION_X2X_ENDPOINT + SYMBOL_SLASH + service, params);
    }

    public Response makeTransactionChargebackX2X(String originalTransactionId, String service) {
        Map<String, String> params = new HashMap<>();
        params.put(CHANNEL, BACKEND);

        JSONObject transactionChargebackMetadataProp = new JSONObject();
        transactionChargebackMetadataProp.put(ADDITIONAL_PROP_1, "");
        transactionChargebackMetadataProp.put(ADDITIONAL_PROP_2, "");
        transactionChargebackMetadataProp.put(ADDITIONAL_PROP_3, "");

        JSONObject payload = new JSONObject();
        payload.put("originalTransactionId", originalTransactionId);
        payload.put(TRANSACTION_DATE, getDateTime());
        payload.put(TRANSACTION_DESCRIPTION, "transacao chargeback");
        payload.put(TRANSACTION_METADATA, transactionChargebackMetadataProp);
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doPostRequestJsonWithHeaderParam(payload.toString(),
                TRANSACTION_X2X_ENDPOINT + SYMBOL_SLASH + service + CHARGEBACK_X2X_ENDPOINT, params);
    }

    public Response getTransactionX2X(String transactionStatus, float transactionAmount, String service) {
        Map<String, Object> map = new HashMap<>();
        map.put(INITIAL_DATE, getDateTime("hours", 1));
        map.put(FINAL_DATE, getDateTime());
        map.put(TRANSACTION_AMOUNT, transactionAmount);
        map.put(CHANNEL, BACKEND);
        map.put(TRANSACTION_STATUS, transactionStatus);

        return doGetRequestWithListParams(map, TRANSACTION_X2X_ENDPOINT + SYMBOL_SLASH + service);
    }

    public Response getTransactionX2XInvalidDate(String service, String initialDate, String finalDate) {
        Map<String, Object> map = new HashMap<>();
        map.put(INITIAL_DATE, initialDate);
        map.put(FINAL_DATE, finalDate);
        return doGetRequestWithListParams(map, TRANSACTION_X2X_ENDPOINT + SYMBOL_SLASH + service);
    }
}
