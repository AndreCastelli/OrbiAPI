package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class AddressMateraJudService extends BaseService {
    private static final String SWAGGER_ADDRESS = "v1/requisicao-informacoes/enderecos";

    public AddressMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
    }

    public Response sendPayloadAddress(String cpfCnpj, String indicadorCpfCnpj, String relacionamentosEncerrados) {
        return doPostRequestJson(
                createAddressConfirmedPayload(cpfCnpj, indicadorCpfCnpj, relacionamentosEncerrados), SWAGGER_ADDRESS);
    }

    public String createAddressConfirmedPayload(String document, String entityType, String isClosedRelationships) {
        JSONObject requestBody = new JSONObject();

        if (Objects.nonNull(document) && !document.isBlank()) {
            requestBody.put("cpfCnpj", document);
        }

        if (Objects.nonNull(entityType) && !entityType.isBlank()) {
            requestBody.put("indicadorCpfCnpj", entityType);
        }

        if (Objects.nonNull(isClosedRelationships) && !isClosedRelationships.isBlank()) {
            requestBody.put("relacionamentosEncerrados", isClosedRelationships);
        }

        return requestBody.toString();
    }
}
