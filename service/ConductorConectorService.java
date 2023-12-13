package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConductorConectorService extends BaseService {

    private static final String INVOICE_CLOSED_RESOURCE = "/v1/invoice/closed?externalAccountId={externalAccountId}";
    private static final String INVOICE_OPENED_RESOURCE = "/v1/invoice/opened?externalAccountId={externalAccountId}";
    private static final String RECENT_TRANSACTIONS_RESOURCE = "/v1/transaction/recent?"
            + "externalAccountId={externalAccountId}";
    private static final String INVOICE_RESOURCE = "/v1/invoice";
    private static final String CARDS_RESOURCE = "/v1/card/";
    private static final String QUERY_PERSON_DETAILS = "/v1/persons?cpf={cpf}";
    private static final String QUERYPARAM_PAGE = "page";
    private static final String QUERYPARAM_LIMIT = "limit";

    DateTimeUtil dateTimeUtil;

    public ConductorConectorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        dateTimeUtil = new DateTimeUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("conductor.connector.baseURI"));
    }

    public Response getInvoice(String externalAccountId, String limit,
                               String page, String processingSituation, String sort) {
        Map<String, String> params = new HashMap<>();
        params.put("externalAccountId", externalAccountId);
        params.put("limit", limit);
        params.put("page", page);
        params.put("situacaoProcessamento", processingSituation);
        params.put("sortDirection", sort);
        params.put("sortField", "DATA_VENCIMENTO_FATURA");

        return doGetRequestWithListParams(params, INVOICE_RESOURCE);
    }

    public Response createConductorVirtualCard(String flagBlocked, String externalAccountId) {
        return doPostRequestMap(createJsonPayload(flagBlocked, externalAccountId), "v1/card/virtual");
    }

    public Response getCard(String externalAccountId, String sortDirection) {
        Map<String, String> params = new HashMap<>();
        params.put("externalAccountId", externalAccountId);
        params.put("sortDirection", sortDirection);
        params.put("sortField", "SEQUENCIAL_CARTAO");

        return doGetRequestWithListParams(params, "/v1/card");
    }

    public Map<String, String> createJsonPayload(String flagBlocked, String externalAccountId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("blocked", flagBlocked);
        payload.put("externalAccountId", externalAccountId);
        payload.put("validationDate", dateTimeUtil.getDateFormattedToValidationDateField());

        return payload;
    }

    public Response getPersonDetailsByCPF(String cpf) {
        return doGetRequestOneAttribute(QUERY_PERSON_DETAILS, cpf);
    }

    public Response getPersonDetailsByPassingTheWrongURL(String cpf) {
        return doGetRequestOneAttribute("v/persons?cpf={cpf}", cpf);
    }

    public Response getAccountLimit(String externalAccountId) {
        return doGetRequestOneAttribute("/v1/account/limit?externalAccountId={externalAccountId}", externalAccountId);
    }

    public Response getInvoiceClosedByAccountId(String externalAccountId) {
        return doGetRequestOneAttribute(INVOICE_CLOSED_RESOURCE, externalAccountId);
    }

    public Response getInvoiceOpenedByAccountId(String externalAccountId) {
        return doGetRequestOneAttribute(INVOICE_OPENED_RESOURCE, externalAccountId);
    }

    public Response getFutureIvoiceByAccountId(String externalAccountId, String limit, String page, String sort) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", limit);
        params.put("page", page);
        params.put("sortDirection", sort);

        return doGetRequestWithListParams(params, "v1/invoice/future?externalAccountId=" + externalAccountId);
    }

    public Response getInvoiceClosedByPage(String externalAccountId, int pageNumber) {
        return doGetRequestWithQueryParam(INVOICE_CLOSED_RESOURCE, externalAccountId,
                QUERYPARAM_PAGE, pageNumber);
    }

    public Response getInvoiceClosedUsingQueryParam(String externalAccountId, int limit) {
        return doGetRequestWithQueryParam(INVOICE_CLOSED_RESOURCE, externalAccountId,
                QUERYPARAM_LIMIT, limit);
    }

    public String getJsonProperty(String property) {
        return RequestManager.shared().getResponse().then().extract().path(property).toString();
    }

    public int getSizePropertyByName(String propertyName) {
        return RequestManager.shared().getResponse().then().extract().jsonPath().getList(propertyName).size();
    }

    public Response getRecentTransactions(String externalAccountId) {
        return doGetRequestOneAttribute(RECENT_TRANSACTIONS_RESOURCE, externalAccountId);
    }

    public Response getRecentTransactionsByLimit(String externalAccountId, int limit) {
        return doGetRequestWithQueryParam(RECENT_TRANSACTIONS_RESOURCE, externalAccountId,
                QUERYPARAM_LIMIT, limit);
    }

    public Response getRecentTransactionsByPage(String externalAccountId, int page) {
        return doGetRequestWithQueryParam(RECENT_TRANSACTIONS_RESOURCE, externalAccountId,
                QUERYPARAM_PAGE, page);
    }

    public Response getAccountByPersonId(String personId, String sortDirection) {
        Map<String, String> params = new HashMap<>();
        params.put("personId", personId);
        params.put("sortDirection", sortDirection);
        params.put("sortField", "DATA_CADASTRO");

        return doGetRequestWithListParams(params, "/v1/account");
    }

    public void validateSortDirection(List<Integer> listIds, String sortDirection) {
        if (!(listIds.size() <= 1)) {
            if ("DESC".equals(sortDirection)) {
                Assert.assertTrue(listIds.get(0) > listIds.get(1));
            }

            if ("ASC".equals(sortDirection)) {
                Assert.assertTrue(listIds.get(1) > listIds.get(0));
            } else {
                throw new RuntimeException("Sort Direction invalido");
            }
        } else {
            throw new RuntimeException("Lista so tem um registro");
        }
    }

    public Response getAccountDetails(String externalAccountId) {
        return doGetRequestOneAttribute("/v1/account/{orbiAccountId}", externalAccountId);
    }

    public Response getInvoiceInstallmentePlans(String externalAccountId, String dueDate) {
        return doGetWithoutParameters(INVOICE_RESOURCE + "/" +
                dueDate + "/campaign-installment-plans?externalAccountId=" + externalAccountId);
    }

    public Response getInvoicePDF(String dueDate, String externalAccountId) {
        Map<String, String> params = new HashMap<>();
        params.put("dueDate", dueDate);
        params.put("externalAccountId", externalAccountId);

        return doGetRequestWithPathParams(params, "v1/invoice/{dueDate}/pdf?externalAccountId={externalAccountId}");
    }
}
