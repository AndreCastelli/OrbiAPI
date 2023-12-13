package service;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbiPixIspbControllerService extends BaseService {

    private static final String SLASH = "/";
    private static final String ENDPOINT_VERSION = "/v1";
    private static final String ENDPOINT_PARTICIPANT = ENDPOINT_VERSION + "/participant";
    private static final String ENDPOINT_UPDATE = ENDPOINT_VERSION + "/update-routine";

    public OrbiPixIspbControllerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.pix.ispb.controller.baseURI"));
    }

    public Response getParticipantISPB() {
        return doGetWithoutParameters(ENDPOINT_PARTICIPANT);
    }

    public Response getParticipantDataISPB(String ispb) {
        return doGetWithoutParameters(ENDPOINT_PARTICIPANT + SLASH + ispb);
    }

    public Response createUpdateRoutinePayload() {
        return doPostRequestNoPayload(ENDPOINT_UPDATE);
    }

    public Response getParticipantPix(String isPixAble) {
        Map<String, Object> params = new HashMap<>();
        params.put("isPixAble", isPixAble);

        return doGetRequestWithListParams(params, ENDPOINT_PARTICIPANT);
    }
}
