package service;

import base.RequestManager;
import base.db.OrbiCcsWorkerDb;
import base.util.DateTimeUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrbiCcsWorkerService extends BaseService {
    private static final String CCS_WORKER_ENDPOINT = "/v1/force-job-execution";
    DateTimeUtil dateTimeUtil;
    OrbiCcsWorkerDb orbiCcsWorkerDb;
    String dateFormater = "yyyy-MM-dd";

    public OrbiCcsWorkerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.ccs.worker.baseURI"));
        dateTimeUtil = new DateTimeUtil();
        orbiCcsWorkerDb = new OrbiCcsWorkerDb();
    }

    public Response forceCssWorkerJobExecution() {
        String referenceDate = dateTimeUtil.getCurrentDate(dateFormater);

        return doGetRequestQuery(sendJobExecutionParameter(referenceDate), CCS_WORKER_ENDPOINT);
    }

    public Response forceCssWorkerDataFutura() {
        String referenceDate = dateTimeUtil.addOrSubtractDaysInADate(1, dateFormater);

        return doGetRequestQuery(
                sendJobExecutionParameter(referenceDate), CCS_WORKER_ENDPOINT);
    }

    public Map<String, Object> sendJobExecutionParameter(String referenceDate) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("referenceDate", referenceDate);

        return queryParams;
    }

    public Response forceCssWorkerJobExecutionSubstractDays() {
        String referenceDate = dateTimeUtil.addOrSubtractDaysInADate(Integer.parseInt("-" + 1), dateFormater);

        return doGetRequestQuery(sendJobExecutionParameter(referenceDate), CCS_WORKER_ENDPOINT);
    }

    public void updateLastCompletedExecution(String lastDateExecution) {
        orbiCcsWorkerDb.updateLastExecutionDate(
                new DateTimeUtil().addOrSubtractLocalDate(Integer.parseInt(lastDateExecution), dateFormater));
    }
}
