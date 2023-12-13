package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DockConnectorService extends BaseService {

    private static final String CARDS_RESOURCE = "/v1/card/";
    private static final String JSON_FILE_NAME = "IndividualData.json";
    private static final String DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT = "/v1/individuals";
    private static final String DOCK_CONNECTOR_INDIVIDUALS_RATE_ENDPOINT = "/v1/individuals/rate";
    private static final String EMPTY_PAYLOAD = "{}";
    private static final String PAYLOAD_INDIVIDUALS_REGISTRATION
            = "dockconnectorindividuals/individualCreatePayload.json";
    private static final String PAYLOAD_INDIVIDUALS_RATE_REGISTRATION
            = "dockconnectorindividuals/individualRateCreatePayload.json";
    private static final String SEPARATOR = "/";
    private static final String SEPARATOR_DOCUMENT = "/?document=";
    private static final String EMAIL = "email";
    private static final String DOCUMENT = "document";
    private static final String ADDRESS = "/address";
    private static final String PHONE = "/phone";
    private static final String BLOCK = "/block";
    private static final String CANCEL = "/cancel";
    private static final String ACCOUNT_ENDPOINT = "/account";
    private static final String ACCOUNT_BALANCE_RESOURCE = "/account/balance";
    private static final String ACCOUNT_RESOURCE = "/account/";
    private static final String TRANSACTIONS_RESOURCE = "/transactions";
    // private static final int ID_PERSON_VALUE = 16915;
    private static final int ID_MAILING_ADRESS_VALUE = 16821;
    private static final int LIMIT_VALUE = 50;
    private static final int DUE_DATE_FIXED = 10;
    private static final int INCOME_FIXED = 100;
    private static final String V2_ACCOUNT_ENDPOINT = "/v2/account";
    private static final String ANTIFRAUD_ENDPOINT = "/v1/antifraud/retail-banking";
    private static final String DYNAMIC_CVV_ENDPOINT = "/dynamic-cvv";
    private static final String DYNAMIC_CVV_ENDPOINT_URL = "/v1/card/{cardId}/dynamic-cvv";
    private static final String LIST_FINANCIAL_ADJUSTMENTS = "/account/list-financial-adjustments";

    private static Response accountResp;
    private static Response cardResp;
    private static Response adjustmentsResp;

    DockClientUtil dockClientUtil;
    DateTimeUtil dateTimeUtil;

    String startDateField = "startDate";
    String endDateField = "endDate";
    String limitField = "limit";

    String blockedField = "blocked";
    String externalAccountIdField = "externalAccountId";
    String validationDateField = "validationDate";

    String transactionField = "transaction";
    String dateField = "date";

    String dueDateField = "dueDate";
    String idCommercialOriginField = "idCommercialOrigin";
    String idMailingAddressField = "idMailingAddress";
    String idPersonField = "idPerson";
    String idProductField = "idProduct";
    String incomeField = "income";
    String pointsField = "points";

    String idStatusWishlistField = "idStatusWishlist";
    String pageField = "page";
    String dateFormater = "yyyy-MM-dd";

    String accountBankIdField = "accountBankId";
    String accountBranchField = "accountBranch";
    String accountCountryField = "accountCountry";
    String accountNumberField = "accountNumber";
    String accountSegmentLevelField = "accountSegmentLevel";
    String amountField = "amount";
    String currencyField = "currency";
    String customerDocumentField = "customerDocument";
    String customerNameField = "customerName";

    String ispb = "27351731";
    String branch = "0001";
    String country = "BRAZIL";
    String transaction = "TRANSACTION";
    String brl = "BRL";

    public DockConnectorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        dockClientUtil = new DockClientUtil();
        dateTimeUtil = new DateTimeUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.connector.baseURI"));
    }

    public Response getAccountData(String cpf) {
        return doGetRequestOneAttribute("/individuals?document={cpf}", cpf);
    }

    public Response getAccountById(String accountID) {
        return doGetRequestOneAttribute("/account/{accountID}", accountID);
    }

    public Response getAccountByIdWithWronUrl(String accountID) {
        return doGetRequestOneAttribute("/accounts/{accountID}", accountID);
    }

    public Response getRechargeDealersById(String dealerCode) {
        return doGetRequestOneAttribute("/recharges/dealers/{dealerCode}", dealerCode);
    }

    public Response getTicketId(String transactionType, String data) {
        Map<String, String> params = new HashMap<>();
        params.put(transactionField, transactionType);
        params.put(dateField, data);

        return doGetRequestWithListParams(params, "/data-extraction/transactions");
    }

    public Response getTicketStatus(String ticket) {
        return doGetRequestOneAttribute("data-extraction/tickets/{ticket}", ticket);
    }

    public void waitForAccountCreation(String cpf) {
        Awaitility.await().atMost(MathUtil.TWENTY, SECONDS).until(() -> validateDigitalAccountCreation(cpf));
    }

    public boolean validateDigitalAccountCreation(String cpf) {
        return getAccountData(cpf).statusCode() == HttpStatus.SC_OK;
    }

    public Response waitForCardsCreationprovisional(String externalAccountId) {
        Awaitility.await().atMost(
                MathUtil.TWENTY, SECONDS).until(() -> validateDigitalCardsCreation(externalAccountId));

        return accountResp;
    }

    public boolean validateDigitalCardsCreation(String externalAccountId) {
        cardResp = createProvisoryCard(externalAccountId);

        return cardResp.statusCode() == HttpStatus.SC_OK;
    }

    public Response createProvisoryCard(String externalAccountId) {
        return doPostRequestNoPayload("/v1/card/provisory?externalAccountId=" + externalAccountId);
    }

    public Response createDockVirtualCard(String isBlocked, String externalAccountId) {
        return doPostRequestMap(createJsonPayload(isBlocked, externalAccountId), "/v1/card/virtual");
    }

    public Map<String, String> createJsonPayload(String isBlocked, String externalAccountId) {
        Map<String, String> payload = new HashMap<>();
        if(isBlocked != null) {
            payload.put(blockedField, getIsBlockedFlag(isBlocked));
        }
        payload.put(externalAccountIdField, externalAccountId);
        payload.put(validationDateField, dateTimeUtil.getDateFormattedToValidationDateField());

        return payload;
    }

    public String getIsBlockedFlag(String isBlocked) {
        if ("ativado".equals(isBlocked)) {
            return "false";
        } else if ("desativado".equals(isBlocked)) {
            return "true";
        } else {
            throw new RuntimeException("Status para o cartão inválido");
        }
    }

    public Map<String, Object> createAccountTransactionQueryParam(String startDate,
                                                                  String endDate, Integer limit, Integer page) {
        Map<String, Object> params = new HashMap<>();
        params.put(startDateField, startDate);
        params.put(endDateField, endDate);
        params.put(limitField, limit);
        params.put(pageField, page);

        return params;
    }

    public Response waitForAccountCreationDock(Response person) {
        Awaitility.await().atMost(MathUtil.TWENTY, SECONDS).until(() -> validateDigitalAccountCreationDock(person));

        return accountResp;
    }

    public boolean validateDigitalAccountCreationDock(Response person) {
        accountResp = createAccount(person);

        return accountResp.statusCode() == HttpStatus.SC_OK;
    }

    public Response createAccount(Response response) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("dueDate", 10);
        payload.put("idCommercialOrigin", 1);
        payload.put("idMailingAddress", response.then().extract().path("person.address.id").toString());
        payload.put("idPerson", response.then().extract().path("person.dockId").toString());
        payload.put("idProduct", 1);
        payload.put("income", 100);
        payload.put("points", 1);

        return doPostRequestMap(payload, ACCOUNT_RESOURCE);
    }

    public Response createAccount(int idPerson, int idProduct) {
        return doPostRequestMap(createJsonPayloadToCreateAccount(idPerson, idProduct), ACCOUNT_RESOURCE);
    }

    public Response getAccountTransactions(String idAccount, String startDate,
                                           String endDate, Integer limit, Integer page) {
        return doGetRequestWithListParams(createAccountTransactionQueryParam(startDate, endDate, limit, page),
                ACCOUNT_RESOURCE + idAccount + TRANSACTIONS_RESOURCE);
    }

    public Map<String, Object> removeFieldAccountTransactionParam(String mandatoryField,
                                                                  Map<String, Object> queryParam) {
        if (startDateField.equals(mandatoryField)) {
            queryParam.remove(startDateField);
        }

        if (endDateField.equals(mandatoryField)) {
            queryParam.remove(endDateField);
        }

        if (limitField.equals(mandatoryField)) {
            queryParam.remove(limitField);
        }

        if (pageField.equals(mandatoryField)) {
            queryParam.remove(pageField);
        }

        return queryParam;
    }

    public Response getAccountTransactionsWithoutMandatoryFields(String mandatoryField) {
        String idAccount = "4090";

        Map queryParamWithAllMandatoriesParams = createAccountTransactionQueryParam(
                "2020-10-01", "2021-03-30", LIMIT_VALUE, 1);

        return doGetRequestWithListParams(removeFieldAccountTransactionParam(mandatoryField,
                queryParamWithAllMandatoriesParams), ACCOUNT_RESOURCE + idAccount + TRANSACTIONS_RESOURCE);
    }

    public Map<String, Integer> createJsonPayloadToCreateAccount(int idPerson, int idProduct) {
        Map<String, Integer> payload = new HashMap<>();
        payload.put(dueDateField, DUE_DATE_FIXED);
        payload.put(idCommercialOriginField, 1);
        payload.put(idMailingAddressField, ID_MAILING_ADRESS_VALUE);
        payload.put(idPersonField, idPerson);
        payload.put(idProductField, idProduct);
        payload.put(incomeField, INCOME_FIXED);
        payload.put(pointsField, 1);

        return payload;
    }

    public Map<String, Integer> removeFieldFromCreateAccountPayload(
            String mandatoryField, Map<String, Integer> payload) {
        if (dueDateField.equals(mandatoryField)) {
            payload.remove(dueDateField);
        }

        if (idCommercialOriginField.equals(mandatoryField)) {
            payload.remove(idCommercialOriginField);
        }

        if (idMailingAddressField.equals(mandatoryField)) {
            payload.remove(idMailingAddressField);
        }

        if (idPersonField.equals(mandatoryField)) {
            payload.remove(idPersonField);
        }

        if (idProductField.equals(mandatoryField)) {
            payload.remove(idProductField);
        }

        if (incomeField.equals(mandatoryField)) {
            payload.remove(incomeField);
        }

        if (pointsField.equals(mandatoryField)) {
            payload.remove(pointsField);
        }

        return payload;
    }

    /*public Response createAccountWithoutMandatoryField(String mandatoryField) {
        Map payloadWithAllFields = createJsonPayloadToCreateAccount(ID_PERSON_VALUE, 1);

        return doPostRequestMap(
                removeFieldFromCreateAccountPayload(mandatoryField,
                        payloadWithAllFields), ACCOUNT_RESOURCE);
    }*/

    public Map<String, Object> removeParamsFromAccountBalanceQueryParam(
            String mandatoryField, Map<String, Object> queryParam) {
        if (limitField.equals(mandatoryField)) {
            queryParam.remove(limitField);
        }

        if (idStatusWishlistField.equals(mandatoryField)) {
            queryParam.remove(idStatusWishlistField);
        }

        if (pageField.equals(mandatoryField)) {
            queryParam.remove(pageField);
        }

        return queryParam;
    }

    public Map<String, Object> createAccountBalanceParams(Integer limit, Integer page, List<Integer> idStatusWishlist) {
        Map<String, Object> params = new HashMap<>();
        params.put(limitField, limit);
        params.put(idStatusWishlistField, idStatusWishlist);
        params.put(pageField, page);

        return params;
    }

    public Response getBalancePage(Integer limit, Integer page, List<Integer> idStatusWishlist) {
        return doGetRequestWithListParams(createAccountBalanceParams(limit,
                page, idStatusWishlist), ACCOUNT_BALANCE_RESOURCE);
    }

    public Response getBalancePageWithoutMandatoryFields(String mandatoryField) {
        Map createAccountBalanceParams = createAccountBalanceParams(LIMIT_VALUE, 1, Collections.singletonList(0));

        return doGetRequestWithListParams(
                removeParamsFromAccountBalanceQueryParam(mandatoryField, createAccountBalanceParams),
                ACCOUNT_BALANCE_RESOURCE);
    }

    public Response getIndividualByCpf(String cpf) {
        return doGetRequestJson(EMPTY_PAYLOAD, DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT + SEPARATOR_DOCUMENT + cpf);
    }

    public String individualRegistrationPayload(PersonUtil person) throws IOException {
        String payloadString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + PAYLOAD_INDIVIDUALS_REGISTRATION);

        payloadString = updatePayload("name", person.getName(), payloadString);
        payloadString = updatePayload(EMAIL, person.getEmail(), payloadString);
        payloadString = updatePayload(DOCUMENT, person.getCpf(), payloadString);
        payloadString = updatePayload("motherName", person.getMother(), payloadString);

        return payloadString;
    }

    public String updatePayload(String attributeName, String attributeValue, String payloadString) {
        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return jsonBody.toString();
    }

    public Response sendIndividualRegistration(String payload) {
        return doPostRequestJson(payload, DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT);
    }

    public String payloadOnlyWithMandatoryData(String payload) {
        String payloadString = payload;

        payloadString = updatePayload("incomeValue", "", payloadString);
        payloadString = updatePayload("isPep", "", payloadString);
        payloadString = updatePayload("fatherName", "", payloadString);
        payloadString = updatePayload("mailingAddress", "", payloadString);
        payloadString = updatePayload("address.complement", "", payloadString);
        payloadString = updatePayload("address.reference", "", payloadString);

        return payloadString;
    }

    public String individualRateRegistrationPayload(String attributeName, String attributeValue, String idPerson)
            throws IOException {
        String payloadString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + PAYLOAD_INDIVIDUALS_RATE_REGISTRATION);

        payloadString = updatePayload("personId", idPerson, payloadString);
        payloadString = updatePayload(attributeName, attributeValue, payloadString);

        return payloadString;
    }

    public JSONObject updatePhoneRegistrationPayload(String ddd, String number)
            throws IOException {
        JSONObject payload = new JSONObject();

        payload.put("ddd", ddd);
        payload.put("number", number);
        payload.put("type", "CELLPHONE");

        return payload;
    }

    public Response sendIndividualUpdatePhoneRegistration(String payload, Integer personId, Integer id) {
        return doPutRequestJson(payload,
                DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT + SEPARATOR + personId + SEPARATOR + PHONE + SEPARATOR  + id );
    }

    public Response sendIndividualRateRegistration(String payload) {
        return doPostRequestJson(payload, DOCK_CONNECTOR_INDIVIDUALS_RATE_ENDPOINT);
    }

    public Response getAddressIndividualById(String idDock) {
        return doGetRequestJson(EMPTY_PAYLOAD, DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT + SEPARATOR + idDock + ADDRESS);
    }

    public Response getPhoneIndividualById(String idDock) {
        return doGetRequestJson(EMPTY_PAYLOAD, DOCK_CONNECTOR_INDIVIDUALS_V1_ENDPOINT + SEPARATOR + idDock + PHONE);
    }

    public Response getRegisteredAccountsPerDay(String accountStatusId, String registerDate) {
        return doGetRequestQuery(consultAccountListParameter(accountStatusId, registerDate), V2_ACCOUNT_ENDPOINT);
    }

    public String getRegisteredAccounts() {
        JSONArray registeredAccountsList = new JSONArray();

        for (int i = 3; i > 0; i--) {
            Response response = doGetRequestQuery(
                    consultAccountQueryParam(new DateTimeUtil().addOrSubtractDaysInADate(
                            Integer.parseInt("-" + i), dateFormater)), V2_ACCOUNT_ENDPOINT);
            JSONObject registeredResp = new JSONObject(response.getBody().asString());
            System.out.println(registeredResp);
            JSONArray registeredArray = registeredResp.getJSONArray("content");

            for (int j = 0; j < registeredArray.length(); j++) {
                JSONObject jsonItem = registeredArray.getJSONObject(j);
                JSONObject registeredJson = new JSONObject();
                registeredJson.put("id", jsonItem.get("id"));
                registeredJson.put("personId", jsonItem.get("personId"));
                registeredJson.put("registerDate", jsonItem.getString("registerDate"));
                registeredAccountsList.put(registeredJson);
            }
        }

        return registeredAccountsList.toString();
    }

    public String getRegisteredAccountsJobFailure() throws ParseException {
        JSONArray registeredAccountsList = new JSONArray();

        for (int i = 3; i > 0; i--) {
            String referenceDate = new DateTimeUtil().addOrSubtractDaysInADate(
                    Integer.parseInt("-" + i), dateFormater);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(referenceDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                Response response = doGetRequestQuery(
                        consultAccountQueryParam(referenceDate), V2_ACCOUNT_ENDPOINT);
                JSONObject registeredResp = new JSONObject(response.getBody().asString());
                response.then().log().all();
                System.out.println(registeredResp);
                JSONArray registeredArray = registeredResp.getJSONArray("content");

                for (int j = 0; j < registeredArray.length(); j++) {
                    JSONObject jsonItem = registeredArray.getJSONObject(j);
                    JSONObject registeredJson = new JSONObject();
                    registeredJson.put("id", jsonItem.get("id"));
                    registeredJson.put("personId", jsonItem.get("personId"));
                    registeredJson.put("registerDate", jsonItem.getString("registerDate"));
                    registeredAccountsList.put(registeredJson);
                }
            } else {
                System.out.println("é Domingo não consulta" + date);
            }
        }

        return registeredAccountsList.toString();
    }

    public String getRegisteredAccountsCurrentDate() {
        JSONArray registeredAccountsList = new JSONArray();

        for (int i = 1; i >= 0; i--) {
            Response response = doGetRequestQuery(
                    consultAccountQueryParam(new DateTimeUtil().addOrSubtractDaysInADate(
                            Integer.parseInt("-" + i), dateFormater)), V2_ACCOUNT_ENDPOINT);
            JSONObject registeredResp = new JSONObject(response.getBody().asString());
            JSONArray registeredArray = registeredResp.getJSONArray("content");

            for (int j = 0; j < registeredArray.length(); j++) {
                JSONObject jsonItem = registeredArray.getJSONObject(j);
                JSONObject registeredJson = new JSONObject();
                registeredJson.put("id", jsonItem.get("id"));
                registeredJson.put("personId", jsonItem.get("personId"));
                registeredJson.put("registerDate", jsonItem.getString("registerDate"));
                registeredAccountsList.put(registeredJson);
            }
        }

        return registeredAccountsList.toString();
    }

    public Map<String, Object> consultAccountListParameter(String accountStatusId, String dataRegistro) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("accountStatusId", accountStatusId);
        queryParams.put("dataRegistro", dataRegistro);

        return queryParams;
    }

    public Map<String, Object> consultAccountQueryParam(String dataRegistro) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("registerDate", dataRegistro);

        return queryParams;
    }

    public Response getCardsByExternalAccountId(String externalAccountId, String ordenacao) {
        Map<String, String> params = new HashMap<>();
        params.put(externalAccountIdField, externalAccountId);
        params.put("sortDirection", ordenacao);
        params.put("sortField", "SEQUENCIAL_CARTAO");

        return doGetRequestWithListParams(params, "/v1/card");
    }

    public void validateSortDirection(List<Integer> listCardId, String sortDirection) {
        if ("DESC".equals(sortDirection)) {
            Assert.assertTrue(listCardId.get(0) > listCardId.get(1));
        } else if ("ASC".equals(sortDirection)) {
            Assert.assertTrue(listCardId.get(1) > listCardId.get(0));
        } else {
            throw new RuntimeException("Sort Direction invalido");
        }
    }

    public Response blockVirtualCard(String virtualCardId) {
        return doPatchRequestWithQueryParam(new HashMap(), CARDS_RESOURCE + virtualCardId + "/block");
    }

    public Response unblockVirtualCard(String virtualCardId) {
        return doPatchRequestWithQueryParam(new HashMap(), CARDS_RESOURCE + virtualCardId + "/unblock");
    }

    public Response getInformationAdjustmentById(String id) {
        return doGetRequestOneAttribute("/v2/account/adjustment/{adjustmentId}", id);
    }

    public Response findInformationAccount(String accountId) {
        return doGetRequestOneAttribute("/account/{idAccount}/balance", accountId);
    }

    public Response accountBlock(String accountDockId, String statusId) {
        Map params = new LinkedHashMap();
        params.put("idStatus", statusId);
        Response response = doPostRequestQuery(params, ACCOUNT_RESOURCE + accountDockId + BLOCK);

        return response;
    }

    public Response accountCancel(String accountDockId, String statusId) {
        Map<String, Object> params = new HashMap<>();
        params.put("idStatus", statusId);

        return doPostRequestQuery(params, ACCOUNT_RESOURCE + accountDockId + CANCEL);
    }

    public String payloadTransactionAntifraud(String accessChannel, String processingChannel,
                                                 String authenticationMethod, String accountBankId, String depositFlag,
                                                 String processingType, float value) {
        JSONObject creditData = new JSONObject();
        creditData.put(accountBankIdField, ispb);
        creditData.put(accountBranchField, branch);
        creditData.put(accountCountryField, country);
        creditData.put(accountNumberField, "139643684");
        creditData.put(accountSegmentLevelField, transaction);
        creditData.put(amountField, value);
        creditData.put(currencyField, brl);
        creditData.put(customerDocumentField, "56446485590");
        creditData.put(customerNameField, "Janaina L");
        creditData.put(dateField, dateTimeUtil.getDateFormattedIso8601());

        JSONObject debitData = new JSONObject();
        debitData.put(accountBankIdField, accountBankId);
        debitData.put(accountBranchField, branch);
        debitData.put(accountCountryField, country);
        debitData.put(accountNumberField, "139674796");
        debitData.put(accountSegmentLevelField, transaction);
        debitData.put(amountField, value);
        debitData.put(currencyField, brl);
        debitData.put(customerDocumentField, "73181566594");
        debitData.put(customerNameField, "Janaina Leal");
        debitData.put(dateField, dateTimeUtil.getDateFormattedIso8601());

        JSONObject payload = new JSONObject();
        payload.put("accessChannel", accessChannel);
        payload.put("authenticationMethod", authenticationMethod);
        payload.put("customerAccountNumber", "132631");
        payload.put(customerDocumentField, "56446485590");
        payload.put("depositWithdrawalFlag", depositFlag);
        payload.put("externalTransactionId", "1235487");
        payload.put("isInternational", "false");
        payload.put("onUsFlag", "ON_US");
        payload.put("paymentOrderFlag", "PAYMENT");
        payload.put("preDecision", "APPROVE");
        payload.put("processingChannel", processingChannel);
        payload.put("processingType", processingType);
        payload.put("recurringExpireDate", dateTimeUtil.getDateFormattedIso8601());
        payload.put("reversalIndicator", "NOT_A_REVERSAL");
        payload.put("serviceType", "PIX");
        payload.put("transactionAmount", value);
        payload.put("transactionCountry", country);
        payload.put("transactionCurrencyCode", brl);
        payload.put("transactionDate", "2022-01-31");
        payload.put("transactionState", "RS");
        payload.put("transactionTime", dateTimeUtil.getDateFormattedIso8601());
        payload.put("transactionType", "TRANSFER");
        payload.put("transactionUsdExchangeRate", "1");
        payload.put("creditData", creditData);
        payload.put("debitData", debitData);

        return payload.toString();
    }

    public Response transactionAntifraud(String accessChannel, String processingChannel, String authenticationMethod,
                         String accountBankId, String depositFlag, String processingType,
                         float value) {
        return doPostRequestJson(payloadTransactionAntifraud(accessChannel, processingChannel, authenticationMethod,
                accountBankId, depositFlag, processingType, value), ANTIFRAUD_ENDPOINT);
    }

    // Metodo foi criado porem nao vai ser utilizado nesse momento
    public Response getCards(String dockAccountid) {
        Map<String, String> params = new HashMap<>();
        params.put("externalAccountId", dockAccountid);
        params.put("sortDirection", "DESC");
        params.put("sortField", "SEQUENCIAL_CARTAO");

        return doGetRequestWithListParams(params, CARDS_RESOURCE);

    }

    public Response createDynamicCvv(String cardId, String expirationDate ) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("expirationDate", expirationDate);

        return doPostRequestQuery(queryParams, CARDS_RESOURCE + cardId + DYNAMIC_CVV_ENDPOINT);
    }

    public Response getCvvDynamic(String cardId) {
        return doGetRequestOneAttribute(DYNAMIC_CVV_ENDPOINT_URL, cardId);
    }

    public Response deleteCvvDynamic(String cardId) {
        return doDeleteRequest(DYNAMIC_CVV_ENDPOINT_URL, cardId);
    }

    public Response getListFinancialAdjustments(String externalIdentifier, String accountDockId) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("externalIdentifier", externalIdentifier);
        queryParams.put("date", LocalDate.now().atStartOfDay().toString());
        queryParams.put("idAccount", accountDockId);

        return doGetRequestQuery(queryParams, LIST_FINANCIAL_ADJUSTMENTS);
    }

    public Response waitForListFinancialAdjustments(String externalIdentifier, String accountDockId) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateListFinancialAdjustments(externalIdentifier, accountDockId));
        } catch (ExceptionInInitializerError e) {
            System.out.println("Erro: " + e);
        }

        return adjustmentsResp;
    }

    public boolean validateListFinancialAdjustments(String externalIdentifier, String accountDockId) {
        adjustmentsResp = getListFinancialAdjustments(externalIdentifier, accountDockId);

        return adjustmentsResp.statusCode() == HttpStatus.SC_OK;
    }
}