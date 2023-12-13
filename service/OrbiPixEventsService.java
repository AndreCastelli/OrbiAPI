package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiPixEventsService extends BaseService {
    private static final String SWAGGER_EVENT_DICT = "/event/dict";
    private static final String SWAGGER_EVENT_TRANSACTION = "/event/transaction";
    private static final String DIC_TRANSACTION_RESOURCE = "/v1/events/dict/transaction";
    private static final String TRANSACTION_RESOURCE = "/v1/events/transaction";

    String transactionExternalIdField = "transactionExternalId";
    String transactionIdField = "transactionId";
    JsonUtil jsonUtil;

    public OrbiPixEventsService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.events.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response createEventDictPIX(String entryType, String eventType, String transactionDate,
                                       String transactionExternalId, String transactionId, String transactionType) {
        return doPostRequestJson(createEventDictPIXPayload(entryType, eventType, transactionDate, transactionExternalId,
                transactionId, transactionType), SWAGGER_EVENT_DICT);
    }

    private String createEventDictPIXPayload(
            String entryType, String eventType, String transactionDate, String transactionExternalId,
            String transactionId, String transactionType) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("entryType", entryType);
        requestBody.put("eventType", eventType);
        requestBody.put("transactionDate", transactionDate);
        requestBody.put("transactionExternalId", transactionExternalId);
        requestBody.put("transactionId", transactionId);
        requestBody.put("transactionType", transactionType);

        return requestBody.toString();
    }

    public Response createEventTransacaoPIX(String amount, String eventType, String transactionDate,
                                            String transactionId, String transactionType) {
        return doPostRequestJson(createEventTransacaoPIXPayload(amount, eventType, transactionDate, transactionId,
                transactionType), SWAGGER_EVENT_TRANSACTION);
    }

    private String createEventTransacaoPIXPayload(String amount, String eventType, String transactionDate,
                                                  String transactionId, String transactionType) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", amount);
        requestBody.put("eventType", eventType);
        requestBody.put("transactionDate", transactionDate);
        requestBody.put("transactionId", transactionId);
        requestBody.put("transactionType", transactionType);

        return requestBody.toString();
    }
}
