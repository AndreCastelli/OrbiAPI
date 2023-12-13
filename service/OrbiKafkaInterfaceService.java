package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.DateTimeUtil;
import base.util.JsonUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import service.datasource.ConductorService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrbiKafkaInterfaceService extends BaseService {

    private static final String PATH_KAFKA = "/v1/kafka";
    private static final String PAYLOAD_TRANSACTION_EVENT = "orbikafkainterface//postTransactionEvent.txt";
    private static final String PAYLOAD_PIX_TRANSACTION_EVENT = "orbikafkainterface//postPixTransactionEvent.txt";
    private static final String PAYLOAD_DICT_INTERNAL_SEARCH = "orbikafkainterface//postDictInternalSearch.txt";
    private static final String PAYLOAD_TRANSACTION_DICT_EVENT = "orbikafkainterface//postDictTransaction.txt";
    private static final String
            PAYLOAD_DICT_TRANSACTIONTYPE_NULL = "orbikafkainterface//postDictTransactiontypeNull.txt";
    private static final String
            PAYLOAD_ONE_AVAILABILITY_INCIDENT = "orbikafkainterface//postOneVailabilityIncident.txt";
    private static final String
            PAYLOAD_MULTIPLE_AVAILABILITY_INCIDENT = "orbikafkainterface//postMultiplesAvailabilityIncident.txt";
    private static final String JSON_FILE_NAME = "orbireceiptmanager/orbireceiptmanager.json";
    private static final String SEPARATE_DUAL = "\\.";
    private static final int DAYS = 3;
    private static final Double AMOUNT = 10.2000;

    private static final String SEARCH = "SEARCH";
    private static final String FIELD_TOPIC = "topic";
    private static final String FIELD_GROUP_ID = "groupId";
    private static final String FIELD_EVENT_TYPE = "eventType";
    private static final String FIELD_ENTRY_TYPE = "entryType";
    private static final String FIELD_TRANSACTION_TYPE = "transactionType";
    private static final String FIELD_TRANSACTION_DATE = "transactionDate";
    private static final String FIELD_APPLICATION_PIX_ENTRIES = "orbi-pix-entries-manager";
    private static final String FIELD_APPLICATION = "application";

    private static final String STRING_TRANSACTION_ID = "stringTransactionId";
    private static final String STRING_TRANSACTION_TYPE = "stringTransactionType";
    private static final String STRING_ACCOUNT_ID = "stringAccountId";
    private static final String STRING_TRANSACTION_DATE = "stringTransactionDate";
    private static final String DOUBLE_AMOUNT = "doubleAmount";
    private static final String BOOLEAN_TRANSACTION_COMPLETED = "booleanTransactionCompleted";
    private static final String STRING_TRANSACTION_STATUS = "stringTransactionStatus";

    private static final String STRING_TRANSACTION_EXTERNAL = "stringTransactionExternalId";
    private static final String STRING_ENTRY_TYPE = "stringEntryType";
    private static final String STRING_REFERENCE_DATE = "stringReferenceDate";
    private static final String STRING_INTERNAL_KEY_SEARCHES_COUNT = "stringInternalKeySearchesCount";

    private static final String FIELD_META_DATA = "metadata";
    private static final String FIELD_KEY_VALUE = "keyValue";
    private static final String FIELD_KEY_TYPE = "keyType";
    private static final String FIELD_PERSON_ID = "personId";
    private static final String FIELD_PAYLOAD = "payload";
    private static final String FIELD_ACCOUNT_NUMBER = "accountNumber";
    private static final String FIELD_TRANSACTION_ID = "transactionId";
    private static final String FIELD_DESTINATION_DATA = "destinationData";
    private static final String FIELD_DOCUMENT = "document";
    private static final String FIELD_ENTITY_DOCUMENT = "entityDocument";
    private static final String FIELD_DESTINATION_ACCOUNT = "destinationAccount";

    private static final String ACCOUNT_ID = "accountId";

    JsonUtil jsonUtil;
    Integer adjustmentId;

    private final DateTimeUtil dateTimeUtil;
    private final String dateFormatter = "yyyy-MM-dd";
    private final String number = "31575";

    public OrbiKafkaInterfaceService() throws IOException {
        dateTimeUtil = new DateTimeUtil();
        propertiesUtil = new PropertiesUtil();
        jsonUtil = new JsonUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.kafka.interface.baseURI"));
    }

    public Response getKafkaConsumer(String groupId, String topic) {
        Map<String, Object> params = new HashMap<>();
        params.put(FIELD_GROUP_ID, groupId);
        params.put(FIELD_TOPIC, topic);

        return doGetRequestWithListParams(params, PATH_KAFKA);
    }

    public Response postKafkaConsumer(String payload, String eventType, String topic) {
        return doPostRequestMapWithQueryParams(payload, createQueryParams(eventType, topic), PATH_KAFKA);
    }

    public Map<String, String> createQueryParams(String eventType, String topic) {
        Map<String, String> params = new HashMap<>();
        params.put(FIELD_EVENT_TYPE, eventType);
        params.put(FIELD_TOPIC, topic);

        return params;
    }

    public String createPayloadTransaction(String transactionId, double amount,
                                           String transactionDate, String transactionType) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_TRANSACTION_EVENT);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_TRANSACTION_ID, transactionId)
                .replace(STRING_TRANSACTION_TYPE, transactionType)
                .replace(STRING_TRANSACTION_DATE, transactionDate)
                .replace(DOUBLE_AMOUNT, String.valueOf(amount));

        return result;
    }

    public Response createTransactionEvent(String eventType, String transactionType, String topic) throws IOException {
        String payload = createPayloadTransaction(MathUtil.getRandomUUID(), AMOUNT,
                dateTimeUtil.getDateFormattedToValidationDateField(), transactionType);

        return postKafkaConsumer(payload, eventType, topic);
    }

    public String createPayloadPixTransaction(String transactionId, String transactionType, String accountId,
                                              String transactionDate, double amount, boolean transactionCompleted,
                                              String transactionStatus)
            throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_PIX_TRANSACTION_EVENT);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_TRANSACTION_ID, transactionId)
                .replace(STRING_TRANSACTION_TYPE, transactionType)
                .replace(STRING_ACCOUNT_ID, accountId)
                .replace(STRING_TRANSACTION_DATE, transactionDate)
                .replace(DOUBLE_AMOUNT, String.valueOf(amount))
                .replace(BOOLEAN_TRANSACTION_COMPLETED, String.valueOf(transactionCompleted))
                .replace(STRING_TRANSACTION_STATUS, transactionStatus);

        return result;
    }

    public Response createPixTransactionEvent(String topic, String eventType, String transactionType, String accountId,
                                              String transactionDate, double amount, boolean transactionCompleted,
                                              String transactionStatus)
            throws IOException {
        String payload = createPayloadPixTransaction(MathUtil.getRandomUUID(), transactionType, accountId,
                transactionDate, amount, transactionCompleted, transactionStatus);

        return postKafkaConsumer(payload, eventType, topic);
    }

    public String createPayloadDictTransaction(String transacationIdDictTransaction, String transactionType,
                                               String transactionDate, String entryType) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_TRANSACTION_DICT_EVENT);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_TRANSACTION_ID, transacationIdDictTransaction)
                .replace(STRING_TRANSACTION_EXTERNAL, number)
                .replace(STRING_TRANSACTION_TYPE, transactionType)
                .replace(STRING_TRANSACTION_DATE, transactionDate)
                .replace(STRING_ENTRY_TYPE, entryType);

        return result;
    }

    public Response createDictTransactionEvent(String eventType, String transactionType,
                                               String entryType, String topic) throws IOException {
        String payload = createPayloadDictTransaction(MathUtil.getRandomUUID(), transactionType,
                dateTimeUtil.getDateFormattedToValidationDateField(), entryType);

        return postKafkaConsumer(payload, eventType, topic);
    }

    public String createPayloadDictInternalSearch(String referenceDate, String internalKeySearchesCount)
            throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_DICT_INTERNAL_SEARCH);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_REFERENCE_DATE, referenceDate)
                .replace(STRING_INTERNAL_KEY_SEARCHES_COUNT, internalKeySearchesCount);

        return result;
    }

    public Response createDictInternalSearchEvent(String eventType, String topic) throws IOException {
        String payload = createPayloadDictInternalSearch(
                dateTimeUtil.getCurrentDate(dateFormatter), "17");

        return postKafkaConsumer(payload, eventType, topic);
    }

    public Response createDictTransEvtransactionTypeNull(String tipoEvento, String topico) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_DICT_TRANSACTIONTYPE_NULL);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_TRANSACTION_ID, MathUtil.getRandomUUID())
                .replace(STRING_TRANSACTION_EXTERNAL, number)
                .replace(STRING_TRANSACTION_DATE, dateTimeUtil.getDateFormattedToValidationDateField())
                .replace(STRING_ENTRY_TYPE, "CPF");

        return postKafkaConsumer(result, tipoEvento, topico);
    }

    public Response createAvailabilityIncidentEvent(String payload, String topic) {
        return postKafkaConsumer(payload, "INCIDENT_REGISTERED", topic);
    }

    public String createPayloadAvailabilityIncident(String startTime, String endTime) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_ONE_AVAILABILITY_INCIDENT);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_REFERENCE_DATE, dateTimeUtil.getCurrentDate(dateFormatter))
                .replace("stringStartTime", startTime)
                .replace("stringEndTime", endTime);

        return result;
    }

    public String createPayloadAvailabilityTwoIncident(String startTime, String endTime, String startTime02,
                                                       String endTime02) throws IOException {
        File textFile = new File(ProjectSettings.JSON_DATA_PATH + PAYLOAD_MULTIPLE_AVAILABILITY_INCIDENT);
        String data = FileUtils.readFileToString(textFile, StandardCharsets.UTF_8);

        String result = data.replace(STRING_REFERENCE_DATE, dateTimeUtil.addOrSubtractDaysInADate(DAYS, dateFormatter))
                .replace("stringStartTime01", startTime)
                .replace("stringEndTime01", endTime)
                .replace("stringStartTime02", startTime02)
                .replace("stringEndTime02", endTime02);

        return result;
    }

    public void validateKeyPixMessageKafka(String personId, String uuidAccount,
                                           String keyValue, String keyType, String eventType) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypekafka = "";
        String accountIdKafka = "";
        String personIdKafka = "";
        String keyValueKafka = "";
        String keyTypeKafka = "";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && uuidAccount.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(ACCOUNT_ID))
                    && personId.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_PERSON_ID))
                    && keyValue.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_KEY_VALUE))
                    && keyType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_KEY_TYPE))) {
                eventTypekafka = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                accountIdKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(ACCOUNT_ID);
                personIdKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_PERSON_ID);
                keyValueKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_KEY_VALUE);
                keyTypeKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_KEY_TYPE);
                break;
            }
        }

        Assert.assertEquals(eventType, eventTypekafka);
        Assert.assertEquals(uuidAccount, accountIdKafka);
        Assert.assertEquals(personId, personIdKafka);
        Assert.assertEquals(keyValue, keyValueKafka);
        Assert.assertEquals(keyType, keyTypeKafka);
    }

    public void validateApixPixKeyManagement(String eventType, String transactionType, String entryType) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypeTopic = "";
        String eventTypeTwoTopic = "";
        String transactionTypeTopic = "";
        String entryTypeTopic = "";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (FIELD_APPLICATION_PIX_ENTRIES.equals(jsonObject.getJSONObject(FIELD_META_DATA)
                          .getString(FIELD_APPLICATION))
                    && eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && transactionType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_TYPE))
                    && entryType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_ENTRY_TYPE))
                    && eventType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_EVENT_TYPE))) {
                eventTypeTopic = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                transactionTypeTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_TYPE);
                entryTypeTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_ENTRY_TYPE);
                eventTypeTwoTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_EVENT_TYPE);
            }
        }

        Assert.assertEquals(eventType, eventTypeTopic);
        Assert.assertEquals(transactionType, transactionTypeTopic);
        Assert.assertEquals(eventType, eventTypeTwoTopic);
        Assert.assertEquals(entryType, entryTypeTopic);
    }

    public void validateSearchApixPixKeyManagement(String eventType, String transactionType, String entryType) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypeTopic = "";
        String eventTypeTwoTopic = "";
        String transactionTypeTopic = "";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (transactionType.equals(SEARCH)
                    && FIELD_APPLICATION_PIX_ENTRIES.equals(jsonObject.getJSONObject(FIELD_META_DATA)
                            .getString(FIELD_APPLICATION))
                    && eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && transactionType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_TYPE))
                    && eventType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_EVENT_TYPE))) {
                eventTypeTopic = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                transactionTypeTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_TYPE);
                eventTypeTwoTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_EVENT_TYPE);
            }
        }

        Assert.assertEquals(eventType, eventTypeTopic);
        Assert.assertEquals(transactionType, transactionTypeTopic);
        Assert.assertEquals(eventType, eventTypeTwoTopic);
    }

    public void validateApixTransaction(String eventType, String transactionType) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypeTopic = "";
        String transactionTypeTopic = "";
        String fieldApplicationTransaction = "orbi-pix-transaction-manager";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (fieldApplicationTransaction.equals(jsonObject.getJSONObject(FIELD_META_DATA)
                            .getString(FIELD_APPLICATION))
                    && eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && transactionType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD)
                           .getString(FIELD_TRANSACTION_TYPE))) {
                eventTypeTopic = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                transactionTypeTopic = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_TYPE);
            }

            Assert.assertNotNull(jsonObject.getJSONObject(FIELD_PAYLOAD).get(FIELD_TRANSACTION_DATE));
        }

        Assert.assertEquals(eventType, eventTypeTopic);
        Assert.assertEquals(transactionType, transactionTypeTopic);
    }

    public Response createTransactionPix(String eventType, String groupId, String topic)
            throws IOException {
        String payload = createTransactionPayload(
                dateTimeUtil.getDateFormattedToValidationDateField(), generateAdjustmentId(), eventType);
        Map<String, String> params = new HashMap<>();
        params.put(FIELD_EVENT_TYPE, eventType);
        params.put(FIELD_TOPIC, topic);
        params.put(FIELD_GROUP_ID, groupId);

        return doPostRequestMapWithQueryParams(payload, params, PATH_KAFKA);
    }

    private String createTransactionPayload(String dateTimeUtilLocal, Integer adjustmentIdOne, String eventType)
            throws IOException {
        String payloadString = jsonUtil.generateStringFromResource(
                ProjectSettings.JSON_DATA_PATH + JSON_FILE_NAME);

        payloadString = updatePayload("paymentDate", dateTimeUtilLocal, payloadString);
        payloadString = updatePayload("adjustmentId", adjustmentIdOne.toString(), payloadString);
        payloadString = updatePayload(FIELD_EVENT_TYPE, eventType, payloadString);

        return payloadString;
    }

    public Integer generateAdjustmentId() {
        adjustmentId = MathUtil.getRandomNumber(MathUtil.ONE_HUNDRED_THOUSAND);

        return adjustmentId;
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

    public void validateSchedulerMessageKafka(String eventType, String document, String accountNumber,
                                           String keyValue, String keyType, String transactionId) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypekafka = "";
        String accountNumberKafka = "";
        String documentKafka = "";
        String keyValueKafka = "";
        String keyTypeKafka = "";
        String transactionIdKafka = "";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && transactionId.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_ID))
                    && document.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                            .getString(FIELD_DOCUMENT))
                    && accountNumber.equals(jsonObject.getJSONObject(FIELD_PAYLOAD)
                            .getJSONObject(FIELD_DESTINATION_DATA).getString(FIELD_ACCOUNT_NUMBER))
                    && keyValue.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                            .getString(FIELD_KEY_VALUE))
                    && keyType.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                            .getString(FIELD_KEY_TYPE))) {
                eventTypekafka = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                transactionIdKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_ID);
                documentKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                        .get(FIELD_DOCUMENT).toString();
                accountNumberKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                        .getString(FIELD_ACCOUNT_NUMBER);
                keyValueKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                        .getString(FIELD_KEY_VALUE);
                keyTypeKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_DATA)
                        .getString(FIELD_KEY_TYPE);
            }
        }
        Assert.assertEquals(eventType, eventTypekafka);
        Assert.assertEquals(transactionId, transactionIdKafka);
        Assert.assertEquals(document, documentKafka);
        Assert.assertEquals(accountNumber, accountNumberKafka);
        Assert.assertEquals(keyValue, keyValueKafka);
        Assert.assertEquals(keyType, keyTypeKafka);
    }

    public void validateSchedulerNotificationMessageKafka(String eventType, String document, String transactionId) {
        List<String> responses = RequestManager.shared().getResponse().body().jsonPath().get();

        String eventTypekafka = "";
        String documentKafka = "";
        String transactionIdKafka = "";

        for (var response : responses) {
            JSONObject jsonObject = new JSONObject(response);

            if (eventType.equals(jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE))
                    && transactionId.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_ID))
                    && document.equals(jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_ACCOUNT)
                               .getString(FIELD_ENTITY_DOCUMENT))) {
                eventTypekafka = jsonObject.getJSONObject(FIELD_META_DATA).getString(FIELD_EVENT_TYPE);
                transactionIdKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getString(FIELD_TRANSACTION_ID);
                documentKafka = jsonObject.getJSONObject(FIELD_PAYLOAD).getJSONObject(FIELD_DESTINATION_ACCOUNT)
                        .get(FIELD_ENTITY_DOCUMENT).toString();
            }
        }
        Assert.assertEquals(eventType, eventTypekafka);
        Assert.assertEquals(transactionId, transactionIdKafka);
        Assert.assertEquals(document, documentKafka);
    }

    public void validarTermoKafka(String bodyCompleto, String cpf, String eventType) {
        String eventTypeExtraido = "";
        List<String> bodyExtraido = Arrays.asList(bodyCompleto.split("\",\""));
        for (String string : bodyExtraido) {
            String cpfExtraido = StringUtils.substringBetween(string, "\\\"cpf\\\":\\\"", "\\\",\\\"").trim();
            if (cpfExtraido.equalsIgnoreCase(cpf)) {
                eventTypeExtraido = StringUtils.substringBetween(string, "eventType\\\":\\\"", "\\\",\\\"").trim();
                break;
            }
        }
        Assert.assertTrue(eventTypeExtraido.equalsIgnoreCase(eventType));
    }

    public Response insertProposalAnalysis(String status, String eventType, String topic) {
        JSONObject payload = new JSONObject();
        payload.put("proposalNumber", RandomStringUtils.randomNumeric(9));
        payload.put("proposalStatus", status);
        payload.put("source", "APPORBI");
        payload.put("cpf", ConductorService.personUtil.getCpf());

        return postKafkaConsumer(payload.toString(), eventType, topic);
    }
}