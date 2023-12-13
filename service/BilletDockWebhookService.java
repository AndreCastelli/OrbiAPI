package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BilletDockWebhookService extends BaseService {
    private static final String BOLETO = "idBoleto";
    private static final String CONTA = "idConta";
    private static final String SWAGGER_BILLET = "/v1/billet";

    public BilletDockWebhookService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("dock.webhook.baseURI"));
    }

    public Response createBilletSettlementUrlIncorreta(Map<String, Object> payload, int idBoleto)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(
                createBilletSettlementPayload(payload, idBoleto), "/v1/bille");
    }

    public Response createBilletSettlementWithoutMandatoryData(String mandatoryField)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createInvalidPayload(mandatoryField), SWAGGER_BILLET);
    }

    public String createInvalidPayload(String mandatoryField) throws NoSuchFieldException, IllegalAccessException {
        JsonUtil jsonUtil = new JsonUtil();

        Map payload = new LinkedHashMap();

        if (mandatoryField.equals(BOLETO)) {
            payload.put(CONTA, "2034");
        } else if (mandatoryField.equals(CONTA)) {
            payload.put(BOLETO, "2042");
        }

        JSONObject requestBody = new JSONObject();
        jsonUtil.returnOrderedJsonObject(requestBody);

        return requestBody.toString();
    }

    public Response createBilletSettlementWithInvalidPayload(String idBoleto) {
        String json = "{\"idConta\":2034,\"idBoleto\":" + idBoleto + ",\"valorBoleto\":5}";

        return doPostRequestJson(json, SWAGGER_BILLET);
    }

    public Map<String, Object> createBilletSettlementIdConta(int idConta) {
        Map payload = new LinkedHashMap();
        payload.put(CONTA, idConta);

        return payload;
    }

    public Response createBilletSettlement(Map<String, Object> payload, int idBoleto)
            throws NoSuchFieldException, IllegalAccessException {
        return doPostRequestJson(createBilletSettlementPayload(payload, idBoleto), SWAGGER_BILLET);
    }

    public String createBilletSettlementPayload(Map<String, Object> payload, int idBoleto)
            throws NoSuchFieldException, IllegalAccessException {
        payload.put(BOLETO, idBoleto);
        payload.put("valor", "0.05");

        return new JSONObject(payload).toString();
    }
}
