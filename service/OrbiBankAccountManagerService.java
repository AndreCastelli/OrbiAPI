package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PersonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OrbiBankAccountManagerService extends BaseService {

    private static final String PAGE = "page";
    private static final String LIMIT = "limit";
    private static final String STATUS_WISHLIST = "idStatusWishlist";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String ITEMS = "items";
    private static final String TRANSACTION_DATE = "transactionDate";
    private static final String SLASH = "/";
    private static final String ENDPOINT_VERSION = "/v1";
    private static final String BALANCE_ENDPOINT = "/balance";
    private static final String ACCOUNTS_ENDPOINT = "/accounts";
    private static final String ACCOUNT_ENDPOINT = "/account";
    private static final String TRANSACTIONS_ENDPOINT = "/transactions";
    private static final String ACCOUNTUUID_ENDPOINT = "/by/dock-id/";
    private static final String CANCEL_CHECK_ENDPOINT = "/cancel/check";
    private static final String REPORTS_STATEMENT_PDF_ENDPOINT = "/reports/statement/pdf";
    private static final String ACCOUNT_CREATION_JSON_FILE = "bankaccountmanager/CreateAccount.json";
    private static final String EMPTY_PAYLOAD = "{}";
    private static final String GENERIC_UUID = "c0f73c42-0961-489b-aa37-6cf1b2071d25";
    private static final String GENERIC_STARTDATE = "2020-10-01";
    private static final String GENERIC_ENDDATE = "2021-03-30";
    private static final int GENERIC_PAGE_LIMIT = 50;
    private static final String ID_PERSON_VALUE = "a29d2660-fe22-47ee-b90d-b903b522ae72";
    private static final int GENERIC_PAGE = 0;
    private static final String GENERIC_PAGE_STRING = "1";
    private static final int GENERIC_DUE_DATE = 10;
    private static final double GENERIC_INCOME = 250.25;
    private static final int PERSON_DOCK_ID = 125452;
    private static final int MAILING_ADDRESS_ID = 19255;
    private static final int POINTS = 1;
    private static final int COMMERCIAL_ORIGIN_ID = 1;
    private static final String ORBI_ACCOUNT_ID = "orbiAccountId";
    private static final String ORBI_PERSON_ID = "orbiPersonId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String BASE_URI = "authentication.account.baseURI";

    private Response accountResp;
    private String commercialOriginId = "commercialOriginId";
    private String mailingAddressId = "mailingAddressId";
    private String personId = "personId";
    private String products = "products";
    private String income = "income";
    private String personDockId = "personDockId";
    private String points = "points";
    private boolean match;

    public OrbiBankAccountManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));
    }

    public Response accountTransactionRequest(
            String idaccount,
            String startdate,
            String enddate,
            Integer limit,
            Integer page) {
        Map params = new LinkedHashMap<>();
        params.put("startDate", startdate);
        params.put("endDate", enddate);
        params.put(LIMIT, limit);
        params.put(PAGE, page);

        return doGetRequestWithListParams(params, ACCOUNTS_ENDPOINT + SLASH + idaccount + TRANSACTIONS_ENDPOINT);
    }

    public Map<String, Object> createBalancePagePayload(String page, String limit, List<Integer> idStatusWishlist) {
        String statusList = idStatusWishlist.toString();

        Map queryParams = new LinkedHashMap();
        queryParams.put(PAGE, page);
        queryParams.put(LIMIT, limit);
        queryParams.put(STATUS_WISHLIST, statusList.substring(1, statusList.length() - 1));

        return queryParams;
    }

    public Response createBalancePagePayloadSuccess(int page, int limit, List<Integer> idStatusWishlist)
            throws NoSuchFieldException, IllegalAccessException {
        Response response = doGetRequestQuery(createBalancePagePayload(Integer.toString(page), Integer.toString(limit),
                idStatusWishlist), ACCOUNTS_ENDPOINT + BALANCE_ENDPOINT);

        return response;
    }

    public Response createBalancePagePayloadStatusNull() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> statusNull = new ArrayList<>();

        return doGetRequestQuery(createBalancePagePayload(GENERIC_PAGE_STRING,
                Integer.toString(GENERIC_PAGE_LIMIT), statusNull),
                ACCOUNTS_ENDPOINT + BALANCE_ENDPOINT);
    }

    public Response createBalancePagePayloadPageNull() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> status = new ArrayList<>();
        status.add(0);

        String page = "";

        Response response = doGetRequestQuery(createBalancePagePayload(page,
                Integer.toString(GENERIC_PAGE_LIMIT), status),
                ACCOUNTS_ENDPOINT + BALANCE_ENDPOINT);

        return response;
    }

    public Response createTransactionsPayloadPageNull() throws NoSuchFieldException, IllegalAccessException {
        Response response = accountTransactionRequest(GENERIC_UUID, GENERIC_STARTDATE,
                GENERIC_ENDDATE, GENERIC_PAGE_LIMIT, -1);

        return response;
    }

    public Response createTransactionsPayloadLimitNull() throws NoSuchFieldException, IllegalAccessException {
        int limit = 0;

        Response response = accountTransactionRequest(GENERIC_UUID, GENERIC_STARTDATE,
                GENERIC_ENDDATE, limit, GENERIC_PAGE);

        return response;
    }

    public Response createBalancePagePayloadLimitNull() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> status = new ArrayList<>();
        status.add(0);

        String limit = "";

        return doGetRequestQuery(createBalancePagePayload(Integer.toString(1), limit, status),
                ACCOUNTS_ENDPOINT + BALANCE_ENDPOINT);
    }

    private Response createTransactionsPayloadUuidInvalid() {
        String uuid = "abc123";

        return accountTransactionRequest(uuid, GENERIC_STARTDATE, GENERIC_ENDDATE, GENERIC_PAGE_LIMIT, GENERIC_PAGE);
    }

    private Response createTransactionsPayloadUuidEmpty() {
        String uuid = "";

        return accountTransactionRequest(uuid, GENERIC_STARTDATE, GENERIC_ENDDATE, GENERIC_PAGE_LIMIT, GENERIC_PAGE);
    }

    public Response createBalancePagePayloadBalance(String id) {
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));
        return doGetRequestJson(EMPTY_PAYLOAD, ACCOUNTS_ENDPOINT + SLASH + id + BALANCE_ENDPOINT);
    }

    public int totalAccounts() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> status = new ArrayList<>();
        status.add(0);

        Response responseAccounts = createBalancePagePayloadSuccess(GENERIC_PAGE_LIMIT, 1, status);

        return responseAccounts.body().jsonPath().get(TOTAL_PAGES);
    }

    public Response listAccounts(int page, int limit, int status) {
        Map queryParams = new LinkedHashMap();
        queryParams.put(PAGE, page);
        queryParams.put(LIMIT, limit);
        queryParams.put("idStatus", status);

        Response response = doGetRequestQuery(queryParams, ACCOUNTS_ENDPOINT);

        return response;
    }

    public double accountBalance(Integer id) throws NoSuchFieldException, IllegalAccessException {
        Number balance = createBalancePagePayloadBalance(id.toString()).body().jsonPath().get("balance");

        if (balance == null) {
            return 0;
        } else {
            return balance.doubleValue();
        }
    }

    public Map<Integer, Double> mapAccountsBalance(int page, int limit, int status)
            throws NoSuchFieldException, IllegalAccessException {
        Response response = listAccounts(page, limit, status);

        List<Integer> listAccountID = response.getBody().jsonPath().getList("content.id");
        Map<Integer, Double> mapAccounts = new LinkedHashMap<>();

        for (Integer id : listAccountID) {
            mapAccounts.put(id, accountBalance(id));
        }

        return mapAccounts;
    }

    public int totalPagesExpected(double accounts, double limit) {
        return (int) Math.ceil(accounts / limit);
    }

    public double calculateBalanceList(Map<Integer, Double> balanceList) {
        double balance = 0;

        for (Integer key : balanceList.keySet()) {
            balance += balanceList.get(key);
        }

        return balance;
    }

    public Map<String, Object> createGenericCancelPayload() {
        Map<String, Object> payload = new HashMap<>();

        payload.put("reason", "[Automação] Razão de encerramento");
        payload.put("channel", "Mobile");
        payload.put("ip", "127.0.0.1");
        payload.put("deviceId", "1");
        payload.put("operatingSystem", "iOS");

        return payload;
    }

    public Map<String, Object> createCancelPayload(String reason, String channel,
                                                   String ip, String deviceId, String os) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("reason", reason);
        payload.put("channel", channel);
        payload.put("ip", ip);
        payload.put("deviceId", deviceId);
        payload.put("operatingSystem", os);

        return payload;
    }

    public Response accountCancel(String uuidAccount) {
        return doPostRequestMap(createGenericCancelPayload(), ACCOUNTS_ENDPOINT + SLASH + uuidAccount + "/cancel");
    }

    public Response accountCancel(String id, String reason, String channel, String ip, String deviceId, String os) {
        return doPostRequestMap(createCancelPayload(reason, channel, ip, deviceId, os),
                ACCOUNTS_ENDPOINT + SLASH + id + "/cancel");
    }

    public Response accountCancelCheck(String uuidAccount) {
        return doGetRequestJson(EMPTY_PAYLOAD, ACCOUNTS_ENDPOINT + SLASH + uuidAccount + CANCEL_CHECK_ENDPOINT);
    }

    public Boolean getMatchCriteriaCancelCheck(String criterio, JSONObject response) {
        JSONArray criteriaList = response.getJSONArray("criteria");
        for (int i = 0; i < criteriaList.length(); i++) {
            JSONObject criteria = criteriaList.getJSONObject(i);

            if (criteria.getString("criteria").contains(criterio)) {
                match = (boolean) criteria.get("match");
            }
        }

        return match;
    }

    public Response accountStatusCheck(String uuidAccount) {
        return doGetRequestOneAttribute(ACCOUNTS_ENDPOINT + SLASH + "{uuidAccount}", uuidAccount);
    }

    public Response sendPostRequestCreateAccount(Response person) {
        return doPostRequestMap(accountCreationPayloadWithPerson(person), ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + SLASH);
    }

    public String accountCreationPayload(PersonUtil person) throws IOException {
        String payloadString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + ACCOUNT_CREATION_JSON_FILE);

        payloadString = updatePayload("name", person.getName(), payloadString);
        payloadString = updatePayload("motherName", person.getMother(), payloadString);
        payloadString = updatePayload("birthDate", person.getBirthDate(), payloadString);
        payloadString = updatePayload("document", person.getCpf(), payloadString);
        payloadString = updatePayload("email", person.getEmail(), payloadString);

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

    public String payloadOnlyWithMandatoryData(String payload) throws IOException {
        String payloadString = payload;

        payloadString = updatePayload("address.complement", "", payloadString);
        payloadString = updatePayload("address.reference", "", payloadString);
        payloadString = updatePayload("incomeValue", "", payloadString);

        return payloadString;
    }

    public static Boolean listIdAdjustmentType() {
        JSONObject adjustemtType = new JSONObject(RequestManager.shared().getResponse().getBody().asString());
        JSONArray userArray = adjustemtType.getJSONArray(ITEMS);

        boolean typeNotNull = false;

        for (int j = 0; j < userArray.length(); j++) {
            JSONObject adjustmentTypeObj = userArray.getJSONObject(j);
            int idAdjustmentType = adjustmentTypeObj.getInt("idAdjustmentType");

            if (idAdjustmentType > 0) {
                typeNotNull = true;
            } else {
                typeNotNull = false;
                break;
            }
        }

        return typeNotNull;
    }

    public Response waitForTheReturnOfAccountInformation(String uuidAccount, String status) {
        Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() -> checkIfTheAccountWasCreated(
                uuidAccount, status));

        return accountIdByPersonId(uuidAccount, status);
    }

    public boolean checkIfTheAccountWasCreated(String uuidAccount, String status) {
        return accountIdByPersonId(uuidAccount, status).statusCode() == HttpStatus.SC_OK;
    }

    public Response accountIdByPersonId(String uuidAccount, String status) {
        return doGetRequestJson(EMPTY_PAYLOAD,
                ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + SLASH + "by/person/" + uuidAccount + "?status=" + status);
    }

    public Response createPayloadSearchAccountUUID(String dockId) {
        return doGetRequestJson(EMPTY_PAYLOAD,
                ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + ACCOUNTUUID_ENDPOINT + dockId);
    }

    public Map<String, Object> accountCreationPayloadWithPerson(Response person) {
        JSONArray productsArray = new JSONArray();
        Map<String, Object> payload = new HashMap<>();

        payload.put("commercialOriginId", 1);
        payload.put("personId", person.then().extract().path("person.id").toString());
        payload.put("mailingAddressId", person.then().extract().path("person.address.id").toString());
        payload.put("products", productsArray);
        payload.put("income", GENERIC_INCOME);
        payload.put("personDockId", person.then().extract().path("person.dockId").toString());
        payload.put("points", 1);

        return payload;
    }

    public Map<String, Object> accountCreationPayloadWithPersonReplace(String person) {
        JSONArray productsArray = new JSONArray();
        Map<String, Object> payload = new HashMap<>();
        payload.put("commercialOriginId", COMMERCIAL_ORIGIN_ID);
        payload.put("personId", person);
        payload.put("mailingAddressId", MAILING_ADDRESS_ID);
        payload.put("products", productsArray);
        payload.put("income", GENERIC_INCOME);
        payload.put("personDockId", PERSON_DOCK_ID);
        payload.put("points", POINTS);

        return payload;
    }

    public Response sendPostRequestCreateAccountSamePerson(Map queryParams) {
        return doPostRequestMap(queryParams, ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + SLASH);
    }

    public Response createAccountWithPersonParam(String person) {
        JSONArray productsArray = new JSONArray();
        Map<String, Object> payload = new HashMap<>();
        payload.put("commercialOriginId", COMMERCIAL_ORIGIN_ID);
        payload.put("personId", person);
        payload.put("mailingAddressId", MAILING_ADDRESS_ID);
        payload.put("products", productsArray);
        payload.put("income", GENERIC_INCOME);
        payload.put("personDockId", PERSON_DOCK_ID);
        payload.put("points", POINTS);

        return doPostRequestMap(payload, ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + SLASH);
    }

    public Response createAccountWithoutMandatoryField(String mandatoryField) {
        Map payloadWithAllFields = accountCreationPayloadWithPersonReplace(ID_PERSON_VALUE);

        return doPostRequestMap(
                removeFieldFromCreateAccountPayload(mandatoryField,
                        payloadWithAllFields), ENDPOINT_VERSION + ACCOUNTS_ENDPOINT + SLASH);
    }

    public Map<String, Integer> removeFieldFromCreateAccountPayload(
            String mandatoryField, Map<String, Integer> payload) {
        if (commercialOriginId.equals(mandatoryField)) {
            payload.remove(commercialOriginId);
        }

        if (mailingAddressId.equals(mandatoryField)) {
            payload.remove(mailingAddressId);
        }

        if (personId.equals(mandatoryField)) {
            payload.remove(personId);
        }

        if (products.equals(mandatoryField)) {
            payload.remove(products);
        }

        if (income.equals(mandatoryField)) {
            payload.remove(income);
        }

        if (personDockId.equals(mandatoryField)) {
            payload.remove(personDockId);
        }

        if (points.equals(mandatoryField)) {
            payload.remove(points);
        }

        return payload;
    }

    public Response waitForAccountCreation(Response person) {
        Response personResp = person;
        Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() -> validateDigitalAccountCreation(personResp));

        return accountResp;
    }

    public boolean validateDigitalAccountCreation(Response person) {
        accountResp = sendPostRequestCreateAccount(person);

        return accountResp.statusCode() == HttpStatus.SC_CREATED;
    }

    public Response createPayloadNegativeScenarios(String field) throws Exception {
        switch (field) {
            case "balance_idStatusWishlist":
                return createBalancePagePayloadStatusNull();

            case "balance_page":
                return createBalancePagePayloadPageNull();

            case "balance_limit":
                return createBalancePagePayloadLimitNull();

            case "transactions_page":
                return createTransactionsPayloadPageNull();

            case "transactions_limit":
                return createTransactionsPayloadLimitNull();

            case "transactions_uuid_invalid":
                return createTransactionsPayloadUuidInvalid();

            case "transactions_uuid_empty":
                return createTransactionsPayloadUuidEmpty();

            default:
                throw new Exception("Parâmetro informado inexistente");
        }
    }

    public void compareDateLimits(String startDate, String endDate, String uuid) throws Exception {
        JSONObject serviceResponse = new JSONObject(RequestManager.shared().getResponse().getBody().asString());
        JSONArray transactions = serviceResponse.getJSONArray(ITEMS);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        Date lastTransaction = dateFormat.parse(transactions.getJSONObject(0).getString(TRANSACTION_DATE));

        for (int j = 0; j < serviceResponse.getInt(TOTAL_PAGES); j++) {
            JSONObject response = new JSONObject(accountTransactionRequest(uuid, startDate,
                    endDate, GENERIC_PAGE_LIMIT, j).getBody().asString());
            transactions = response.getJSONArray(ITEMS);

            for (int i = 0; i < transactions.length(); i++) {
                JSONObject item = transactions.getJSONObject(i);
                String transactionDate = item.getString(TRANSACTION_DATE);

                Date convertedStartDate = dateFormat.parse(startDate);
                Date convertedEndDate = dateFormat.parse(endDate);
                Date convertedTransactionDate = dateFormat.parse(transactionDate);

                if (!convertedTransactionDate.after(convertedStartDate)
                        && !convertedTransactionDate.before(convertedEndDate)) {
                    throw new Exception("Possui um item com data fora do período informado");
                }

                if (convertedTransactionDate.after(lastTransaction)) {
                    throw new Exception("Paginação de transactions fora de ordem temporal");
                }

                lastTransaction = convertedTransactionDate;
            }
        }
    }

    public Response accountPermissionsCheck(String uuidAccount) {
        return doGetRequestOneAttribute(
                ENDPOINT_VERSION + ACCOUNT_ENDPOINT + SLASH + "{uuidAccount}", uuidAccount + "/permissions");
    }

    public Response setBlockingMonitoring(String uuidAccount, String blockingMonitoring) {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("blockingMonitoring", blockingMonitoring);

        return doPostRequestQuery(queryParams,
                ENDPOINT_VERSION + ACCOUNT_ENDPOINT + SLASH + uuidAccount + "/blocking-monitoring");
    }

    public Response generateReportStatementPdf(String orbiAccountId, String orbiPersonId, String date) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put(ORBI_ACCOUNT_ID, orbiAccountId);
        queryParams.put(ORBI_PERSON_ID, orbiPersonId);
        queryParams.put(START_DATE, date);
        queryParams.put(END_DATE, date);

        return doGetRequestQuery(queryParams,
                ENDPOINT_VERSION + ACCOUNT_ENDPOINT + REPORTS_STATEMENT_PDF_ENDPOINT);
    }

    public Response getTransactionsWithAccountId(String accountId) {
        return doGetRequestOneAttribute("/accounts/{accountId}/transactions", accountId);
    }
}
