package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.EncodedUtil;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.containsString;

public class OrbiOnboardingManagerService extends BaseService {
    private static final String GET_ACCOUNT_STATUS_USING_GET = "/v1/account/status?cpf={cpf}";
    private static final String GET_ACCOUNT_STATUS_USING_GET_INVALID = "/v1/ccount/status?cpf={cpf}";
    private static final String FACE_VALIDATION = "/v1/orbi/onboarding/face-validation";
    private static final String FACE_VALIDATION_MIGRATION_FLOW = "/v1/orbi/onboarding/migration/face-validation";
    private static final String CREATE_A_NEW_ACCOUNT = "/v1/account";
    private static final String CREATE_A_NEW_ACCOUNT_V2 = "/v2/account";
    private static final String TERMS_OPTIN = "/v1/orbi/onboarding/optin/v2/terms";
    private static final String TERMS_MIGRATION = "/v1/orbi/onboarding/migration/v2/terms";
    private static final String DIGITALACCESS_FOLDER = "onboardingmanager//biometry//";
    private static final String ONBOARDINGMANAGER_FOLDER = "onboardingmanager//";
    private static final String CREATE_ACCOUNT_AND_REGISTER_PASSWORD = "/v1/orbi/onboarding/password";
    private static final String REGISTER_PASSWORD = "/v1/orbi/onboarding/migration/password";
    private static final String IMAGE_BASE_64 = "imageBase64";
    private static final String CPF = "cpf";
    private static final String DEVICE_ID = "deviceId";
    private static final String EXECUTION_ENVIRONMENT_HML = "HML";
    private static final String USUARIO = "usuario";
    private static final String PASSWORD = "password";
    private static final String ORIGIN = "origin";
    private static final String PASSWORDRECOVERY = "/v1/password-recovery/sms";
    private static final String VALIDATECLIENT = "/v1/password-recovery";
    private static final String VALIDATETOKEN = "/v1/password-recovery/validate";
    private static final String CHANGEPASSWORD = "/v1/password-recovery/password";
    private static final String CELLPHONE = "/v1/orbi/onboarding/cellphone";
    private static final String EMAIL = "/v1/orbi/onboarding/email";
    private static final String ADDRESS = "/v1/orbi/onboarding/address";
    private static final String SEARCH_ADDRESS_BY_CEP = "/v1/orbi/onboarding/address/zipcode/";
    private static final String OCCUPATION = "/v1/orbi/onboarding/occupation";
    private static final String PROPERTY_VALUE = "/v1/orbi/onboarding/property-value";
    private static final String PROPOSAL_ANALYSIS = "/v1/orbi/onboarding/proposal-analysis";
    private static final String DEVICE_ID_HEADER = "deviceId";
    private static final String CPF_HEADER = "cpf";
    private static final String TOKEN_HEADER = "token";
    private static final String NEW_PASSWORD_NODE = "newPassword";
    private final String STRING_ZIPCODE = "zipCode";
    private final String STRING_STREET = "street";
    private final String STRING_NUMBER = "number";
    private final String STRING_COMPLEMENT = "complement";
    private final String STRING_NEIGHBORHOOD = "neighborhood";
    private final String STRING_CITY = "city";
    private final String STRING_STATE = "state";
    private final String STRING_ID = "id";
    private final String STRING_VALUE = "value";
    private EncodedUtil encodedUtil;
    private String environment;
    private String imgBase64;
    private String deviceId = "TestdeviceId";
    private String cpfFix = "50320731057";

    public OrbiOnboardingManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        String baseURI = propertiesUtil.getPropertyByName("orbi.onboarding.manager.baseURI");
        RequestManager.shared().setBaseURI(baseURI);
        environment = getEnvironment(baseURI);
        encodedUtil = new EncodedUtil();
    }

    public String getEnvironment(String environmentUrl) {
        if (environmentUrl.toUpperCase().contains(EXECUTION_ENVIRONMENT_HML)) {
            return EXECUTION_ENVIRONMENT_HML;
        } else {
            return "DEV";
        }
    }

    public Response consultTheUserSituation(String cpf) {
        return doGetRequestOneAttribute(GET_ACCOUNT_STATUS_USING_GET, cpf);
    }

    public Response consultTheUserSituationInvalidUrl(String cpf) {
        return doGetRequestOneAttribute(GET_ACCOUNT_STATUS_USING_GET_INVALID, cpf);
    }

    public Response createOrbiAccount(String origin, String cpf, String device) {
        Map queryParams = new LinkedHashMap();
        queryParams.put(CPF, cpf);
        queryParams.put(DEVICE_ID, device);
        queryParams.put(ORIGIN, origin);

        return doPostRequestQuery(queryParams, CREATE_A_NEW_ACCOUNT);
    }

    public Response createOrbiAccountV2(String origin, String cpf, String device) {
        Map payLoad = new LinkedHashMap();
        payLoad.put(CPF, cpf);
        payLoad.put(DEVICE_ID, device);
        payLoad.put(ORIGIN, origin);
        payLoad.put("channel", "ORBI");
        payLoad.put("ip", "200.176.2.10-vander-em-brasilia");
        payLoad.put("manufacturer", "LG");
        payLoad.put("model", "X1");
        payLoad.put("operatingSystem", "Android 10");
        payLoad.put("osVersion", "10");
        payLoad.put("password", "751359");

        return doPostRequestMap(payLoad, CREATE_A_NEW_ACCOUNT_V2);
    }

    public Response listTermsOptin(String cpf) {
        headerWithCPFAndDeviceId(cpf);

        return doGetWithoutParameters(TERMS_OPTIN);
    }

    public Response listTermsMigration(String cpf) {
        headerWithCPFAndDeviceId(cpf);

        return doGetWithoutParameters(TERMS_MIGRATION);
    }

    public Response sendTermAcceptanceOptin(String cpf) {
        headerWithCPFAndDeviceId(cpf);

        JSONArray payload = new JSONArray();
        payload.put(Map.of("type", "PRIVACY_TERM", "version", 1));
        payload.put(Map.of("type", "USAGE_TERM", "version", 1));

        return doPostRequestJson(payload.toString(), TERMS_OPTIN);
    }

    public Response sendTermAcceptanceMigration(String cpf) {
        headerWithCPFAndDeviceId(cpf);

        JSONArray payload = new JSONArray();
        payload.put(Map.of("type", "USAGE_TERM", "version", 1));

        return doPostRequestJson(payload.toString(), TERMS_MIGRATION);
    }

    public Response validateSelfie() throws IOException {
        return doPostRequestJson(imgBase64, FACE_VALIDATION);
    }

    public Map headerWithCPFAndDeviceId(String cpf) {
        Map params = new LinkedHashMap();
        params.put(CPF, cpf);
        params.put(DEVICE_ID, deviceId);

        return params;
    }

    public JSONObject getUserToValidateTheSelfie(String dataType) throws IOException {
        String userString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + ONBOARDINGMANAGER_FOLDER + "facialbiometricsdata.json");

        JSONArray biometricsUsersArray = new JSONArray(userString);

        for (int i = 0; i < biometricsUsersArray.length(); i++) {
            JSONObject biometricsUsersJson = biometricsUsersArray.getJSONObject(i);

            if (biometricsUsersJson.get(USUARIO).toString().equals(dataType + "_" + environment)
                    || biometricsUsersJson.get(USUARIO).toString().equals(dataType)) {
                return biometricsUsersJson;
            }
        }

        return null;
    }

    public void getUserInformationSelfie(String user) throws IOException {
        JSONObject payload = new JSONObject();
        JSONObject userData = getUserToValidateTheSelfie(user);

        deviceId = userData.getString(DEVICE_ID);
        imgBase64 = payload.put(IMAGE_BASE_64, encodedUtil.base64ImageEncoder(ProjectSettings.JSON_DATA_PATH
                + DIGITALACCESS_FOLDER + userData.getString(IMAGE_BASE_64))).toString();

        RequestManager.shared().setHelp(userData.getString(CPF));
        RequestManager.shared().setHeaders(headerWithCPFAndDeviceId(userData.getString(CPF)));
    }

    public void selectUserInformationInHTML(String cpf) {
        deviceId = RequestManager.shared().getDeviceId();
        imgBase64 = "{\"imageBase64\": \"iVBORw0KGgoAAAANSUhEUgAAAAgAAAAKCAIAAAAGpYjXAAAAAXNSR0IArs4c6QAAAARnQU1BA\"}";

        RequestManager.shared().setHeaders(headerWithCPFAndDeviceId(cpf));
    }

    public void getBase64Image(String user) throws IOException {
        JSONObject payload = new JSONObject();
        JSONObject userData = getUserToValidateTheSelfie(user);

        imgBase64 = payload.put(IMAGE_BASE_64, encodedUtil.base64ImageEncoder(ProjectSettings.JSON_DATA_PATH
                + DIGITALACCESS_FOLDER + userData.getString(IMAGE_BASE_64))).toString();
    }

    public void validateStatusOfFacialBiometrics(Response response, int code) {
        if (environment.contains(EXECUTION_ENVIRONMENT_HML.toLowerCase())) {
            response.then().statusCode(HttpStatus.SC_OK);
        } else {
            response.then().statusCode(code);
        }
    }

    public void validateStatusOfFacialBiometricsHML(Response response) {
        if (environment.contains(EXECUTION_ENVIRONMENT_HML.toLowerCase())) {
            response.then().statusCode(HttpStatus.SC_OK);
        }
    }

    public void valStatusAndMsgOfFacialBiometrics(Response response, int code, String message) {
        if ("HML".equals(environment)) {
            response.then().statusCode(HttpStatus.SC_OK);
        } else {
            response.then().statusCode(code);
            response.then().body(containsString(message));
        }
    }

    public void saveAccountInformationToMemory(String cpf, String personId, String accountId, String birthDate) {
        JSONObject accInformation = new JSONObject();
        accInformation.put(CPF, cpf);
        accInformation.put("personId", personId);
        accInformation.put("birthDate", birthDate);
        accInformation.put("accountId", accountId);
        RequestManager.shared().setHelpJsonObject(accInformation);
    }

    public void waitForAccountAvailability(String cpf) {
        Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() -> waitForAccountCreation(cpf));
    }

    public boolean waitForAccountCreation(String cpf) {
        Response resp = consultTheUserSituation(cpf);
        resp.then().statusCode(HttpStatus.SC_OK);

        return resp.then().extract().path("status").equals("CREATED");
    }

    public Response validateSelfieInHML() throws IOException {
        if (environment == EXECUTION_ENVIRONMENT_HML) {
            return validateSelfie();
        }

        return null;
    }

    public Response validateSelfieInHMLMigrationFlow() throws IOException {
        if (environment == EXECUTION_ENVIRONMENT_HML) {
            return doPostRequestJson(imgBase64, FACE_VALIDATION_MIGRATION_FLOW);
        }

        return null;
    }

    public void accountCreationRegisteringPassword(String password) {
        if (environment == EXECUTION_ENVIRONMENT_HML) {
            Map payloadPassword = new HashMap();
            payloadPassword.put(PASSWORD, password);

            RequestManager.shared().setResponse(
                    doPostRequestMap(payloadPassword, CREATE_ACCOUNT_AND_REGISTER_PASSWORD));
        }
    }

    public void valStatusAndMsgOfAccountCreation(Response response, int code) {
        if (EXECUTION_ENVIRONMENT_HML.equals(environment)) {
            response.then().log().all();
            response.then().statusCode(code);
        }
    }

    public void registerPasswordForMigrationFlow(String password) {
        if (environment == EXECUTION_ENVIRONMENT_HML) {
            JSONObject payloadPassword = new JSONObject();
            payloadPassword.put(PASSWORD, password);

            RequestManager.shared().setResponse(
                    doPostRequestJson(payloadPassword.toString(), REGISTER_PASSWORD));
        }
    }

    public Response searchClient() {
        Map header = Map.of(DEVICE_ID_HEADER, RequestManager.shared().getDeviceId(),
                CPF_HEADER, RequestManager.shared().getHelp());
        RequestManager.shared().setHeaders(header);

        return doGetWithoutParameters(VALIDATECLIENT);
    }

    public Response requestRecoveryPassword(int times) {
        return Stream.iterate(1, i -> i + 1)
                .limit(times)
                .map($ -> doPostRequestNoPayload(PASSWORDRECOVERY))
                .reduce((first, second) -> second)
                .get();
    }

    public Response requestRecoveryPassword() {
        return requestRecoveryPassword(1);
    }

    public Response validateToken(String token) {
        return validateToken(token, 1);
    }

    public Response validateToken(String token, int times) {
        JSONObject payload = new JSONObject();
        payload.put(TOKEN_HEADER, token);

        return Stream.iterate(1, i -> i + 1)
                .limit(times)
                .map($ -> doPostRequestJson(payload.toString(), VALIDATETOKEN))
                .reduce((first, second) -> second)
                .get();
    }

    public Response changePassword(String newPassword, String token) {
        JSONObject payload = new JSONObject();
        payload.put(NEW_PASSWORD_NODE, newPassword);
        payload.put(TOKEN_HEADER, token);

        return doPostRequestJson(payload.toString(), CHANGEPASSWORD);
    }

    public Response searchCurrentPhone() {
        return doGetWithoutParameters(CELLPHONE);
    }

    public Response updatePhone(String phone) {
        return doPostRequestJson(new JSONObject().put("cellphone", phone).toString(), CELLPHONE);
    }

    public Response searchCurrentEmail() {
        return doGetWithoutParameters(EMAIL);
    }

    public Response updateEmail(String email) {
        return doPostRequestJson(new JSONObject().put("email", email).toString(), EMAIL);
    }

    public Response searchCurrentAddress() {
        return doGetWithoutParameters(ADDRESS);
    }

    public Map<String, String> searchAndExtractCurrentAddress() {
        ResponseBody responseBody = searchCurrentAddress().body();
        return Map.of(STRING_ZIPCODE, responseBody.jsonPath().getString(STRING_ZIPCODE),
                STRING_STREET, responseBody.jsonPath().getString(STRING_STREET),
                STRING_NUMBER, responseBody.jsonPath().getString(STRING_NUMBER),
                STRING_COMPLEMENT, responseBody.jsonPath().getString(STRING_COMPLEMENT),
                STRING_NEIGHBORHOOD, responseBody.jsonPath().getString(STRING_NEIGHBORHOOD),
                STRING_CITY, responseBody.jsonPath().getString(STRING_CITY),
                STRING_STATE, responseBody.jsonPath().getString(STRING_STATE));
    }

    public Response searchAddressByCep(String cep) {
        return doGetWithoutParameters(SEARCH_ADDRESS_BY_CEP + cep);
    }

    public Response updateAddress(Map<String, String> address) {
        ResponseBody responseBody = searchAddressByCep(address.get(STRING_ZIPCODE));
        Map<String, String> foundAddress = new HashMap<>();
        try {
            foundAddress.put(STRING_STREET, responseBody.jsonPath().getString(STRING_STREET));
            foundAddress.put(STRING_NEIGHBORHOOD, responseBody.jsonPath().getString(STRING_NEIGHBORHOOD));
            foundAddress.put(STRING_ZIPCODE, responseBody.jsonPath().getString(STRING_ZIPCODE));
            foundAddress.put(STRING_CITY, responseBody.jsonPath().getString(STRING_CITY));
            foundAddress.put(STRING_STATE, responseBody.jsonPath().getString(STRING_STATE));
            foundAddress.put(STRING_NUMBER, address.get(STRING_NUMBER));
            foundAddress.put(STRING_COMPLEMENT, address.get(STRING_COMPLEMENT));
        } catch (NullPointerException e) {
            foundAddress.put(STRING_STREET, address.get(STRING_STREET));
            foundAddress.put(STRING_NEIGHBORHOOD, address.get(STRING_NEIGHBORHOOD));
            foundAddress.put(STRING_ZIPCODE, responseBody.jsonPath().getString(STRING_ZIPCODE));
            foundAddress.put(STRING_CITY, responseBody.jsonPath().getString(STRING_CITY));
            foundAddress.put(STRING_STATE, responseBody.jsonPath().getString(STRING_STATE));
            foundAddress.put(STRING_NUMBER, address.get(STRING_NUMBER));
            foundAddress.put(STRING_COMPLEMENT, address.get(STRING_COMPLEMENT));
        }

        return doPostRequestJson(new JSONObject(address).toString(), ADDRESS);
    }

    public Response searchCurrentOccupation() {
        return doGetWithoutParameters(OCCUPATION);
    }

    public int searchAndExtractCurrentOccupation() {
        String extractJson = searchCurrentOccupation().body().jsonPath().get().toString().replaceAll("=", ":");
        JSONArray occupationsArray = new JSONArray(extractJson);
        for (int i = 0; i < occupationsArray.length(); i++) {
            if (occupationsArray.getJSONObject(i).getBoolean("selected")) {
                return occupationsArray.getJSONObject(i).getInt(STRING_ID);
            }
        }
        // return '0' when dont has occupation
        return 0;
    }

    public Response updateOccupation(int idOccupation) {
        return doPostRequestJson(new JSONObject().put(STRING_ID, idOccupation).toString(), OCCUPATION);
    }

    public Response searchCurrentRangePropertyValue() {
        return doGetWithoutParameters(PROPERTY_VALUE);
    }

    public int searchAndExtractCurrentRangePropertyValue() {
        String extractJson = searchCurrentRangePropertyValue().body().jsonPath().get().toString().replaceAll("=", ":");
        JSONArray occupationsArray = new JSONArray(extractJson);
        for (int i = 0; i < occupationsArray.length(); i++) {
            if (occupationsArray.getJSONObject(i).getBoolean("selected")) {
                return occupationsArray.getJSONObject(i).getInt(STRING_VALUE);
            }
        }
        // return '0' when dont has property value
        return 0;
    }

    public Response updateRangePropertyValue(int rangePropertyValue) {
        return doPostRequestJson(new JSONObject().put(STRING_VALUE, rangePropertyValue).toString(), PROPERTY_VALUE);
    }

    public Response searchStatusProposalAnalysis() {
        return doGetWithoutParameters(PROPOSAL_ANALYSIS);
    }
}
