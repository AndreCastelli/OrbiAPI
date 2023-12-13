package service;

import base.RequestManager;
import base.util.JsonUtil;
import base.util.PersonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiBffMobileHomeService extends BaseService {
    private static final String HOME_ACCOUNT_ENDPOINT = "/v1/home/account";
    private static final String HOME_ENDPOINT = "/v1/home/home";
    private static final String HOME_PERSON_ENDPOINT = "/v1/home/person";
    private static final String HOME_PERSON_NAME_ENDPOINT = "/v1/home/person/preferred-name";

    PersonUtil personUtil;
    JsonUtil jsonUtil;

    public OrbiBffMobileHomeService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.bff.mobile.baseURI"));
        jsonUtil = new JsonUtil();
        personUtil = new PersonUtil();
    }

    public Response consultHomeAccount() {
        return doForceJobExecute(HOME_ACCOUNT_ENDPOINT);
    }

    public Response consultHome() {
        return doForceJobExecute(HOME_ENDPOINT);
    }

    public Response consultHomePerson() {
        return doForceJobExecute(HOME_PERSON_ENDPOINT);
    }

    public Response changePreferredNameWithString(String name) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("preferredName", name);

        return doPatchRequestMap(payload, HOME_PERSON_NAME_ENDPOINT);
    }

    public Response changePreferredName() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("preferredName", personUtil.generatesFemaleName());

        return doPatchRequestMap(payload, HOME_PERSON_NAME_ENDPOINT);
    }
}