package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OrbiPixTransactionManagerService extends BaseService {
    private static final String TRANSACTION_PAYMENT_ENDPOINT = "/v1/transaction/payment";
    private static final String TRANSACTION_STATUS_TRANSACTIONID_ENDPOINT = "/v1/transaction/status/by/transaction-id/";
    private static final String TRANSACTION_STATUS_ENDTOENDID_ENDPOINT = "/v1/transaction/status/by/end-to-end-id/";
    private static final String TRANSACTION_PIX_QRCODE_ENDPOINT = "/v1/transaction/qrcode";
    private static final String TRANSACTION_PIX_DEVOLUTION = "/v1/transaction/devolution";
    private static final String ISPB_NUMBER = "27351731";
    private static final String NATURAL_PERSON = "NATURAL_PERSON";
    private static final String PAYMENT = "PAYMENT";
    private static final String BRANCH = "0001";
    private static final String IBGE_NUMBER = "1234";
    private static final String ACCOUNTID = "consultantAccountId";
    private static final String CHANNEL = "Channel";
    private static final String BACKEND = "BACKEND";
    private static final String DESTINATION_ACCOUNT_BRANCH = "destinationAccountBranch";
    private static final String DESTINATION_ACCOUNT_NUMBER_WITH_DIGIT = "destinationAccountNumberWithDigit";
    private static final String DESTINATION_ACCOUNT_TYPE = "destinationAccountType";
    private static final String DESTINATION_DOCUMENT = "destinationDocument";
    private static final String DESTINATION_ENTITY_TYPE = "destinationEntityType";
    private static final String DESTINATION_ISBP = "destinationIsbp";
    private static final String DESTINATION_NAME = "destinationName";

    private static final String FIELD_ACCOUNT_ID = "accountId";
    private static final String FIELD_PAYMENT_AMOUNT = "paymentAmount";
    private static final String FIELD_PAYMENT_DATE = "paymentDate";
    private static final String FIELD_REQUEST_DATE = "requestDateTime";
    private static final String FIELD_PAYMENT_DESCRIPTION = "paymentDescription";
    private static final String FIELD_IBGE_TOWN_CODE = "ibgeTownCode";
    private static final String FIELD_DESTINATION_DATA = "destinationData";

    private static final String FIELD_PAYMENT_STATUS = "paymentStatus";
    private static final String FIELD_CONFIRMED = "CONFIRMED";
    private static final String FIELD_CANCELED = "CANCELED";

    private static final String FIELD_DEVOLUTION_AMOUNT = "devolutionAmount";
    private static final String FIELD_DEVOLUTION_DATE = "devolutionDate";
    private static final String FIELD_DEVOLUTION_DESCRIPTION = "devolutionDescription";
    private static final String FIELD_ORIGIN_ADJUSTMENT_ID = "originAdjustmentId";
    private static final String FIELD_ORIGIN_END_TO_END_ID = "originEndToEndId";

    private static final String ERROR = "Log de Erro:";

    private static Response transactionStatusResp;
    DateTimeUtil dateTimeUtil;

    public OrbiPixTransactionManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.transaction.manager.baseURI"));
        dateTimeUtil = new DateTimeUtil();
    }

    public String getDateTime() {
        return dateTimeUtil.getDateFormattedIso8601();
    }

    public String getDateTimeDays(long days) {
        return LocalDateTime.now().plusDays(days).toString();
    }

    public String createTransactionPix(String accountId, String amount, String accountNumber,
                                               String personDocument, String keyType, String keyValue, String name) {
        JSONObject payload = new JSONObject();
        payload.put(DESTINATION_ACCOUNT_BRANCH, BRANCH);
        payload.put(DESTINATION_ACCOUNT_NUMBER_WITH_DIGIT, accountNumber);
        payload.put(DESTINATION_ACCOUNT_TYPE, PAYMENT);
        payload.put(DESTINATION_DOCUMENT, personDocument);
        payload.put(DESTINATION_ENTITY_TYPE, NATURAL_PERSON);
        payload.put(DESTINATION_ISBP, ISPB_NUMBER);
        payload.put("destinationKeyType", keyType);
        payload.put("destinationKeyValue", keyValue);
        payload.put(DESTINATION_NAME, name);

        JSONObject parameters = new JSONObject();
        parameters.put(FIELD_ACCOUNT_ID, accountId);
        parameters.put(FIELD_PAYMENT_AMOUNT, amount);
        parameters.put(FIELD_PAYMENT_DATE, getDateTime());
        parameters.put(FIELD_REQUEST_DATE, getDateTime());
        parameters.put(FIELD_PAYMENT_DESCRIPTION, "Teste Automacao Transacao Pix");
        parameters.put(FIELD_IBGE_TOWN_CODE, IBGE_NUMBER);
        parameters.put(FIELD_DESTINATION_DATA, payload);

        return parameters.toString();
    }

    public String createTransactionPixQrCode(String accountId, String amount, String accountNumber,
                                                               String personDocument, String emv, String name) {
        JSONObject payload = new JSONObject();
        payload.put(DESTINATION_ACCOUNT_BRANCH, BRANCH);
        payload.put(DESTINATION_ACCOUNT_NUMBER_WITH_DIGIT, accountNumber);
        payload.put(DESTINATION_ACCOUNT_TYPE, PAYMENT);
        payload.put(DESTINATION_DOCUMENT, personDocument);
        payload.put(DESTINATION_ENTITY_TYPE, NATURAL_PERSON);
        payload.put(DESTINATION_ISBP, ISPB_NUMBER);
        payload.put("destinationQrCodeEMV", emv);
        payload.put("destinationQrCodeType", "STATIC");
        payload.put(DESTINATION_NAME, name);

        JSONObject parameters = new JSONObject();
        parameters.put(FIELD_ACCOUNT_ID, accountId);
        parameters.put(FIELD_PAYMENT_AMOUNT, amount);
        parameters.put("documentValue", amount);
        parameters.put(FIELD_PAYMENT_DATE, getDateTime());
        parameters.put(FIELD_REQUEST_DATE, getDateTime());
        parameters.put(FIELD_PAYMENT_DESCRIPTION, "Teste Automacao Transacao QRCODE Pix");
        parameters.put(FIELD_IBGE_TOWN_CODE, IBGE_NUMBER);
        parameters.put(FIELD_DESTINATION_DATA, payload);

        return parameters.toString();
    }

    public String createTransactionPixAccount(
            String accountId, String amount, String accountNumber, String personDocument, String name) {
        JSONObject payload = new JSONObject();
        payload.put(DESTINATION_ACCOUNT_BRANCH, BRANCH);
        payload.put(DESTINATION_ACCOUNT_NUMBER_WITH_DIGIT, accountNumber);
        payload.put(DESTINATION_ACCOUNT_TYPE, PAYMENT);
        payload.put(DESTINATION_DOCUMENT, personDocument);
        payload.put(DESTINATION_ENTITY_TYPE, NATURAL_PERSON);
        payload.put(DESTINATION_ISBP, ISPB_NUMBER);
        payload.put(DESTINATION_NAME, name);

        JSONObject parameters = new JSONObject();
        parameters.put(FIELD_ACCOUNT_ID, accountId);
        parameters.put(FIELD_PAYMENT_AMOUNT, amount);
        parameters.put(FIELD_PAYMENT_DATE, getDateTime());
        parameters.put(FIELD_REQUEST_DATE, getDateTime());
        parameters.put(FIELD_PAYMENT_DESCRIPTION, "Teste Automacao Transacao por conta Pix");
        parameters.put(FIELD_IBGE_TOWN_CODE, IBGE_NUMBER);
        parameters.put(FIELD_DESTINATION_DATA, payload);

        return parameters.toString();
    }

    public Response sendTransactionPix(
            String accountId, String amount, String accountNumber, String personDocument,
            String keyType, String keyValue, String name) {
        return doPostRequestJson(
                createTransactionPix(accountId, amount, accountNumber, personDocument, keyType,
                        keyValue, name), TRANSACTION_PAYMENT_ENDPOINT);
    }

    public Response sendTransactionPixQrCode(
            String accountId, String amount, String accountNumber, String personDocument,
            String emv, String name) {
        return doPostRequestJson(
                createTransactionPixQrCode(accountId, amount, accountNumber, personDocument,
                        emv, name), TRANSACTION_PAYMENT_ENDPOINT);
    }

    public Response sendTransactionPixAccount(
            String accountId, String amount, String accountNumber, String personDocument, String name) {
        return doPostRequestJson(
                createTransactionPixAccount(
                        accountId, amount, accountNumber, personDocument, name), TRANSACTION_PAYMENT_ENDPOINT);
    }

    public Response getTransactionPix(String transactionId) {
        return doGetWithoutParameters(TRANSACTION_STATUS_TRANSACTIONID_ENDPOINT + transactionId);
    }

    public Response getEndtoEndPix(String endToEndId) {
        return doGetWithoutParameters(TRANSACTION_STATUS_ENDTOENDID_ENDPOINT + endToEndId);
    }

    public Response getQrCode(String emv, String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("emvPayload", emv);
        params.put(ACCOUNTID, accountId);

        return doGetRequestWithListParams(params, TRANSACTION_PIX_QRCODE_ENDPOINT);
    }

    public Response createDevolution(String accountId, float devolutionAmount,
                                     String endToEndId, String originAdjustmentId, int days) {
        Map<String, String> params = new HashMap<>();
        params.put(CHANNEL, BACKEND);

        return doPostRequestMapAndQueryParamsTypeJson(payloadDevolution(
                accountId, devolutionAmount, endToEndId, originAdjustmentId, days), params, TRANSACTION_PIX_DEVOLUTION);
    }

    public String payloadDevolution(String accountId, float devolutionAmount,
                                    String endToEndId, String originAdjustmentId, int days) {
        String devAmount = String.valueOf(devolutionAmount);

        JSONObject requestDevolution = new JSONObject();

        requestDevolution.put(FIELD_ACCOUNT_ID, accountId);
        requestDevolution.put(FIELD_DEVOLUTION_AMOUNT, devAmount);
        requestDevolution.put(FIELD_DEVOLUTION_DATE, getDateTimeDays(days));
        requestDevolution.put(FIELD_DEVOLUTION_DESCRIPTION, "teste devolucao");
        requestDevolution.put(FIELD_ORIGIN_ADJUSTMENT_ID, originAdjustmentId);
        requestDevolution.put(FIELD_ORIGIN_END_TO_END_ID, endToEndId);
        requestDevolution.put(FIELD_REQUEST_DATE, getDateTime());

        return requestDevolution.toString();
    }

    public ArrayList<Float> getArrayListWithValuesDevolution(float amount1, float amount2, float amount3) {
        ArrayList<Float> amountValues = new ArrayList<>();
        amountValues.add(amount1);
        amountValues.add(amount2);
        amountValues.add(amount3);

        return amountValues;
    }

    public void executeAllPartialDevolution(ArrayList<Float> amountValuesToDevolution, String accountId,
                                            String endToEndId, String originAdjustmentId, int days) throws IOException {
        for (int i = 0; i <= 2; i++) {
            float selectAmount = amountValuesToDevolution.get(i);
            RequestManager.shared().setResponse(new OrbiPixTransactionManagerService().createDevolution(
                    accountId, selectAmount, endToEndId, originAdjustmentId, days));
        }
    }

    public Response waitForTransactionConfirm(String transactionId) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateTransactionConfirm(transactionId));
        } catch (ExceptionInInitializerError e) {
            System.out.println(ERROR + e);
        }

        return transactionStatusResp;
    }

    public boolean validateTransactionConfirm(String transactionId) {
        transactionStatusResp = getTransactionPix(transactionId);
        Assert.assertEquals(HttpStatus.SC_OK, transactionStatusResp.statusCode());

        return Objects.equals(transactionStatusResp.getBody().jsonPath().getString(
                FIELD_PAYMENT_STATUS), FIELD_CONFIRMED);
    }

    public Response waitForTransactionEndToEndConfirm(String endToEndId) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateTransactionEndToEndConfirm(endToEndId));
        } catch (ExceptionInInitializerError e) {
            System.out.println(ERROR + e);
        }

        return transactionStatusResp;
    }

    public boolean validateTransactionEndToEndConfirm(String endToEndId) {
        transactionStatusResp = getEndtoEndPix(endToEndId);
        Assert.assertEquals(HttpStatus.SC_OK, transactionStatusResp.statusCode());

        return Objects.equals(transactionStatusResp.getBody().jsonPath().getString(
                FIELD_PAYMENT_STATUS), FIELD_CONFIRMED);
    }

    public Response createDevolutionWithError(
            String accountId, String devolutionAmount,
            String originEndToEndId, String originAdjustmentId) {
        Map<String, String> params = new HashMap<>();
        params.put(CHANNEL, BACKEND);

        return doPostRequestMapAndQueryParamsTypeJson(payloadDevolutionWithError(
                accountId, devolutionAmount, originEndToEndId, originAdjustmentId), params, TRANSACTION_PIX_DEVOLUTION);
    }

    public String payloadDevolutionWithError(String accountId, String devolutionAmount, String originEndToEndId,
                                                                                           String originAdjustmentId) {
        JSONObject requestDevolution = new JSONObject();
        requestDevolution.put(FIELD_ACCOUNT_ID, accountId);
        requestDevolution.put(FIELD_DEVOLUTION_AMOUNT, devolutionAmount);
        requestDevolution.put(FIELD_DEVOLUTION_DATE, getDateTime());
        requestDevolution.put(FIELD_DEVOLUTION_DESCRIPTION, "teste erro devolucao");
        requestDevolution.put(FIELD_ORIGIN_ADJUSTMENT_ID, originAdjustmentId);
        requestDevolution.put(FIELD_ORIGIN_END_TO_END_ID, originEndToEndId);
        requestDevolution.put(FIELD_REQUEST_DATE, getDateTime());

        return requestDevolution.toString();
    }

    public Response waitForTransactionCanceled(String transactionId) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateTransactionCanceled(transactionId));
        } catch (ExceptionInInitializerError e) {
            System.out.println(ERROR + e);
        }

        return transactionStatusResp;
    }

    public Response waitForTransactionWithMessage(String transactionId, String message) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateTransactionWithMessage(transactionId, message));
        } catch (ExceptionInInitializerError e) {
            System.out.println(ERROR + e);
        }

        return transactionStatusResp;
    }

    public boolean validateTransactionCanceled(String transactionId) {
        transactionStatusResp = getTransactionPix(transactionId);
        Assert.assertEquals(HttpStatus.SC_OK, transactionStatusResp.statusCode());

        return Objects.equals(transactionStatusResp.getBody().jsonPath()
                              .getString(FIELD_PAYMENT_STATUS), FIELD_CANCELED);
    }

    public boolean validateTransactionWithMessage(String transactionId, String message) {
        transactionStatusResp = getTransactionPix(transactionId);
        Assert.assertEquals(HttpStatus.SC_OK, transactionStatusResp.statusCode());

        return Objects.equals(transactionStatusResp.getBody().jsonPath().getString(
                "motivationDescription"), message);
    }
}