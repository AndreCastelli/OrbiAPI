package service.datasource;

import base.ProjectSettings;
import base.RequestManager;
import base.util.FileUtil;
import base.util.PersonUtil;
import base.util.PropertiesUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import service.AuthenticationService;
import service.BaseService;
import service.ConductorConectorService;
import service.DockConnectorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConductorService extends BaseService {
    public static final String JSON_FILENAME = "//usersMyCardWithoutDigitalAccount.json";
    public static final String CPF = "cpf";
    public static final String STATUS = "status";
    public static final String AVAILABLE = "available";
    public static final String UNAVAILABLE = "unavailable";

    public static PersonUtil personUtil;
    long totalPages;
    long maxUserAmount;
    FileUtil fileUtil;

    public ConductorService() throws IOException {
        propertiesUtil = new PropertiesUtil();
        fileUtil = new FileUtil();
        personUtil = new PersonUtil();
        getAuthenticateToTheConductorAPI();
    }

    public PersonUtil getPerson() {
        return personUtil;
    }

    public void getAuthenticateToTheConductorAPI() {
        RestAssured.baseURI = propertiesUtil.getPropertyByName("authentication.conductor.url");

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.addHeader(
                "access_token", propertiesUtil.getPropertyByNameBase64("authentication.conductor.accessToken"));
        reqBuilder.addHeader(
                "client_id", propertiesUtil.getPropertyByNameBase64("authentication.conductor.clientId"));
        RequestManager.shared().setRequest(reqBuilder.build());
    }

    public Response createPerson() {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("nome", personUtil.getName());
        queryParams.put("tipo", "PF");
        queryParams.put("dataNascimento", personUtil.getBirthDate());
        queryParams.put("cpf", personUtil.getCpf());
        queryParams.put("numeroIdentidade", personUtil.getRg());
        queryParams.put("orgaoExpedidorIdentidade", "SSP");
        queryParams.put("sexo", personUtil.getGender());

        return doPostRequestQuery(queryParams, "/pessoas");
    }

    public Response createPersonDetail(int idPerson) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("idPessoa", idPerson);
        queryParams.put("nomeMae", personUtil.getMother());
        queryParams.put("email", personUtil.getEmail());
        queryParams.put("idNaturezaOcupacao", personUtil.getIdOccupationConductor());
        queryParams.put("patrimonioTotal", personUtil.getRangePropertyValue());

        return doPostRequestQuery(queryParams, "/pessoas-detalhes");
    }

    public Response createPersonAddress(int idPerson) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("idPessoa", idPerson);
        queryParams.put("idTipoEndereco", 2);
        queryParams.put("cep", personUtil.getAddress().get("zipCode"));
        queryParams.put("logradouro", personUtil.getAddress().get("street"));
        queryParams.put("numero", personUtil.getAddress().get("number"));
        queryParams.put("complemento", personUtil.getAddress().get("complement"));
        queryParams.put("bairro", personUtil.getAddress().get("neighborhood"));
        queryParams.put("cidade", personUtil.getAddress().get("city"));
        queryParams.put("uf", personUtil.getAddress().get("state"));
        queryParams.put("pais", "BR");
        queryParams.put("flagCorrespondencia", true);

        return doPostRequestQuery(queryParams, "/enderecos");
    }

    public Response createPersonPhone(int idPerson, int idTypePhone) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("idTipoTelefone", idTypePhone);
        queryParams.put("idPessoa", idPerson);
        queryParams.put("ddd", "053");
        queryParams.put("telefone", personUtil.getCellPhone());

        return doPostRequestQuery(queryParams, "/telefones");
    }

    public Response createAccountByProductId(int productId, int phoneType) {
        int idPerson = createPerson().then().statusCode(HttpStatus.SC_OK).extract().path("id");
        createPersonDetail(idPerson).then().statusCode(HttpStatus.SC_OK);
        int personAddressId = createPersonAddress(idPerson).then().statusCode(HttpStatus.SC_OK).extract().path("id");
        createPersonPhone(idPerson, phoneType).then().statusCode(HttpStatus.SC_OK);

        JSONObject requestBody = new JSONObject();
        requestBody.put("idPessoa", idPerson);
        requestBody.put("idOrigemComercial", 5);
        requestBody.put("idProduto", productId);
        requestBody.put("diaVencimento", 15);
        requestBody.put("valorRenda", 3400);
        requestBody.put("canalEntrada", "hml");
        requestBody.put("valorPontuacao", 0);
        requestBody.put("idEnderecoCorrespondencia", personAddressId);
        requestBody.put("limiteGlobal", 1000);
        requestBody.put("limiteMaximo", 1000);
        requestBody.put("limiteParcelas", 500);
        requestBody.put("limiteConsignado", 0);
        requestBody.put("flagFaturaPorEmail", 1);
        requestBody.put("idPromotorVenda", 0);
        requestBody.put("idStatusConta", 0);

        return doPostRequestJson(requestBody.toString(), "/contas");
    }

    public Response createAccountByProductId(int productId) {
        return createAccountByProductId(productId, 18);
    }

    public Response createAccountByPhoneType(int phoneType) {
        return createAccountByProductId(4, phoneType);
    }

    public Response getAccountInfo(String accountID) {
        return doGetRequestOneAttribute("/contas/{idAccount}", accountID);
    }

    public Response getAccountInfoBy(String idPerson) {
        return doGetRequestOneAttribute("/contas/?idPessoa={cpf}", idPerson);
    }

    public String generateJsonWithUsers(List<String> userListMyCard) {
        JSONArray payloadArray = new JSONArray();

        userListMyCard.forEach(item -> {
            JSONObject payload = new JSONObject();
            payload.put(CPF, item);
            payload.put(STATUS, AVAILABLE);
            payloadArray.put(payload);
        });

        return payloadArray.toString();
    }

    public void generateJsonFile(String payload) throws IOException {
        String archive = ProjectSettings.FILE_TDM_PATH + JSON_FILENAME;
        fileUtil.createEmptyFile(archive);
        fileUtil.writeToFile(archive, payload);
    }

    public String searchForUsersToCreateDigitalAccount() throws IOException {
        long qtdUser = Integer.parseInt(propertiesUtil.getPropertyByName("mc.create.account.user.amount"));
        List<String> userList = new ArrayList<>();
        setFetchInterval();

        for (long i = totalPages - 1; i >= 1; i--) {
            getAuthenticateToTheConductorAPI();
            JSONArray userArray = fetchUsersMyCardFromASpecificPage(i).getJSONArray("content");

            for (int j = 0; j < userArray.length(); j++) {
                JSONObject cpfObj = userArray.getJSONObject(j);
                String cpf = cpfObj.getString(CPF);

                if (verifyUserAtApiPersons(cpf)
                        && checkIfUserDoesntHaveADockAccount(cpf)) {
                    // userList.add(cpf);

                    return cpf;
                }
            }
        }

        return "";
    }

    public void setFetchInterval() {
        getAuthenticateToTheConductorAPI();
        totalPages = fetchUsersMyCardFromASpecificPage(0).getLong("totalPages");
        maxUserAmount = totalPages - Long.parseLong(propertiesUtil.getPropertyByName("mc.create.account.user.amount"));
    }

    public JSONObject fetchUsersMyCardFromASpecificPage(long pageNumber) {
        Response response = doGetRequestWthPagination("/pessoas", pageNumber);
        response.then();

        return new JSONObject(response.getBody().asString());
    }

    public boolean verifyUserAtApiPersons(String cpf) throws IOException {
        RequestManager.shared().setRequest(new AuthenticationService().getAccessTokenBackEnd());

        return new ConductorConectorService().getPersonDetailsByCPF(cpf).statusCode() == HttpStatus.SC_OK;
    }

    public boolean checkIfUserDoesntHaveADockAccount(String cpf) throws IOException {
        RequestManager.shared().setRequest(new AuthenticationService().getAccessTokenBackEnd());

        return new DockConnectorService().getAccountData(cpf).getStatusCode() == HttpStatus.SC_NOT_FOUND;
    }

    public String getUserToCreateAccount() {
        String userAvailable = "";
        try {
            String jsonUsers = new FileUtil().readFile(ProjectSettings.FILE_TDM_PATH + JSON_FILENAME);
            JSONArray payloadArray = new JSONArray(jsonUsers);

            for (int i = 0; i < payloadArray.length(); i++) {
                JSONObject user = payloadArray.getJSONObject(i);

                if (user.getString(STATUS).equals(AVAILABLE)
                        && checkIfUserDoesntHaveADockAccount(user.getString(CPF))) {
                    userAvailable = user.getString(CPF);
                } else {
                    payloadArray.getJSONObject(i).put(STATUS, UNAVAILABLE);
                }
            }

            new FileUtil().writeToFile(
                    ProjectSettings.FILE_TDM_PATH + JSON_FILENAME, payloadArray.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(
                    "Erro para obter os dados de um usuário meu cartão sem Conta Digital! " + e.getMessage());
        }

        return userAvailable;
    }

    public Response createMyCard(String personId, String accountId, String flagId) {
        Map<String, Object> queryParams = new LinkedHashMap();
        queryParams.put("idBandeira", flagId);

        return doPostRequestQuery(queryParams, "/contas/" + accountId + "/pessoas/" + personId + "/gerar-cartao");
    }

    public Response createAccountFromScratch() {
        Response resp = createAccountByProductId(4);
        String personId = resp.getBody().jsonPath().getString("idPessoa");
        String accountId = resp.getBody().jsonPath().getString("id");

        //createMyCard(personId, accountId, "2").then().statusCode(HttpStatus.SC_OK);

        return getAccountInfoBy(personId);
    }
}
