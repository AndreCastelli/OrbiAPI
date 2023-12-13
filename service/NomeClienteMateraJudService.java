package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class NomeClienteMateraJudService extends BaseService {
    private static final String NOME_CLIENTE = "v1/clientes/nome";
    JsonUtil jsonUtil;

    public NomeClienteMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response consultName(String cpfCnpj) {
        return doGetRequestQuery(consultNamePayload(cpfCnpj), NOME_CLIENTE);
    }

    public Map<String, Object> consultNamePayload(String cpfCnpj) {
        Map<String, Object> payload = new LinkedHashMap();
        payload.put("cpfCnpj", cpfCnpj);

        return payload;
    }
}
