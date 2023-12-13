package base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestManager {

    private static RequestManager sharedInstance;

    private RequestSpecification request;
    private Response response;

    private String help;
    private String profile;
    private String deviceId;
    private String accountId;

    private JSONObject helpJsonObject;
    private Map helpHashMap = new LinkedHashMap<>();

    public static synchronized RequestManager shared() {
        if (sharedInstance == null) {
            sharedInstance = new RequestManager();
        }

        return sharedInstance;
    }

    public RequestSpecification getRequest() {
        if (request == null) {
            request = new RequestSpecBuilder().build();
        }

        return request;
    }

    public Response getResponse() {
        return response;
    }

    public String getHelp() {
        return help;
    }

    public String getProfile() {
        return profile;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public JSONObject getHelpJsonObject() {
        return helpJsonObject;
    }

    public Object getHashHelp(final String key) {
        return helpHashMap.get(key);
    }

    public void setRequest(final RequestSpecification request) {
        this.request = request;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }

    public void setHelp(final String help) {
        this.help = help;
    }

    public void setProfile(final String profile) {
        if (profile == null || !profile.trim().equalsIgnoreCase("local")) {
            this.profile = "default";
        } else {
            this.profile = profile.trim().toUpperCase();
        }
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public void setHelpJsonObject(final JSONObject helpJsonObject) {
        this.helpJsonObject = helpJsonObject;
    }

    public void addHashHelp(final String entry, final Object value) {
        helpHashMap.put(entry, value);
    }

    public void setBaseURI(final String uri) {
        this.getRequest().baseUri(uri);
    }

    public void setHeaders(final Map<String, ?> header) {
        this.getRequest().headers(header);
    }

    public void teardownRequest() {
        this.request = null;
    }

    public void teardownRequestResponse() {
        this.response = null;
    }
}