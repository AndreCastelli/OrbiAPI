package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OrbiApixService extends BaseService {

    private static final String DIC_TRANSACTION_RESOURCE = "/v1/events/dict/transaction";
    private static final String TRANSACTION_RESOURCE = "/v1/events/transaction";
    private static final String PARAMETERS_RESOURCE = "/v1/parameters";
    private static final String AVAILABILITY_INCIDENT_RESOURCE = "v1/events/availability/incident";
    private static final String DICT_TRANSACTION_INTERNAL_SEARCH_RESOURCE = "/v1/events/dict/internal-search/count";
    private static final String POST_PARAMETERS_PAYLOAD = "orbiapixservice//PostParameters.txt";
    private static final String TRANSACTION_QUERY_PARAM = "?transactionId={transactionId}";
    private static final String REFERENCE_DATE_QUERY_PARAM = "?referenceDate={referenceDate}";

    private Response dictResponse;
    private AuthenticationService authenticationService;

    public OrbiApixService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        //authenticationService = new AuthenticationService();
        //RequestManager.shared().setRequest(authenticationService.getAccessTokenBackEnd());
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.apix.service.baseURI"));
    }

    public Response getDictTransaction(String transactionId) {
        Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() -> waitDictTransaction(transactionId));

        return dictResponse;
    }

    public Response sendInvalidRequest(String transactionId) {
        return doGetRequestOneAttribute(DIC_TRANSACTION_RESOURCE + TRANSACTION_QUERY_PARAM, transactionId);
    }

    public Response sendNullRequest() {
        return doForceJobExecute(DIC_TRANSACTION_RESOURCE);
    }

    public boolean waitDictTransaction(String transactionId) {
        dictResponse = doGetRequestOneAttribute(DIC_TRANSACTION_RESOURCE + TRANSACTION_QUERY_PARAM, transactionId);

        return dictResponse.statusCode() == HttpStatus.SC_OK;
    }

    public Response getTransaction(String transactionId) {
        return doGetRequestOneAttribute(TRANSACTION_RESOURCE + TRANSACTION_QUERY_PARAM, transactionId);
    }

    public Response findAvailabilityIncident(String referenceDate) {
        return doGetRequestOneAttribute(AVAILABILITY_INCIDENT_RESOURCE + REFERENCE_DATE_QUERY_PARAM, referenceDate);
    }

    public Response getTransactionDictInternalSearch(String referenceDate) {
        return doGetRequestOneAttribute(DICT_TRANSACTION_INTERNAL_SEARCH_RESOURCE
                + REFERENCE_DATE_QUERY_PARAM, referenceDate);
    }

    public Response getParameters() {
        return doGetWithoutParameters(PARAMETERS_RESOURCE);
    }

    public Response postParameters(String recipients) throws IOException {
        String payload = createPostParametersPayload(recipients);

        return doPostRequestJson(payload, PARAMETERS_RESOURCE);
    }

    public String createPostParametersPayload(String recipients) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + POST_PARAMETERS_PAYLOAD);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);
        String result = data.replace("stringRecipients", recipients);

        return result;
    }

    public Map<String, Object> consultDictTransactionParameter(String transactionExternalId, String transactionId) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("transactionExternalId", transactionExternalId);
        queryParams.put("transactionId", transactionId);

        return queryParams;
    }

    public Response consultDictTransaction(String transactionExternalId, String transactionId) {
        Response response = doGetRequestQuery(consultDictTransactionParameter(transactionExternalId, transactionId),
                DIC_TRANSACTION_RESOURCE);

        return response;
    }

    public Map<String, Object> consultTransactionParameter(String transactionId) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("transactionId", transactionId);

        return queryParams;
    }

    public Response consultTransaction(String transactionId) {
        Response response = doGetRequestQuery(consultTransactionParameter(transactionId), TRANSACTION_RESOURCE);

        return response;
    }
}
