package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class InformationMateraJudService extends BaseService {
    private static final String SWAGGER_ADDRESS = "v1/requisicao-informacoes/contas";
    private static final String CPF_CNPJ = "cpfCnpj";
    private static final String AGENCIA = "agencia";
    private static final String CONTA = "conta";

    public InformationMateraJudService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.jud.manager.baseURI"));
    }

    public Response sendPayloadInformationCompleted(String document, String accountBranch, String accountNumber) {
        return doPostRequestJson(
                createInformationConfirmedPayload(document, accountBranch, accountNumber), SWAGGER_ADDRESS);
    }

    public String createInformationConfirmedPayload(String document, String accountBranch, String accountNumber) {
        JSONObject requestBody = new JSONObject();

        if (Objects.nonNull(document) && !document.isBlank()) {
            requestBody.put(CPF_CNPJ, document);
        }

        if (Objects.nonNull(accountBranch) && !accountBranch.isBlank()) {
            requestBody.put(AGENCIA, accountBranch);
        }

        if (Objects.nonNull(accountNumber) && !accountNumber.isBlank()) {
            requestBody.put(CONTA, accountNumber);
        }

        return requestBody.toString();
    }
}
