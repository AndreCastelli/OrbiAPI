package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.IOException;

public class OrbiBackofficeManagerService extends BaseService {

    private static final String BACKOFFICE_LOGIN_ENDPOINT = "/authorization/login";
    private final String grantPassword = "password";
    private final String grantRefrehToken = "refresh_token";

    public OrbiBackofficeManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.backoffice.manager.baseURI"));
    }

    public String createLoginPayload(String username, String password) {
        JSONObject payload = new JSONObject();
        payload.put("username", username);
        payload.put("password", password);
        payload.put("grant", grantPassword);

        return payload.toString();
    }

    public Response doPostLogin(String username, String password) {
        return doPostRequestJson(createLoginPayload(username, password), BACKOFFICE_LOGIN_ENDPOINT);
    }

    public String createRefreshTokenPayload(String refresh) {
        JSONObject payload = new JSONObject();
        payload.put("refreshToken", refresh);
        payload.put("grant", grantRefrehToken);

        return payload.toString();
    }

    public Response refreshToken(String refresh) {
        return doPostRequestJson(createRefreshTokenPayload(refresh), BACKOFFICE_LOGIN_ENDPOINT);
    }
}