package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class OrbiFinancialTransactionsService extends BaseService {

    private static final String ENDPOINT_VERSION = "/v1";
    private static final String JUDICIAL_ORDER = "/judicial-orders";
    private static final String BLOCK_ENDPOINT = "/block";
    private static final String UNBLOCK_ENDPOINT = "/unblock";

    private final String AMOUNT = "amount";
    private final String ACCOUNT_ID = "accountId";
    private final String EXTERNAL_ID = "externalId";
    private String externalIdGenerator;

    public OrbiFinancialTransactionsService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.financial.transactions.baseURI"));
    }

    /*
    Comentado pois metodo vai ser refatorado em breve
    @janaina.lavarda

    public Map<String, Object> createP2PTransfPayload(String uuidOriginalAccount, String uuidDestinationAccount,
                                                      double value) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", value);
        payload.put("destinationAccount", uuidDestinationAccount);
        payload.put("originalAccount", uuidOriginalAccount);
        payload.put("description", "QA Automation");

        return payload;
    }

    public Response createTransfP2PRequest(String uuidOriginalAccount, String uuidDestinationAccount,
                                           double value) {
        Response response = doPostRequestMap(createP2PTransfPayload(uuidOriginalAccount, uuidDestinationAccount,
                value), ENDPOINT_VERSION + TRANSACTION_ENDPOINT + P2P_ENDPOINT);

        return response;
    } */

    public String generateExternalId() {
        externalIdGenerator = String.format("%19d", Math.abs(new Random().nextLong()));

        return externalIdGenerator;
    }

    public Response sendPayloadBlockJudicialOrders(String amount, String accountId,
                                                   String externalIdGenerate) {
        return doPostRequestJson(
                createBlockJudOrdersPayload(amount, accountId, externalIdGenerate),
                ENDPOINT_VERSION + JUDICIAL_ORDER + BLOCK_ENDPOINT);
    }

    public String createBlockJudOrdersPayload(
            String amount, String accountId,
            String externalIdGenerate) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(AMOUNT, amount);
        requestBody.put(ACCOUNT_ID, accountId);
        requestBody.put(EXTERNAL_ID, externalIdGenerate);

        return requestBody.toString();
    }

    public Response sendPayloadUnblockJudicialOrders(String amount, String externalId,
                                                     String accountId) {
        return doPostRequestJson(
                createUnblockJudOrdersPayload(amount, externalId, accountId),
                ENDPOINT_VERSION + JUDICIAL_ORDER + UNBLOCK_ENDPOINT);
    }

    public String createUnblockJudOrdersPayload(
            String amount, String externalId,
            String accountId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(AMOUNT, amount);
        requestBody.put(EXTERNAL_ID, externalId);
        requestBody.put(ACCOUNT_ID, accountId);

        return requestBody.toString();
    }
}