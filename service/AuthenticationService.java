package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService extends BaseService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String URL_ENCODING = "application/x-www-form-urlencoded";
    private static final String CLIENT_ID = "client_id";
    private static final String GRANT_TYPE = "grant_type";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String HEADER_NAME = "Authorization";
    private static final String HEADER_VALUE = "Bearer ";
    private static final String RESOURCE = "protocol/openid-connect/token";
    private static final String CLIENT_SECRET = "client_secret";

    public AuthenticationService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.back.baseURI"));
    }

    public RequestSpecification getAccessTokenBackEnd() {
        Map<String, String> loginMap;
        String accessToken;
        loginMap = new HashMap<>();
        loginMap.put(CONTENT_TYPE, URL_ENCODING);
        loginMap.put(CLIENT_ID, propertiesUtil.getPropertyByName("authentication.back.clientId"));
        loginMap.put(CLIENT_SECRET, propertiesUtil.getPropertyByNameBase64("authentication.back.clientSecret"));
        loginMap.put(GRANT_TYPE, propertiesUtil.getPropertyByName("authentication.back.grantType"));

        accessToken = getAttributeNotAuthen(loginMap, RESOURCE, ACCESS_TOKEN);
        return new RequestSpecBuilder().addHeader(HEADER_NAME, HEADER_VALUE + accessToken).build();
    }

    public RequestSpecification getAccessTokenFrontEnd() {
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.front.baseURI"));

        Map<String, String> loginMap;
        String accessToken;

        loginMap = new HashMap<>();
        loginMap.put(CONTENT_TYPE, URL_ENCODING);
        loginMap.put(CLIENT_ID, propertiesUtil.getPropertyByName("authentication.front.clientId"));
        loginMap.put(CLIENT_SECRET, propertiesUtil.getPropertyByNameBase64("authentication.front.clientSecret"));
        loginMap.put(GRANT_TYPE, propertiesUtil.getPropertyByName("authentication.front.grantType"));
        accessToken = getAttributeNotAuthen(
                loginMap, RESOURCE, ACCESS_TOKEN);

        return new RequestSpecBuilder().addHeader(HEADER_NAME, HEADER_VALUE + accessToken).build();
    }

    public RequestSpecification getBasicAuth() {
        PreemptiveBasicAuthScheme authenticationScheme = new PreemptiveBasicAuthScheme();
        authenticationScheme.setUserName(propertiesUtil.getPropertyByNameBase64("security.basic.username"));
        authenticationScheme.setPassword(propertiesUtil.getPropertyByNameBase64("security.basic.password"));

        return new RequestSpecBuilder().setAuth(authenticationScheme).build();
    }

    public RequestSpecification getAccessTokenWso2BackEnd() {
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("authentication.wso2.back.baseURI"));
        Map<String, String> loginMap;
        String accessToken;

        loginMap = new HashMap<>();
        loginMap.put(CONTENT_TYPE, URL_ENCODING);
        loginMap.put(CLIENT_ID, propertiesUtil.getPropertyByName("authentication.wso2.back.clientId"));
        loginMap.put(CLIENT_SECRET, propertiesUtil.getPropertyByName("authentication.wso2.back.clientSecret"));
        loginMap.put(GRANT_TYPE, propertiesUtil.getPropertyByName("authentication.wso2.back.grantType"));
        accessToken = getAttributeNotAuthen(loginMap, ACCESS_TOKEN);

        return new RequestSpecBuilder().addHeader(HEADER_NAME, HEADER_VALUE + accessToken).build();
    }
}
