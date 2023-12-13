package service;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;

public class OrbiReceiptManagerService extends BaseService {

    private static final String RECEIPT_PIX_ENDPOINT = "/v1/receipt/pix/adjustment/";
    private static final String RECEIPT_ADJUSTMENTID_ENDPOINT = "/v1/receipt/by/adjustment-id/";
    private static final String RECEIPT_ADJUSTMENTID_PDF_ENDPOINT = "/v1/receipt/by/adjustment-id/{adjustment-id}/pdf";

    JsonUtil jsonUtil;
    DateTimeUtil dateTimeUtil;

    public OrbiReceiptManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.receipt.manager.baseURI"));
        jsonUtil = new JsonUtil();
        dateTimeUtil = new DateTimeUtil();
    }

    public Response getReceiptPixByAdjustmentId(String adjustmentId) {
        return doGetWithoutParameters(RECEIPT_PIX_ENDPOINT + adjustmentId);
    }

    public Response createReceiptAdjustmentIdPayload(String adjustmentId) {
        return doGetWithoutParameters(RECEIPT_ADJUSTMENTID_ENDPOINT + adjustmentId);
    }

    public Response getTransactionReceiptByAdjustmentIdPdf(String adjustmentId) {
        return doGetRequestOneAttribute(RECEIPT_ADJUSTMENTID_PDF_ENDPOINT , adjustmentId);
    }
}
