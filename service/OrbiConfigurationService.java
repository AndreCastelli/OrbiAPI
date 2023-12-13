package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiConfigurationService extends BaseService {
    private static final String BUSCAR_PARAMETROS_MOBILE_BACKEND = "/parameters/find?mobile={platformType}";
    private static final String FETCH_A_CERTAIN_PARAMETER = "parameters?name={parametername}";
    private static final String GENERAL_PARAMETER = "/v1/general-parameter";
    private static final String FETCH_A_CERTAIN_FEATURE = "features?name={parametername}";
    private static final String FETCH_FEATURES_FIND_STATUS = "features/find?enabled={status}";

    public OrbiConfigurationService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(
                propertiesUtil.getPropertyByName("orbi.configuration.baseURI"));
    }

    public Response consultParameters(String platformType) {
        return doGetRequestOneAttribute(
                BUSCAR_PARAMETROS_MOBILE_BACKEND, searchParameters(platformType));
    }

    public String searchParameters(String platformType) {
        Map<String, String> quaryParameter = new HashMap<>();
        quaryParameter.put("mobile", "true");
        quaryParameter.put("backend", "false");

        return quaryParameter.get(platformType);
    }

    public Response consultParametersPassingInvalidData(String platformType) {
        return doGetRequestOneAttribute(
                BUSCAR_PARAMETROS_MOBILE_BACKEND, platformType);
    }

    public Response searchForParameterInformation(String parametro) {
        return doGetRequestOneAttribute(
                FETCH_A_CERTAIN_PARAMETER, parametro);
    }

    public Response searchForFeatureInformation(String parametro) {
        return doGetRequestOneAttribute(
                FETCH_A_CERTAIN_FEATURE, parametro);
    }

    public Response consultFeaturePassingStatus(String parametro) {
        return doGetRequestOneAttribute(
                FETCH_FEATURES_FIND_STATUS, parametro);
    }

    public Response sendPatchRequest(String value) {
        return doPatchRequestMap(createJsonPayload(value), GENERAL_PARAMETER);
    }

    public Map<String, Object> createJsonPayload(String value) {
        JSONArray applicationId = new JSONArray();
        applicationId.put("orbi-ccs-worker");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "EXECUTE_ON_WEEKENDS");
        payload.put("value", value);
        payload.put("context", "REGULATORY-CCS");
        payload.put("dataType", "EXECUTE_ON_WEEKENDS");
        payload.put("applicationId", applicationId);
        payload.put("dateLastUpdate", "2021-04-13T17:20:46.118");

        return payload;
    }
}
