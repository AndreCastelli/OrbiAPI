package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiBffMobileCardService extends BaseService {

    private static final String EXPIRED_AUTH_MANAGER_TOKEN = "ehJ4NXQiOiJNell4TW1Ga09HWXdNV0kwWldOb"
            + "U5EY3hOR1l3WW1NNFpUQTNNV0kyTkRBelpHUXpOR00wWkdSbE5qSmtPREZrWkRSaU9URmtNV"
            + "0ZoTXpVMlpHVmxOZyIsImtpZCI6Ik16WXhNbUZrT0dZd01XSTBaV05tTkRje"
            + "E5HWXdZbU00WlRBM01XSTJOREF6WkdRek5HTTBaR1JsTmpKa09ERmtaRFJpT"
            + "1RGa01XRmhNelUyWkdWbE5nX1JTMjU2IiwiYWxnIjoiUlMyNTYifQ.eyJzdWI"
            + "iOiJhdXRvLnRlc3RlMSIsImF1dCI6IkFQUExJQ0FUSU9OX1VTRVIiLCJhdWQiOi"
            + "JJSDg1cnZDRnlsOEduYVFYb3lNaUU4Z21FelFhIiwibmJmIjoxNjE3OTA1MjE3L"
            + "CJleHRlbmRlZF9leHRlcm5hbF9pZCI6IjRlZWY5NTY4LTM1NjgtNDAxZS1iODEyL"
            + "WUwMmM2MDNhMGM5NjoxYTZkZjg1NS1lZjg2LTQ5YWEtYWY2ZS1jODU3NDMxMTJkN"
            + "jMiLCJhenAiOiJJSDg1cnZDRnlsOEduYVFYb3lNaUU4Z21FelFhIiwic2NvcGUiOi"
            + "Jwcm9maWxlIiwiaXNzIjoiaHR0cHM6XC9cL2lkZW50aXR5LWhtbC5yZWFsaXplY2Z"
            + "pLmlvOjk0NDNcL29hdXRoMlwvdG9rZW4iLCJleHAiOjE2MTc5MDg4MTcsImlhdCI6M"
            + "TYxNzkwNTIxNywianRpIjoiYjQ4MWRlZmUtMjE5ZS00MjEzLThiNjMtNjhlMjZmYmRm"
            + "NjI1In0.fnU_K0nxzNjtE2RuvIHvhz_8uPE50zkloDKzyy0_GzVmVVqnfz67slsFZZ4F"
            + "QkioRyI1J0RTIK5Svlc1SIBfJbOh_vLLy5S4BzObHKWaGveRz22dApnkMDMfaDtxXkzL2"
            + "z-lrbdSBLrYOlirzsACTA5qotmESO6rJqQ8fSUSk0aJeoaE2a5CGZDpU_FfHnJraY5dFV"
            + "Flylx9kLv6QNksB_O2X6Lj0-rTig2Pc4hJlbVfHi6G7Knt2u1lzXwh6ZxImbGDmG_4pmPF"
            + "fcGhkRI0LJk9VqP_Izm6y13-Rotb5pD_fNkv4lIE1pn3PjJGItRiecQ52npkajWvXzNCMemykw";

    private static final String BFF_LIST_CARDS_RESOURCE = "/v1/card/cards";
    private static final String BFF_CARD_RESOURCE = "/v1/card/";
    private static final String ID = "id";
    private static final String ORBI_BFF_MOBILE_BASEURI = "orbi.bff.mobile.baseURI";
    private static final String DUEDATE = "dueDate";

    public OrbiBffMobileCardService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(ORBI_BFF_MOBILE_BASEURI));
    }

    public Response getCardsHome() {
        return doGetWithoutParameters("/v1/card/home");
    }

    public Response getBilletById(String id) {
        Map<String, String> params = new HashMap<>();
        params.put(ID, id);

        return doGetRequestWithPathParams(params, "/v1/card/billet/{id}");
    }

    public Response getInvoiceByDueDate(String dueDate) {
        Map<String, String> params = new HashMap<>();
        params.put(DUEDATE, dueDate);

        return doGetRequestWithPathParams(params, "/v1/card/invoice/{dueDate}");
    }

    public Response getInvoiceSummaryAndPaymentTypes(String dueDate) {
        Map<String, String> params = new HashMap<>();
        params.put(DUEDATE, dueDate);

        return doGetRequestWithPathParams(params, "v1/card/invoice/{dueDate}/payments");
    }

    public Response getInvoiceSummaryByBilletId(String invoideDueDate, String billetId) {
        Map<String, String> params = new HashMap<>();
        params.put("invoideDueDate", invoideDueDate);
        params.put("billetId", billetId);

        return doGetRequestWithPathParams(params, "v1/card/invoice/{invoideDueDate}/payments/billet/{billetId}");
    }

    public Response getBilletPdfById(String id) {
        Map<String, String> params = new HashMap<>();
        params.put(ID, id);

        return doGetRequestWithPathParams(params, "/v1/card/billet/{id}/pdf");
    }

    public void loginWithExpiredToken() {
        RequestManager.shared().setRequest(
                new RequestSpecBuilder().addHeader("Authorization",
                        "Bearer" + EXPIRED_AUTH_MANAGER_TOKEN).build());
    }

    public Response getCardsFromBff() {
        return doGetWithoutParameters(BFF_LIST_CARDS_RESOURCE);
    }

    public Response getCardId() {
        return doGetWithoutParameters(BFF_LIST_CARDS_RESOURCE);
    }

    public Response getCardRealData(String cardUUID) {
        Map<String, String> params = new HashMap<>();
        params.put(ID, cardUUID);

        return doGetRequestWithPathParams(params, "/v1/card/{id}/real-data");
    }

    public Response updateCardStatus(String cardId, String cardStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("cardStatus", cardStatus);

        return doPatchRequestWithQueryParam(params, BFF_CARD_RESOURCE + cardId + "/update-status");
    }

    public Response getFutureInvoice() {
        return doGetWithoutParameters("v1/card/invoice/future");
    }

    public Response createCard(String cardType, String isBlocked) {
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName(ORBI_BFF_MOBILE_BASEURI));
        return doPostRequestNoPayload(BFF_LIST_CARDS_RESOURCE + "?cardType=" + cardType + "&isBlocked=" + isBlocked);
    }

    public Response updateDynamicCvv(String cardId) {
        return doPatchRequestWithQueryParam(new HashMap(), BFF_CARD_RESOURCE + cardId + "/dynamic-cvv");
    }

    public Response getIinstallmentPlans(String invoiceDueDate) {
        return doGetRequestOneAttribute("v1/card/invoice/{invoiceDueDate}/campaign-installment-plans", invoiceDueDate);
    }

    public Response getHistoryInvoice() {
        return doGetWithoutParameters("/v1/card/invoice/history");
    }

    public Response getHistoryInvoiceWithFilters(String fromDate, String toDate) {
        Map<String, String> params = new HashMap<>();
        params.put("from", fromDate);
        params.put("to", toDate);

        return doGetRequestWithPathParams(params , "v1/card/invoice/history?from={from}&to={to}");
    }

    public Response getAccountLimit() {
        return doGetWithoutParameters("v1/card/account/limit");
    }

    public Response getInvoicePDF(String dueDate) {
        Map<String, String> params = new HashMap<>();
        params.put(DUEDATE, dueDate);

        return doGetRequestWithPathParams(params, "/v1/card/invoice/{dueDate}/pdf");
    }

    public Response getVirtualCreditCard() {
        return doGetWithoutParameters(BFF_LIST_CARDS_RESOURCE + "?cardType=VIRTUAL_CREDIT");
    }
}
