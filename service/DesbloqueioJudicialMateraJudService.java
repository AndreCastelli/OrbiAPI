package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class DesbloqueioJudicialMateraJudService extends BaseService {
    private static final String SWAGGER_UNBLOCK_PROCESS = "/v1/desbloqueio-judicial/processar";
    private static final String JSON_FILE_UNBLOCK = "materajud//desbloqueioProcessar.json";
    JsonUtil jsonUtil;
    String idDesbloqueioJud;

    public DesbloqueioJudicialMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public String generateUnblockJudId() {
        idDesbloqueioJud = String.format("%19d", Math.abs(new Random().nextLong()));

        return idDesbloqueioJud;
    }

    public Response sendPayloadUnblockJud(double valor, int origemDesbloqueio,
                                          String cnpjCpf, String idBloqueio, String unblockJudIdGenerate) {
        return doPostRequestJson(
                createUnblockJudPayload(valor, origemDesbloqueio,
                        cnpjCpf, idBloqueio, unblockJudIdGenerate),
                SWAGGER_UNBLOCK_PROCESS);
    }

    public String createUnblockJudPayload(
            double valor, int origemDesbloqueio,
            String cnpjCpf, String idBloqueio, String unblockJudIdGenerate) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("valor", valor);
        requestBody.put("cnpjCpf", cnpjCpf);
        requestBody.put("idBloqueio", idBloqueio);
        requestBody.put("origemDesbloqueio", origemDesbloqueio);
        requestBody.put("idDesbloqueioJud", unblockJudIdGenerate);

        return requestBody.toString();
    }

    public Response sendPayloadUnblockIdBloqueio(String idBloqueio, Object cnpjCpf,
                                       String unblockJudIdGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_UNBLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idDesbloqueioJud", unblockJudIdGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_UNBLOCK_PROCESS);
    }

    public Response sendPayloadUnblockDocument(String idBloqueio, Object cnpjCpf,
                                                 String unblockJudIdGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_UNBLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idDesbloqueioJud", unblockJudIdGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_UNBLOCK_PROCESS);
    }

    public Response sendPayloadUnblock(String attributeValue, String attributeName, Object cnpjCpf,
                                       Object idBloqueio, String unblockJudIdGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_UNBLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put(attributeName, attributeValue);
        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("idDesbloqueioJud", unblockJudIdGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_UNBLOCK_PROCESS);
    }

    public Response sendPayloadUnblockAlreadyUnblocked(String cnpjCpf, String idBloqueio,
                                                   String unblockJudIdGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_UNBLOCK);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpf", cnpjCpf);
        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("idDesbloqueioJud", unblockJudIdGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_UNBLOCK_PROCESS);
    }
}
