package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PersonUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public class OrbiPersonManagerService extends BaseService {
    private static final String PERSON_MANAGER_ENDPOINT = "/v1/person";
    private static final String PERSON_V2_MANAGER_ENDPOINT = "/v2/person";
    private static final String PERSON_RATE_MANAGER_ENDPOINT = "/v1/person/rate";
    private static final String PAYLOAD_PERSON_RATE_REGISTRATION = "orbipersonmanager/personRatePayload.json";
    private static final String PAYLOAD_PERSON_REGISTRATION = "orbipersonmanager/personManagerPayload.json";
    private static final String PAYLOAD_PERSON_PREFERREDNAME = "orbipersonmanager/personNamePayload.json";
    private static final String EMPTY_PAYLOAD = "{}";
    private static final String SLASH = "/";
    private static final String CPF = "cpfCnpj";
    private static final String DOCK_ID = "dockId";
    private static final String SEPARATE_DUAL = "\\.";
    private static final String PERSON_EMAIL = "person.email";
    private static final String ADDRESS = "/address";
    private static final String PHONE = "/phone";
    private static Response personResp;

    JsonUtil jsonUtil;

    public OrbiPersonManagerService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.person.manager.baseURI"));
        jsonUtil = new JsonUtil();
    }

    public Response consultPerson(String cpfCnpj, String dockId) {
        return doGetRequestQuery(consultPersonPayload(cpfCnpj, dockId), PERSON_MANAGER_ENDPOINT);
    }

    public Response consultPersonByUuid(String personId) {
        return doGetRequestJson(EMPTY_PAYLOAD, PERSON_MANAGER_ENDPOINT + SLASH + personId);
    }

    public Map<String, Object> consultPersonPayload(String cpfCnpj, String dockId) {
        Map<String, Object> payload = new LinkedHashMap();

        if (dockId == null) {
            payload.put(CPF, cpfCnpj);
        } else if (cpfCnpj == null) {
            payload.put(DOCK_ID, dockId);
        } else {
            payload.put(CPF, cpfCnpj);
            payload.put(DOCK_ID, dockId);
        }

        return payload;
    }

    public Response personRateRegistrationPayload(String personId) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + PAYLOAD_PERSON_RATE_REGISTRATION);

        return registerPersonRate(payloadString, PERSON_RATE_MANAGER_ENDPOINT + SLASH + personId);
    }

    public Response registerPersonRate(String payload, String endPoint) {
        return doPostRequestJson(payload, endPoint);
    }

    public String personRegistrationPayload(PersonUtil person) throws IOException {
        String payloadString = new JsonUtil().generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + PAYLOAD_PERSON_REGISTRATION);

        payloadString = updatePayload("person.name", person.getName(), payloadString);
        payloadString = updatePayload(PERSON_EMAIL, person.getEmail(), payloadString);
        payloadString = updatePayload("person.cpfCnpj", person.getCpf(), payloadString);
        payloadString = updatePayload("person.motherName", person.getMother(), payloadString);
        payloadString = updatePayload("person.phone.number", person.getPhone(), payloadString);

        return payloadString;
    }

    public String saveUserJsonData(PersonUtil person) throws IOException {
        JSONObject jsonPerson = new JSONObject();

        jsonPerson.put("nome", person.getName());
        jsonPerson.put("cpf", person.getCpf());

        return jsonPerson.toString();
    }

    public String updatePayload(String attributeName, String attributeValue, String payloadString) {
        JSONObject jsonBody = new JSONObject(payloadString);

        if (attributeName.split(SEPARATE_DUAL).length > 2) {
            String[] attributeArr = attributeName.split(SEPARATE_DUAL);
            jsonBody.getJSONObject(attributeArr[0]).getJSONObject(attributeArr[1]).put(attributeArr[2], attributeValue);
        } else if (attributeName.contains(".")) {
            String[] attributeArr = attributeName.split(SEPARATE_DUAL);
            jsonBody.getJSONObject(attributeArr[0]).put(attributeArr[1], attributeValue);
        } else {
            jsonBody.put(attributeName, attributeValue);
        }

        return jsonBody.toString();
    }

    public Response waitForAccountCreation(String personPayload) {
        try {
            Awaitility.await().atMost(MathUtil.FORTY, SECONDS).until(() ->
                    validateDigitalPersonCreation(personPayload));
        } catch (ExceptionInInitializerError e) {
            System.out.println("Cadastro de Person falhou -- Timeout Log:" + e);
        }

        return personResp;
    }

    public boolean validateDigitalPersonCreation(String personPayload) {
        personResp = sendPersonRegistration(personPayload);
        System.out.println("Response Cadastro da Person StatusCode---> " + personResp.statusCode());

        return personResp.statusCode() == HttpStatus.SC_CREATED;
    }

    public Response sendPersonRegistration(String payload) {
        return doPostRequestJson(payload, PERSON_MANAGER_ENDPOINT);
    }

    public String payloadOnlyWithMandatoryData(String payload) {
        String payloadString = payload;

        payloadString = updatePayload("person.address.complement", "", payloadString);
        payloadString = updatePayload("person.fatherName", "", payloadString);
        payloadString = updatePayload("person.isPoliticallyExposedPerson", "", payloadString);
        payloadString = updatePayload("person.professionId", "", payloadString);

        return payloadString;
    }

    public Response getAddressPersonById(String personId) {
        return doGetRequestJson(EMPTY_PAYLOAD, PERSON_MANAGER_ENDPOINT + SLASH + personId + ADDRESS);
    }

    public Response getPhonePersonById(String personId) {
        return doGetRequestJson(EMPTY_PAYLOAD, PERSON_MANAGER_ENDPOINT + SLASH + personId + PHONE);
    }

    public Response changePersonPreferredName(String personUuid) throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + PAYLOAD_PERSON_PREFERREDNAME);

        return doPutRequestJson(payloadString, PERSON_MANAGER_ENDPOINT + SLASH + personUuid);
    }

    public Response updatePhonePersonRegistrationPayload(String novoNumero, String personId) throws IOException {

        JSONObject payload = new JSONObject();
        payload.put("cellphone", novoNumero);

        return doPutRequestJson(payload.toString(), PERSON_V2_MANAGER_ENDPOINT + SLASH + personId + PHONE);
    }

}