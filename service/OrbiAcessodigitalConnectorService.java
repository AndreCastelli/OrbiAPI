package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.EncodedUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiAcessodigitalConnectorService extends BaseService {
    private static final String DIGITALACCESS_FOLDER = "acessodigitalconnector//";
    private static final String VALID_ENDPOINT = "/biometry/faces/validation";
    private static final String INVALID_ENDPOINT = "/biometry/fac/validation";
    private static final String DOCUMENT = "document";
    private static final String IMAGEBASE64 = "imageBase64";

    public OrbiAcessodigitalConnectorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(
                propertiesUtil.getPropertyByName("authentication.acessodigital.connector.baseURI"));
    }

    public String createPayload(String cpf, String nameImage) {
        EncodedUtil encodedUtil = new EncodedUtil();
        JSONObject requestBody = new JSONObject();
        requestBody.put(DOCUMENT, cpf);
        requestBody.put(IMAGEBASE64, encodedUtil.base64ImageEncoder(
                ProjectSettings.JSON_DATA_PATH + DIGITALACCESS_FOLDER + nameImage));

        return requestBody.toString();
    }

    public String createInvalidPayload(String cpf, String nameImage) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(DOCUMENT, cpf);
        requestBody.put(IMAGEBASE64, nameImage);

        return requestBody.toString();
    }

    public Response sendRequestForValidation(String payloadJson) {
        return doPostRequestJson(payloadJson, VALID_ENDPOINT);
    }

    public Response sendRequestForValidationUrlInvalida(String payload) {
        return doPostRequestJson(payload, INVALID_ENDPOINT);
    }
}
