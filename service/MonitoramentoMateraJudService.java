package service;

import base.ProjectSettings;
import base.RequestManager;
import base.db.MateraJudServiceDb;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

public class MonitoramentoMateraJudService extends BaseService {

    private static final String SWAGGER_START_MONITORING = "/v1/monitoramento/inicializar";
    private static final String SWAGGER_FINISH_MONITORING = "/v1/monitoramento/finalizar";
    private static final String SWAGGER_PROCESS_MONITORING = "/v1/monitoramento/process";
    private static final String JSON_FILE_START_MONITORING = "materajud//monitoramentoInicializar.json";
    JsonUtil jsonUtil;

    public MonitoramentoMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response sendPayloadMonitoring(String attributeValue, String attributeName, Object cnpjCpf,
                                          Object conta, Object idBloqueioJud) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put(attributeName, attributeValue);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJud);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_START_MONITORING);
    }

    public Response sendPayloadMonitoringDocument(String cnpjCpf, String idBloqueioJud) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJud);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_START_MONITORING);
    }

    public Response sendPayloadFinishMonitoring(String cnpjCpf, String conta, String idBloqueioJud) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJud);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_FINISH_MONITORING);
    }

    public Response sendPayloadFinishMonitoring(String cnpjCpf, String conta, String idBloqueioJud,
                                                String errorParameter) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJud);
        jsonBody.put("indicadorCpfCnpjReu", errorParameter);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_FINISH_MONITORING);
    }

    public Response sendPayloadMonitoringDocumentAmount(String amount, String cnpjCpf,
                                                        String idBloqueioJud) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("valor", amount);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJud);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_START_MONITORING);
    }

    public JSONObject findMonitoringBlockRecent() throws IOException {
        return (JSONObject) new MateraJudServiceDb().getMonitoringBlockedRecent();
    }

    public void findMonitoringMessage(String eventType, String uuidAccount, String idBloqueioJud) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypekafka = "";
        String accountId = "";
        String blockExternalId = "";
        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (eventType.equals(jsonObject.getJSONObject("metadata").getString("eventType"))
                    && uuidAccount.equals(jsonObject.getJSONObject("payload").getString("accountId"))
                    && idBloqueioJud.equals(jsonObject.getJSONObject("payload").getString("blockExternalId"))) {
                eventTypekafka = jsonObject.getJSONObject("metadata").getString("eventType");
                accountId = jsonObject.getJSONObject("payload").getString("accountId");
                blockExternalId = jsonObject.getJSONObject("payload").getString("blockExternalId");
                break;
            }
        }

        Assert.assertEquals(eventType, eventTypekafka);
        Assert.assertEquals(uuidAccount, accountId);
        Assert.assertEquals(idBloqueioJud, blockExternalId);
    }

    public Response sendPayloadMonitoringNotBlock(String attributeValue, String attributeName,
                                                  String cnpjCpf, String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put(attributeName, attributeValue);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_START_MONITORING);
    }

    public Response sendPayloadProcessMonitoringBlock(String value, String cnpjCpf,
                                                      String idBloqueioJud) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_START_MONITORING);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("valor", value);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJud);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_PROCESS_MONITORING);
    }

    public JSONObject findBlockNotify(String idBloqueioJud) throws IOException {
        return (JSONObject) new MateraJudServiceDb().getBlockNotify(idBloqueioJud);
    }
}
