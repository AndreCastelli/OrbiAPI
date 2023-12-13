package service;

import base.ProjectSettings;
import base.RequestManager;
import base.db.MateraJudServiceDb;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class TransferenciaJudicialMateraJudService extends BaseService {
    private static final String SWAGGER_TRANSFER_JUD_PROCESS = "/v1/transferencia-judicial/processar";
    private static final String JSON_FILE_TRANSFER = "materajud//transferenciaProcessar.json";
    JsonUtil jsonUtil;
    String idTransferJud;
    String idTransferBacen;

    public TransferenciaJudicialMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public String generateIdTransferJud() {
        idTransferJud = String.format("%19d", Math.abs(new Random().nextLong()));

        return idTransferJud;
    }

    public String generateIdTransferBacen() {
        idTransferBacen = String.format("%18d", Math.abs(new Random().nextLong()));

        return idTransferBacen;
    }

    public JSONObject findBlockOperationsRecent() throws IOException {
        return (JSONObject) new MateraJudServiceDb().getBlockOperationsRecentForTransfer();
    }

    public JSONObject findBlockOperationsRecentWithoutTransfer() throws IOException {
        return (JSONObject) new MateraJudServiceDb().getBlockOperationsRecentWithoutTransfer();
    }

    public JSONObject findBlockOperationsRecentWithTransfer() throws IOException {
        return (JSONObject) new MateraJudServiceDb().getBlockOperationsRecentWithTransfer();
    }

    public Response sendPayloadTransfervalueattribute(String attributeValue, String attributeName,
                                                      String idBloqueio, String cnpjCpfReu,
                                                      String idTransferJudGenerate,
                                                      String idTransferBacenGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_TRANSFER);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put(attributeName, attributeValue);
        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("cnpjCpfReu", cnpjCpfReu);
        jsonBody.put("idTransferenciaJud", idTransferJudGenerate);
        jsonBody.put("idTransferenciaBacen", idTransferBacenGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_TRANSFER_JUD_PROCESS);
    }

    public Response sendPayloadTransferIdExisting(String amount, String idBloqueio,
                                                  String cnpjCpfReu, String idTransferJudGenerate,
                                                  String idTransferBacenGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_TRANSFER);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("valor", amount);
        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("cnpjCpfReu", cnpjCpfReu);
        jsonBody.put("idTransferenciaJud", idTransferJudGenerate);
        jsonBody.put("idTransferenciaBacen", idTransferBacenGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_TRANSFER_JUD_PROCESS);
    }

    public Response sendPayloadTransferIdLockNonexisting(String cnpjCpfReu, String idTransferJudGenerate,
                                                         String idTransferBacenGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_TRANSFER);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("cnpjCpfReu", cnpjCpfReu);
        jsonBody.put("idTransferenciaJud", idTransferJudGenerate);
        jsonBody.put("idTransferenciaBacen", idTransferBacenGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_TRANSFER_JUD_PROCESS);
    }

    public Response sendPayloadTransferDifferentDocument(String idBloqueio, String idTransferJudGenerate,
                                                         String idTransferBacenGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_TRANSFER);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("idTransferenciaJud", idTransferJudGenerate);
        jsonBody.put("idTransferenciaBacen", idTransferBacenGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_TRANSFER_JUD_PROCESS);
    }

    public Response sendPayloadTransferNotClient(String idBloqueio, String cnpjCpfReu, String idTransferJudGenerate,
                                                 String idTransferBacenGenerate) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_TRANSFER);

        JSONObject jsonBody = new JSONObject(payloadString);

        jsonBody.put("idBloqueio", idBloqueio);
        jsonBody.put("cnpjCpfReu", cnpjCpfReu);
        jsonBody.put("idTransferenciaJud", idTransferJudGenerate);
        jsonBody.put("idTransferenciaBacen", idTransferBacenGenerate);

        return doPostRequestJson(jsonBody.toString(), SWAGGER_TRANSFER_JUD_PROCESS);
    }

    public void deleteDataCollection(String collection) throws IOException {
        new MateraJudServiceDb().deleteCollection(collection);
    }
}
