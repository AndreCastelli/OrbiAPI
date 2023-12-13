package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrbiCardsManagerService extends BaseService {

    private static final String FIELD_CARD_TYPE = "cardType";
    private static final String FIELD_ORBI_ACCOUNT_ID = "orbiAccountId";
    private static final String CARDS_RESOURCE = "/v1/card/";
    private static final String INVOICE_HISTORY_RESOURCE = "/v1/invoice/history";
    private static final String INVOICE_RESOURCE = "/v1/invoice";
    private static final String BASE_URI = "orbi.cards.manager.baseURI";
    private static final String ORBI_ACCOUNT_ID = "orbi-Account-Id";
    DateTimeUtil dateTimeUtil;

    String orbiAccountIdPayloadField = "orbiAccountId";
    String isBlockedPayloadField = "blocked";
    String cardTypeField = "cardType";
    String blockedTrueFlag = "true";
    String blockedFalseFlag = "false";

    private AuthenticationService authenticationService;

    public OrbiCardsManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        dateTimeUtil = new DateTimeUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));
    }

    private void setHeaderUpdateBaseURI(Map<String, ?> header) {
        RequestManager.shared().setHeaders(header);
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));
    }

    public Response createCardWithoutMandatoryFields(String campoObrigatorio, String orbiAccountUID, String cardType) {
        Map payloadWithAllMandatoriesFields = createCardPayload(orbiAccountUID, String.valueOf(true), cardType);

        return doPostRequestMap(removeFieldFromPayload(campoObrigatorio, payloadWithAllMandatoriesFields),
                CARDS_RESOURCE);
    }

    public Map<String, String> removeFieldFromPayload(String mandatoryField, Map<String, String> payload) {
        if (orbiAccountIdPayloadField.equals(mandatoryField)) {
            payload.remove(orbiAccountIdPayloadField);
        }

        if (isBlockedPayloadField.equals(mandatoryField)) {
            payload.remove(isBlockedPayloadField);
        }

        if (cardTypeField.equals(mandatoryField)) {
            payload.remove(cardTypeField);
        }

        return payload;
    }

    /*
    / Metodo que converte a string 'desbloqueado/bloqueado' passada na featura para 'true/false'
    / Para usar no payload da criacao do cartao virtual que recebe: true/false
     */
    public String returnFlagIsBlocked(String isBlocked) throws Exception {
        if ("bloqueado".equals(isBlocked)) {
            return blockedTrueFlag;
        } else if ("desbloqueado".equals(isBlocked)) {
            return blockedFalseFlag;
        } else {
            throw new Exception("Status para o cartão invalido");
        }
    }

    public Map<String, String> createCardPayload(String orbiAccountId, String isBlocked, String cardType) {
        Map<String, String> payload = new HashMap<>();

        payload.put(orbiAccountIdPayloadField, orbiAccountId);
        payload.put("blocked", isBlocked);
        payload.put("cardType", cardType);

        return payload;
    }

    public Response createCard(String orbiAccountId, String isBlocked, String cardType) {
        return doPostRequestMap(
                createCardPayload(orbiAccountId, isBlocked, cardType), CARDS_RESOURCE);
    }

    public Response getlastClosedInvoice(String conta) {
        Map<String, String> params = new HashMap<>();
        params.put(FIELD_ORBI_ACCOUNT_ID, conta);

        return doGetRequestWithListParams(params, "/v1/invoice/closed/last");
    }

    public Response getCardLastValid(String conta, String cardType) {
        Map<String, String> params = new HashMap<>();
        params.put(FIELD_CARD_TYPE, cardType);
        params.put(FIELD_ORBI_ACCOUNT_ID, conta);

        return doGetRequestWithListParams(params, "/v1/card/last-valid");
    }

    public Response updateCardStatus(String orbiAccountId, String cardId, String cardStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("cardStatus", cardStatus);
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doPatchRequestWithQueryParam(params, CARDS_RESOURCE + cardId + "/update-status");
    }

    public Response getBestShoppingDay(String orbiAccountId) {
        return doGetRequestOneAttribute("/v1/account/{orbiAccountId}/best-shopping-day", orbiAccountId);
    }

    public Response getAccountLimit(String orbiAccountId) {
        return doGetRequestOneAttribute("/v1/account/{orbiAccountId}/limit", orbiAccountId);
    }

    public Response getInvoice(String orbiAccountId, String processingSituation,
                               String limit, String page, String sortDirection) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", limit);
        params.put("orbiAccountId", orbiAccountId);
        params.put("page", page);
        params.put("processingSituation", processingSituation);
        params.put("sortDirection", sortDirection);
        params.put("sortField", "DATA_VENCIMENTO_FATURA");

        return doGetRequestWithListParams(params, INVOICE_RESOURCE);
    }

    public Response getFutureInvoice(String orbiAccountId, String limit, String page, String sort) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", limit);
        params.put("page", page);
        params.put("sortDirection", sort);

        return doGetRequestWithListParams(params, "v1/invoice/future?orbiAccountId=" + orbiAccountId);
    }

    public Response getFutureInvoiceWithoutFilters(String orbiAccountId) {
        return doGetWithoutParameters("v1/invoice/future?orbiAccountId=" + orbiAccountId);
    }

    public void validateSortDirection(List<String> listInvoiceDueDates, String sortDirection) {
        if (!(listInvoiceDueDates.size() <= 1)) {
            LocalDate invoiceDueDateLocalDate = dateTimeUtil.getLocalDateByStringDate(
                    listInvoiceDueDates.get(0).replace("-", ""));
            LocalDate today = dateTimeUtil.getLocalDateByStringDate(
                    listInvoiceDueDates.get(1).replace("-", ""));

            if ("DESC".equals(sortDirection)) {
                Assert.assertTrue(invoiceDueDateLocalDate.isAfter(today));
            }

            if ("ASC".equals(sortDirection)) {
                Assert.assertTrue(invoiceDueDateLocalDate.isBefore(today));
            }
        }
    }

    public Response updateDynamicCvv(String cardId, String orbiAccountId) {
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doPatchRequestWithQueryParam(new HashMap(), CARDS_RESOURCE + cardId + "/dynamic-cvv");
    }

    public Response getDynamicCvv(String cardId, String orbiAccountId) {
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doGetRequestOneAttribute("v1/card/{cardId}/dynamic-cvv", cardId);
    }

    public Response getRealData(String cardId, String orbiAccountId) {
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(BASE_URI));

        return doGetRequestOneAttribute("/v1/card/{cardId}/real-data", cardId);
    }

    public Response getPaymentesType(String invoiceDueDate, String orbiAccountId) {
        Map<String, String> params = new HashMap<>();
        params.put("invoiceDueDate", invoiceDueDate);
        params.put("orbiAccountId", orbiAccountId);

        return doGetRequestWithPathParams(params, "/v1/invoice/{invoiceDueDate}/payments?orbiAccountId={orbiAccountId}");
    }

    public Response getInvoiceHistory(String orbiAccountId) {
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));

        return doGetWithoutParameters(INVOICE_HISTORY_RESOURCE);
    }

    public Response getInvoiceHistoryWithFilters(String orbiAccountId, String fromDate, String toDate) {
        setHeaderUpdateBaseURI(Map.of(ORBI_ACCOUNT_ID, orbiAccountId));
        Map<String, String> params = new HashMap<>();
        params.put("from", fromDate);
        params.put("to", toDate);

        return doGetRequestWithPathParams(params, INVOICE_HISTORY_RESOURCE + "?from={from}&to={to}");
    }

    public Response getInvoicePDF(String dueDate, String orbiAccountId) {
        Map<String, String> params = new HashMap<>();
        params.put("dueDate", dueDate);
        params.put("orbiAccountId", orbiAccountId);

        return doGetRequestWithPathParams(params, "/v1/invoice/{dueDate}/pdf?orbiAccountId={orbiAccountId}");
    }

}
