package service;

import base.RequestManager;
import base.db.OrbiCcsWorkerDb;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class OrbiK8sManagerService extends BaseService {
    private static final String K8S_CREATE_ENDPOINT = "/v1/cronjobs/forceCreateJob";
    private static final String K8S_STATUS_ENDPOINT = "/v1/cronjobs/status";
    DateTimeUtil dateTimeUtil;
    OrbiCcsWorkerDb orbiCcsWorkerDb;
    String dateFormater = "yyyy-MM-dd";
    Object time = new Object();

    public OrbiK8sManagerService() throws IOException {
        setPropertiesRequest();
    }

    public Response forceK8sJobExecution() {
        return doPostRequestJson(createJSONRequest(), K8S_CREATE_ENDPOINT);
    }

    public Response forceK8sJobExeWithSubstractDay() {
        return doPostRequestJson(createJSONRequestSubstractDay(), K8S_CREATE_ENDPOINT);
    }

    public Response returnKuberJobStatus() {
        return doGetRequestQuery(sendJobExecutionParameter("cronjob-orbi-ccs-worker"), K8S_STATUS_ENDPOINT);
    }

    public Map<String, Object> sendJobExecutionParameter(String cronjobName) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("cronjobName", cronjobName);

        return queryParams;
    }

    private String createJSONRequest() {
        String referenceDate = dateTimeUtil.getCurrentDate(dateFormater);
        JSONArray parameters = new JSONArray();
        parameters.put(referenceDate);
        parameters.put("teste2");
        parameters.put("teste3");

        JSONObject requestBody = new JSONObject();
        requestBody.put("executionId", "c766544a-cca1-4ec3-9b22-3222492db2ee");
        requestBody.put("cronjobName", "cronjob-orbi-ccs-worker");
        requestBody.put("parameters", parameters);

        return requestBody.toString();
    }

    private String createJSONRequestSubstractDay() {
        String referenceDate = dateTimeUtil
                .addOrSubtractDaysInADate(Integer.parseInt("-" + 1), dateFormater);
        JSONArray parameters = new JSONArray();
        parameters.put(referenceDate);
        parameters.put("teste2");
        parameters.put("teste3");

        JSONObject requestBody = new JSONObject();
        requestBody.put("executionId", "c766544a-cca1-4ec3-9b22-3222492db2ee");
        requestBody.put("cronjobName", "cronjob-orbi-ccs-worker");
        requestBody.put("parameters", parameters);

        return requestBody.toString();
    }

    public void validateSuccessResponse() throws IOException {
        JSONArray statusList = new JSONArray();
        JSONObject statusJson = new JSONObject();
        var succeededKey = "succeeded";
        var activeKey = "active";
        var failedKey = "failed";
        var numberStatusSucceeded = "1";

        synchronized (time) {
            for (int i = 0; i < 100; i++) {
                RequestManager.shared().setResponse(returnKuberJobStatus());
                Response resp = RequestManager.shared().getResponse();

                if (resp.statusCode() == 401) {
                    RequestManager.shared().setRequest(new AuthenticationService().getAccessTokenBackEnd());
                    setPropertiesRequest();
                    RequestManager.shared().setResponse(returnKuberJobStatus());
                    resp = RequestManager.shared().getResponse();
                }

                Assert.assertEquals(200, resp.statusCode());
                String active = resp.then().extract().jsonPath().getString(activeKey);
                String failed = resp.then().extract().jsonPath().getString(failedKey);
                String succeeded = resp.then().extract().jsonPath().getString(succeededKey);

                if (Objects.isNull(active) && (
                        numberStatusSucceeded.equals(succeeded) || numberStatusSucceeded.equals(failed))) {
                    statusJson.put(failedKey, active);
                    statusJson.put(activeKey, failed);
                    statusJson.put(succeededKey, succeeded);
                    statusList.put(statusJson);

                    break;
                }

                try {
                    time.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertTrue(statusList.length() > 0);
        Assert.assertEquals(numberStatusSucceeded, statusJson.getString(succeededKey));
    }

    private void setPropertiesRequest() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.k8s.manager.baseURI"));
        dateTimeUtil = new DateTimeUtil();
        orbiCcsWorkerDb = new OrbiCcsWorkerDb();
    }
}
