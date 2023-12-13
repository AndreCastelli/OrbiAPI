package service.datasource;

import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.DockClientUtil;
import io.restassured.response.Response;
import service.BaseService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DockService extends BaseService {

    private static final String ACCOUNT_DOCK_ENDPOINT = "/contas";
    private static final String SLASH = "/";

    DockClientUtil dockClientUtil;
    DateTimeUtil dateTimeUtil;

    public DockService() throws IOException {
        dockClientUtil = new DockClientUtil();
        dateTimeUtil = new DateTimeUtil();
        RequestManager.shared().setRequest(dockClientUtil.getAuthenticateToTheDockAPI());
        RequestManager.shared().setBaseURI("https://api.hml.caradhras.io");
    }

    public String getTickedIdFromDock(String tipoTransacao) {
        Map<String, String> params = new HashMap<>();
        params.put("service", tipoTransacao);
        params.put("date", dateTimeUtil.getDateFormattedToDockConnector());

        Response response = doGetRequestWithListParams(params, "/v1/transactions");
        String ticketId = response.getBody().jsonPath().get("ticket");

        return ticketId;
    }
}
