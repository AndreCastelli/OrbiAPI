package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WhitelistService extends BaseService {
    private static final String URL_RESOURCE = "v2/whitelist/{cpf}";

    public WhitelistService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("whitelist.baseURI"));
    }

    public Response searchCPFOnWhitelist(String cpf) {
        return doGetRequestOneAttribute(URL_RESOURCE, cpf);
    }

    public Response registerCPFInWhiteList(String cpf) {
        return doPutRequestOneAttribute(URL_RESOURCE, cpf);
    }

    public Response deleteCPFInWhiteList(String cpf) {
        return doDeleteRequest(URL_RESOURCE, cpf);
    }

    public void deleteCpfIfItIsOnTheWhitelist(String cpf) {
        if (searchCPFOnWhitelist(cpf).statusCode() == HttpStatus.SC_OK) {
            deleteCPFInWhiteList(cpf);
        }
    }

    public void registerCpfIfItIsNotOnTheWhitelist(String cpf) {
        if (searchCPFOnWhitelist(cpf).statusCode() == HttpStatus.SC_NOT_FOUND) {
            searchCPFOnWhitelist(cpf);
        }
    }
}
