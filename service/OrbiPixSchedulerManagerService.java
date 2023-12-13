package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrbiPixSchedulerManagerService extends BaseService {
    private static final String SCHEDULER_ENDPOINT = "/v1/scheduler";

    private static final String DESTINATION_ACCOUNT_BRANCH = "destinationAccountBranch";
    private static final String DESTINATION_ACCOUNT_NUMBER = "destinationAccountNumber";
    private static final String DESTINATION_ACCOUNT_TYPE = "destinationAccountType";
    private static final String DESTINATION_DOCUMENT = "destinationDocument";
    private static final String DESTINATION_ENTITY_TYPE = "destinationEntityType";
    private static final String DESTINATION_ISBP = "destinationIsbp";
    private static final String DESTINATION_NAME = "destinationName";
    private static final String BRANCH_0001 = "0001";
    private static final String PAYMENT = "PAYMENT";
    private static final String NATURAL_PERSON = "NATURAL_PERSON";
    private static final String ISBP_27351731 = "27351731";
    private static final String ACCOUNT_ID = "accountId";
    private static final String PAYMENT_DESCRIPTION = "paymentDescription";
    private static final String PAYMENT_DATE = "paymentDate";
    private static final String DOCUMENT_VALUE = "documentValue";
    private static final String PAYMENT_AMOUNT = "paymentAmount";
    private static final String IBGE_TOWN_CODE = "ibgeTownCode";
    private static final String DESTINATION_DATA = "destinationData";
    private static final String DESTINATION_KEY_TYPE = "destinationKeyType";
    private static final String DESTINATION_KEY_VALUE = "destinationKeyValue";
    private static final String SYMBOL_BAR = "/";
    private static final String REQUEST_DATATIME = "requestDateTime";

    DateTimeUtil dateTimeUtil;

    public OrbiPixSchedulerManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.scheduler.manager.baseURI"));
        dateTimeUtil = new DateTimeUtil();
    }

    public String getDateScheduler(long days) {
        return LocalDateTime.now().plusDays(days).toString();
    }

    public String payloadSchedulerWithPixKey(String accountId, String keyType, String keyValue, String accountNumber,
                                                                    String name, String personDocument, int days) {
        JSONObject requestBodyDestination = new JSONObject();
        requestBodyDestination.put(DESTINATION_ACCOUNT_BRANCH, BRANCH_0001);
        requestBodyDestination.put(DESTINATION_ACCOUNT_NUMBER, accountNumber);
        requestBodyDestination.put(DESTINATION_ACCOUNT_TYPE, PAYMENT);
        requestBodyDestination.put(DESTINATION_DOCUMENT, personDocument);
        requestBodyDestination.put(DESTINATION_ENTITY_TYPE, NATURAL_PERSON);
        requestBodyDestination.put(DESTINATION_ISBP, ISBP_27351731);
        requestBodyDestination.put(DESTINATION_KEY_TYPE, keyType);
        requestBodyDestination.put(DESTINATION_KEY_VALUE, keyValue);
        requestBodyDestination.put(DESTINATION_NAME, name);

        JSONObject requestBody = new JSONObject();
        requestBody.put(ACCOUNT_ID, accountId);
        requestBody.put(PAYMENT_DESCRIPTION, "Teste agend");
        requestBody.put(PAYMENT_DATE, getDateScheduler(days));
        requestBody.put(DOCUMENT_VALUE, 2);
        requestBody.put(PAYMENT_AMOUNT, 2);
        requestBody.put(IBGE_TOWN_CODE, "262");
        requestBody.put(DESTINATION_DATA, requestBodyDestination);
        requestBody.put(REQUEST_DATATIME, getDateScheduler(0));

        return requestBody.toString();
    }

    public Response sendSchedulerWithPixKey(
            String accountId, String amount, String accountNumber, String personDocument,
            String emv, String name, int days) {
        return doPostRequestJson(
                payloadSchedulerWithPixKey(accountId, amount, accountNumber, personDocument,
                        emv, name, days), SCHEDULER_ENDPOINT);
    }

    public Response getScheduler(String transactionIdScheduler) {
        return doGetWithoutParameters(SCHEDULER_ENDPOINT + SYMBOL_BAR + transactionIdScheduler);
    }

    public Response sendSchedulerWithPixAccountNumber(
            String accountId, String accountNumber, String personDocument,
            String name, int days) {
        return doPostRequestJson(
                payloadSchedulerWithPixAccountNumber(accountId, accountNumber, personDocument,
                        name, days), SCHEDULER_ENDPOINT);
    }

    public String payloadSchedulerWithPixAccountNumber(String accountId, String accountNumber,
                                             String name, String personDocument, int days) {
        JSONObject requestBodyDestination = new JSONObject();
        requestBodyDestination.put(DESTINATION_ACCOUNT_BRANCH, BRANCH_0001);
        requestBodyDestination.put(DESTINATION_ACCOUNT_NUMBER, accountNumber);
        requestBodyDestination.put(DESTINATION_ACCOUNT_TYPE, PAYMENT);
        requestBodyDestination.put(DESTINATION_DOCUMENT, personDocument);
        requestBodyDestination.put(DESTINATION_ENTITY_TYPE, NATURAL_PERSON);
        requestBodyDestination.put(DESTINATION_ISBP, ISBP_27351731);
        requestBodyDestination.put(DESTINATION_NAME, name);

        JSONObject requestBody = new JSONObject();
        requestBody.put(ACCOUNT_ID, accountId);
        requestBody.put(PAYMENT_DESCRIPTION, "Teste agendamento");
        requestBody.put(PAYMENT_DATE, getDateScheduler(days));
        requestBody.put(DOCUMENT_VALUE, 2);
        requestBody.put(PAYMENT_AMOUNT, 2);
        requestBody.put(IBGE_TOWN_CODE, "123");
        requestBody.put(DESTINATION_DATA, requestBodyDestination);
        requestBody.put(REQUEST_DATATIME, getDateScheduler(0));

        return requestBody.toString();
    }

    public Response sendCancelSchedulerWithPixKey(String transactionIdScheduler) {
        return doPostRequestNoPayload(SCHEDULER_ENDPOINT + SYMBOL_BAR + transactionIdScheduler + "/cancel");
    }

    public Response getSchedulerByAccountId(String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID, accountId);

        return doGetRequestWithListParams(params, SCHEDULER_ENDPOINT);
    }
}