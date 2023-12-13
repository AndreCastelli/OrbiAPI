package service;

import base.RequestManager;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class OrbiIdentityManagerService extends BaseService {

    public static final String FIXED_BIRTHDATE = "1995-03-23";
    private static final String CREATE_CREDENTIAL = "/v1/credential";
    private static final String AUTH = "/auth";
    private static final String RESET_PASSWD = "/reset-passwd";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String HEADER_NAME = "Authorization";
    private static final String HEADER_VALUE = "Bearer ";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCOUNT_ID = "accountId";
    private static final String BIRTHDATE = "birthDate";
    private static final String PERSON_ID = "personId";
    private static final String DEVICE_ID = "deviceId";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    String deviceId;

    public OrbiIdentityManagerService() throws IOException {
        deviceId = MathUtil.getRandomUUID();
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.identity.manager.baseURI"));
    }

    public String createAuthPayload(String device, String cpf, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(DEVICE_ID, device);
        requestBody.put(USERNAME, cpf);
        requestBody.put(PASSWORD, password);

        return requestBody.toString();
    }

    public String createRefreshPayload(String device, String refresh) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(DEVICE_ID, device);
        requestBody.put("refreshToken", refresh);

        return requestBody.toString();
    }

    public RequestSpecification authenticate(String device, String username, String password) {
        String accessToken;
        accessToken = getAttributeFromPostRequest(createAuthPayload(
                device, username, password), AUTH, ACCESS_TOKEN);

        return new RequestSpecBuilder().addHeader(HEADER_NAME, HEADER_VALUE + accessToken).build();
    }

    public Response logout(String device, String refreshToken) {
        return doPostRequestJson(createRefreshPayload(device, refreshToken), "/logout");
    }

    public Response refresh(String device, String refreshToken) {
        return doPostRequestJson(createRefreshPayload(device, refreshToken), "/refresh");
    }

    public Response credential(String accountId, String personId, String device, String cpf, String password) {
        return doPostRequestJson(
                createPasswordPayload(accountId, personId, device, cpf, password), CREATE_CREDENTIAL);
    }

    public String createPasswordPayload(
            String accountId, String personId, String device, String cpf, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(ACCOUNT_ID, accountId);
        requestBody.put(DEVICE_ID, device);
        requestBody.put(PASSWORD, password);
        requestBody.put(PERSON_ID, personId);
        requestBody.put(USERNAME, cpf);

        return requestBody.toString();
    }

    public String createPasswordPayload(JSONObject userData, String password, String device) {
        System.out.println(userData.toString());
        JSONObject requestBody = new JSONObject();
        requestBody.put(ACCOUNT_ID, userData.get(ACCOUNT_ID));
        requestBody.put(BIRTHDATE, userData.get(BIRTHDATE));
        requestBody.put(PERSON_ID, userData.get(PERSON_ID));
        requestBody.put(DEVICE_ID, device);
        requestBody.put(USERNAME, userData.get("cpf"));
        requestBody.put(PASSWORD, password);

        return requestBody.toString();
    }

    public JSONObject authenticateIdentServer(String device, String password, String username) {
        Response respAuth = doPostRequestJson(createAuthPayload(device, username, password), AUTH);

        respAuth.then().log().all();
        respAuth.then().statusCode(HttpStatus.SC_OK);

        JSONObject loginData = new JSONObject();
        loginData.put(ACCESS_TOKEN, respAuth.then().extract().path(ACCESS_TOKEN).toString());
        loginData.put(EXPIRES_IN, respAuth.then().extract().path(EXPIRES_IN).toString());
        loginData.put(REFRESH_TOKEN, respAuth.then().extract().path(REFRESH_TOKEN).toString());
        loginData.put(TOKEN_TYPE, respAuth.then().extract().path(TOKEN_TYPE).toString());

        return loginData;
    }

    public Response createCredential(JSONObject userData, String password, String device) {
        return doPostRequestJson(createPasswordPayload(userData, password, device), CREATE_CREDENTIAL);
    }

    public Response resetPassword(String cpf, String newPassword, String birthdate) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(BIRTHDATE, birthdate);
        requestBody.put(PASSWORD, newPassword);
        return doPostRequestJson(requestBody.toString(), CREATE_CREDENTIAL+"/"+cpf+"/"+RESET_PASSWD);
    }
    public Response createNewCredential(String payload, String cpf) {
        return doPutRequestJson(payload, CREATE_CREDENTIAL+"/"+cpf);
    }

    public String createCredentialPayload(String password){
        switch (password) {
            case "partesDoCPF":
                password = RequestManager.shared().getHelp().substring(0, 5);
                break;
            case "dataAniversario":
                password = FIXED_BIRTHDATE.replaceAll("\\D+", "").substring(0, 5);
                break;
            case "partesTelefone":
                password = "999999";
                break;
            case "digitosIguais":
                password = "132343";
                break;
            case "sequenciais":
                password = "123456";
                break;
        }
        var payload = new JSONObject();
        payload.put("accountId", UUID.randomUUID().toString());
        payload.put("birthDate", FIXED_BIRTHDATE);
        payload.put("deviceId", RequestManager.shared().getDeviceId());
        payload.put("password", password);
        payload.put("personId", UUID.randomUUID().toString());
        return payload.toString();
    }
}
