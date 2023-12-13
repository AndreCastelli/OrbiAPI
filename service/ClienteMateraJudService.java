package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClienteMateraJudService extends BaseService {

    private static final String CLIENTES = "v1/clientes";

    public ClienteMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
    }

    public Map<String, Object> consultClientParameter(String cpfCnpj, String indicadorCnpjCpf) {
        Map queryParams = new LinkedHashMap();
        queryParams.put("cpfCnpj", cpfCnpj);
        queryParams.put("indicadorCnpjCpf", indicadorCnpjCpf);

        return queryParams;
    }

    public Response consultClientStatus(String cpfCnpj, String indicadorCnpjCpf) {
        return doGetRequestQuery(consultClientParameter(cpfCnpj, indicadorCnpjCpf), CLIENTES);
    }
}
