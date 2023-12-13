package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuickWithDrawalService extends BaseService {

    public static final String PROPERTIE_LIMIT_AVAILABLE = "person_id_limit_available";
    public static final String PROPERTIE_LIMIT_NO_AVAILABLE = "person_id_limit_no_available";
    public static final String PERSON_ID_LIMIT_MIN_AVAILABLE = "person_id_limit_min_available";
    public static final String PERSON_ID_CARENCIA = "person_id_carencia";
    public static final String PERSON_ID_DISABLED_CUSTOMER = "person_id_disable_customer";
    public static final String ANDROID_XSOURCE = "android";

    private static final String CONSULT_LIMIT_CUSTOMER_ENDPOINT = "/v1/limit?personId={personId}";
    private static final String CONSULT_PARAMETER_SIMULATION_ENDPOINT = "/v1/simulation/parameter?personId={personId}";
    private static final String CONSULT_AVAILABILITY_ENDPOINT = "/v1/availability?personId={personId}&operations={operations}";
    private static final String OPERATIONS = "SIMULATION, ACCOUNT_CREDIT";
    private static final String POST_SIMULATION_ENDPOINT = "/v1/simulation?personId={personId}";
    private static final String GET_SIMULATION_ENDPOINT = "/v1/simulation/{id}?personId={personId}";
    private static final String GET_DOCUMENT_ENDPOINT = "/v1/document?personId={personId}";
    private static final String ANALYSIS_ENDPOINT = "/v1/analysis?personId={personId}";

    public QuickWithDrawalService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("quick.with.drawal.manager.baseURI"));
    }

    public Response getLimit(String personId) {
        return doGetRequestOneAttribute(CONSULT_LIMIT_CUSTOMER_ENDPOINT, personId);
    }

    public Response getSimulationParameters(String personId) {
        return doGetRequestOneAttribute(CONSULT_PARAMETER_SIMULATION_ENDPOINT, personId);
    }

    public Response getAvailability(String personId, String xSource) {
        Map<String, String> params = new HashMap<>();
        params.put("personId", personId);
        params.put("operations", OPERATIONS);

        return doGetRequestWithListParamsAndXsource(params,
                CONSULT_AVAILABILITY_ENDPOINT.
                        replace("{personId}", personId).
                        replace("{operations}", OPERATIONS), xSource);
    }

    public Response postSimulation(Double amount, String planId, String personId, String xSource) {
        return doPostRequestJsonWithXsource(payLoadPostSimulation(amount, planId), POST_SIMULATION_ENDPOINT, personId, xSource);
    }

    public String payLoadPostSimulation(Double amount, String planId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("amount", amount);
        requestBody.put("planId", planId);

        return requestBody.toString();
    }

    public Response getSimulation(String id, String personId, String xSource) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("personId", personId);

        return doGetRequestWithListParamsAndXsource(params,
                GET_SIMULATION_ENDPOINT.
                        replace("{id}", id).
                        replace("{personId}", personId), xSource);
    }

    public Response getDocument(String personId, String xSource) {
        return doGetRequestTwoAttributes(GET_DOCUMENT_ENDPOINT, personId, xSource);
    }

    public Response postAnalysis(String personId, String xSource) {
        return doPostRequestWithXsource(ANALYSIS_ENDPOINT, personId, xSource);
    }

    public Response postSimulationWrong(String planId, String personId, String xSource) {
        return doPostRequestJsonWithXsource(payLoadPostSimulationWrong(planId), POST_SIMULATION_ENDPOINT, personId, xSource);
    }

    public String payLoadPostSimulationWrong(String planId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("planId", planId);

        return requestBody.toString();
    }

    public Response getAnalysis(String personId, String xSource) {
        return doGetRequestTwoAttributes(ANALYSIS_ENDPOINT, personId, xSource);
    }
}