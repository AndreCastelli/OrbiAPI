package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OptinOnboardingService extends BaseService {

    public OptinOnboardingService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("optin.onboarding.baseURI"));
    }

    public Map<String, String> setMapPayload(String cpf, String origin) {
        Map<String, String> payload = new HashMap<>();
        payload.put("cpf", cpf);
        payload.put("origin", origin);

        return payload;
    }

    public Response registerOptIn(String cpf, String origin) {
        return doPostRequestMap(setMapPayload(cpf, origin), "/opt-in");
    }
}