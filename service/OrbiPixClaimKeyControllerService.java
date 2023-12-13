//package service;
//
//import base.RequestManager;
//import base.util.DateTimeUtil;
//import base.util.JsonUtil;
//import base.util.MathUtil;
//import base.util.PropertiesUtil;
//import io.restassured.response.Response;
//import org.apache.http.HttpStatus;
//import org.awaitility.Awaitility;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import static java.util.concurrent.TimeUnit.MINUTES;
//
//public class OrbiPixClaimKeyControllerService extends BaseService {
//
//    private static final String VERSION_V1 = "/v1/";
//    private static final String CLAIMS_ENDPOINT = "/claims";
//    private static final String ISPB_NUMBER = "27351731";
//    private static final String BRANCH_NUMBER = "0001";
//    private static final String NATURAL_PERSON = "NATURAL_PERSON";
//    private static final String SYMBOL_SLASH = "/";
//    private static final String COMPLETE_CLAIM_ENDPOINT = "/complete";
//    private static final String KEYVALUE = "keyValue";
//    private static final String KEYTYPE = "keyType";
//    private static final String REQUESTDATETIME = "requestDateTime";
//
//    DateTimeUtil dateTimeUtil;
//    JsonUtil jsonUtil;
//
//
//    public OrbiPixClaimKeyControllerService() throws IOException {
//        propertiesUtil = new PropertiesUtil();
//        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.entries.manager.baseURI"));
//        jsonUtil = new JsonUtil();
//        dateTimeUtil = new DateTimeUtil();
//    }
//
//    public String getDateTime() {
//        return dateTimeUtil.getDateFormattedIso8601();
//    }
//
//
//    public Response claimPixKey(String keyType, String keyValue, String accountId) {
//        JSONObject requestBody = new JSONObject();
//
//        requestBody.put("keyType", keyType);
//        requestBody.put("keyValue", keyValue);
//
//        return doPostRequestJson(requestBody.toString(), VERSION_V1 + accountId + CLAIMS_ENDPOINT);
//    }
//
//    public Response completeClaimPixKey(String accountId, String requestId, boolean responseConfirm) {
//        String date = getDateTime();
//        JSONObject requestBody = new JSONObject();
//
//        requestBody.put("dateTimeResponse", date);
//        requestBody.put("response", responseConfirm);
//
//        return doPostRequestJson(requestBody.toString(), VERSION_V1 + accountId
//                + CLAIMS_ENDPOINT + SYMBOL_SLASH + requestId + COMPLETE_CLAIM_ENDPOINT);
//    }
//
//    public Response waitForResponseServiceStatusKeysClaimed(String accountId) {
//        Awaitility.await().atMost(MathUtil.THREE, MINUTES).until(() -> checkResponseServiceStatusKeysClaimed(
//                accountId));
//
//        return getKeysClaimed(accountId);
//    }
//
//    public boolean checkResponseServiceStatusKeysClaimed(String accountId) {
//        return getKeysClaimed(accountId).statusCode() == HttpStatus.SC_OK;
//    }
//
//    public Response getKeysClaimed(String accountId) {
//
//        return doGetWithoutParameters(VERSION_V1 + accountId + CLAIMS_ENDPOINT);
//    }
//}
