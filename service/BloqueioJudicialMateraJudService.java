package service;

import base.ProjectSettings;
import base.RequestManager;
import base.db.MateraJudServiceDb;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BloqueioJudicialMateraJudService extends BaseService {
    private static final String SWAGGER_BLOCK_CONSULT = "/v1/bloqueio-judicial";
    private static final String SWAGGER_BLOCK_PROCESS = "/v1/bloqueio-judicial/processar";
    private static final String JSON_FILE_BLOCK = "materajud//bloqueioProcessar.json";
    private static final String JSON_FILE_UNBLOCK = "materajud//desbloqueioProcessar.json";
    private static final String SWAGGER_UNBLOCK_PROCESS = "/v1/desbloqueio-judicial/processar";
    private static final String SWAGGER_FIND_BALANCE_BLOCK = "/v1/bloqueio-judicial/balance-blocked";
    JsonUtil jsonUtil;
    String idBloqueioJud;

    public BloqueioJudicialMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public String generateIdBloqueioJud() {
        idBloqueioJud = String.format("%19d", Math.abs(new Random().nextLong()));

        return idBloqueioJud;
    }

    public Response sendPayloadBlock(String attributeValue, String attributeName, Object cnpjCpf,
                                     Object conta, String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put(attributeName, attributeValue);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public Response sendPayloadUnblock(String cnpjCpf) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_UNBLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_UNBLOCK_PROCESS);
    }

    public Response sendPayloadBlock2(Object cnpjCpf, Object conta, String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public Response sendPayloadBlockAlreadyBlocked(String cnpjCpf, Integer conta,
                                                   String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("conta", conta);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public Response sendPayloadBlock3(Object cnpjCpf, String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public Response sendPayloadBlockVariable(String attributeName1, Integer attributeValue1,
                                             String attributeName2, Integer attributeValue2,
                                             Object cnpjCpf, String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName1.contains(".")) {
            String[] attributeArr = attributeName1.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue1);
        } else {
            jsonBody.put(attributeName1, attributeValue1);
        }

        if (attributeName2.contains(".")) {
            String[] attributeArr = attributeName2.split("\\.");
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue2);
        } else {
            jsonBody.put(attributeName2, attributeValue2);
        }

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public String findBlockOperations() throws IOException {
        return (String) new MateraJudServiceDb().getBlockOperationsRecent().get("blockExternalId");
    }

    public Response getBlockOperations(String blockExternalId) {
        return super.doGetRequestQuery(consultBlockOperationsPayload(blockExternalId), SWAGGER_BLOCK_CONSULT);
    }

    public Map<String, Object> consultBlockOperationsPayload(String blockExternalId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("idBloqueioJud", blockExternalId);

        return payload;
    }

    public Response consultBlock(String blockExternalId) {
        return doGetRequestQuery(consultBlockOperationsPayload(blockExternalId), SWAGGER_BLOCK_CONSULT);
    }

    public Response consultBalanceBlocked(String accountId) {
        return doGetRequestQuery(consultBalanceBlockedPayload(accountId), SWAGGER_FIND_BALANCE_BLOCK);
    }

    public Map<String, Object> consultBalanceBlockedPayload(String accountId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("accountId", accountId);

        return payload;
    }

    public Response sendPayloadBlockValueBlock(double vlrMonetarioBloq, String cnpjCpf,
                                               String idBloqueioJudGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("valor", vlrMonetarioBloq);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }

    public Response sendPayloadBlockDocument(String cnpjCpf, String idBloqueioJudGenerate)throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_BLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueioJud", idBloqueioJudGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_BLOCK_PROCESS);
    }
}
