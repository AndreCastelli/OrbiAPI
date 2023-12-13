package service;

import base.ProjectSettings;
import base.RequestManager;
import base.db.OrbiApixServiceDb;
import base.util.*;
import com.microsoft.azure.storage.StorageException;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class OrbiApixBuilderService extends BaseService {

    private static final String SWAGGER_APIX001 = "/v1/apix/process/apix001";
    private static final String FILE = "apix001.xml";
    private static final String INDICEDISPONIBILIDADE = "IndiceDisponibilidade";
    private static final String QTDCONSULTAS = "QtdConsultas";

    MathUtil mathUtil;
    AzureDevOpsUtil azureDevOpsUtil;
    XMLCreateUtil xmlCreateUtil;

    public OrbiApixBuilderService()
            throws IOException, InvalidKeyException, StorageException, URISyntaxException {
        propertiesUtil = new PropertiesUtil();
        azureDevOpsUtil = new AzureDevOpsUtil();
        xmlCreateUtil = new XMLCreateUtil();
        mathUtil = new MathUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("orbi.apix.builder.baseURI"));
    }

    public Response postDataTime(String month, String year) {
        return doPostRequestJson(
                createApixPayload(month, year), SWAGGER_APIX001);
    }

    public Response postProcessApix() {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        return doPostRequestJson(
                createApixPayload(
                        dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"),
                        dateTimeUtil.getCurrentDate("yyyy")
                ), SWAGGER_APIX001);
    }

    public String createApixPayload(String month, String year) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("month", month);
        requestBody.put("year", year);

        return requestBody.toString();
    }

    public void downloadTheTransactionFile(String folderName)
            throws IOException, StorageException, URISyntaxException {
        String fileName = azureDevOpsUtil.getFileName("apix/output");

        long fileSize = azureDevOpsUtil.getFileSize(folderName, fileName, ProjectSettings.FILE_TEMP_PATH
                + "apix001.xml");
        Assert.assertTrue("O arquivo na pasta output esta vazio", fileSize > 0);
    }

    public boolean standardFirstLineLayout() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                .equals(xmlCreateUtil.getXmlHeader(ProjectSettings.FILE_TEMP_PATH + FILE));
    }

    public String secondLineDefaultLayout(Response parameterResp) {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        int currentMonth = Integer.parseInt(dateTimeUtil.getCurrentDate("MM"));

        return "<APIX001 DtArquivo=\"" + dateTimeUtil.getTheLastDayOfTheMonth(
                currentMonth - MathUtil.ONE, "yyyy-MM-dd")
                + "\" Ano=\"" + dateTimeUtil.getCurrentDate("yyyy")
                + "\" Mes=\"" + dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")
                + "\" ISPB=\"" + parameterResp.then().extract().path("ispbCode").toString()
                + "\" NomeResp=\"" + parameterResp.then().extract().path("accountablePersonName").toString()
                + "\" EmailResp=\"" + parameterResp.then().extract().path("accountablePersonEmail").toString()
                + "\" TelResp=\"" + parameterResp.then().extract().path("accountablePersonPhone").toString()
                + "\" TipoEnvio=\"I\">";
    }

    public boolean compareLayoutOfTheSecondLineOfTheHeader(Response parameterResp) {
        return secondLineDefaultLayout(parameterResp).equals(
                xmlCreateUtil.getTheRootNodeAttributes(ProjectSettings.FILE_TEMP_PATH + FILE));
    }

    public boolean validateTransactionGroup01() throws IOException {
        JSONObject layoutDb;
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        layoutDb = (JSONObject) new OrbiApixServiceDb().getInformationOnTransactionType01();
        fileXML = getTransactionTypeElement("1", "1");

        qtdTransacoes = layoutDb.get("qtdTransacoes").toString().equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = layoutDb.get("valorTransacoes").toString().equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0.0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup02() throws IOException {
        JSONObject layoutDb;
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        layoutDb = (JSONObject) new OrbiApixServiceDb().getInformationOnTransactionType02();
        fileXML = getTransactionTypeElement("2", "1");

        qtdTransacoes = layoutDb.get("qtdTransacoes").toString().equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = layoutDb.get("valorTransacoes").toString().equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0.0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup03() throws IOException {
        JSONObject layoutDb;
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        layoutDb = (JSONObject) new OrbiApixServiceDb().getInformationOnTransactionType03();
        fileXML = getTransactionTypeElement("3", "1");

        qtdTransacoes = layoutDb.get("qtdTransacoes").toString().equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = layoutDb.get("valorTransacoes").toString().equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0.0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup04() throws IOException {
        JSONObject layoutDb;
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        layoutDb = (JSONObject) new OrbiApixServiceDb().getInformationOnTransactionType04();
        fileXML = getTransactionTypeElement("4", "1");

        qtdTransacoes = layoutDb.get("qtdTransacoes").toString().equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = layoutDb.get("valorTransacoes").toString().equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0.0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup05() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("5", "1");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0.0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0.0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup06() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("1", "2");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup07() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("2", "2");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup08() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("3", "2");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup09() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("4", "2");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup10() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("5", "2");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup11() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("1", "3");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup12() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("2", "3");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup13() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("3", "3");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup14() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("4", "3");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public boolean validateTransactionGroup15() throws IOException {
        JSONObject fileXML;

        boolean qtdTransacoes = false;
        boolean valorTransacoes = false;
        boolean valorEspecie = false;

        fileXML = getTransactionTypeElement("5", "3");

        qtdTransacoes = "0".equals(fileXML.get("QtdTransacoes"));
        valorTransacoes = "0".equals(fileXML.get("ValorTransacoes"));
        valorEspecie = "0".equals(fileXML.get("ValorEspecie"));

        return qtdTransacoes && valorTransacoes && valorEspecie;
    }

    public JSONObject getTransactionTypeElement(String transactionDetailing, String transactionFinality) {
        NodeList nList = new XMLCreateUtil().returnsChildrenOfTheNode(
                ProjectSettings.FILE_TEMP_PATH + FILE, "Transacao");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) nNode).getElementsByTagName("DetalhamentoTransacoes")
                    .item(0).getTextContent().equals(transactionDetailing)
                    && ((Element) nNode).getElementsByTagName("FinalidadeTransacoes")
                    .item(0).getTextContent().equals(transactionFinality)) {
                Element eElement = (Element) nNode;
                JSONObject transJson = new JSONObject();

                transJson.put("QtdTransacoes",
                        eElement.getElementsByTagName("QtdTransacoes").item(0).getTextContent());
                transJson.put("ValorTransacoes",
                        eElement.getElementsByTagName("ValorTransacoes").item(0).getTextContent());
                transJson.put("ValorEspecie",
                        eElement.getElementsByTagName("ValorEspecie").item(0).getTextContent());
                transJson.put("DetalhamentoTransacoes",
                        eElement.getElementsByTagName("DetalhamentoTransacoes").item(0).getTextContent());
                transJson.put("FinalidadeTransacoes",
                        eElement.getElementsByTagName("FinalidadeTransacoes").item(0).getTextContent());

                return transJson;
            }
        }

        return null;
    }

    public boolean validateTheDevolutionInformation() throws IOException {
        JSONObject layoutDb;
        JSONObject fileXML;

        boolean qtdDevolucoes = false;
        boolean valorDevolucoes = false;

        layoutDb = (JSONObject) new OrbiApixServiceDb().getInformationOnDevolution();
        fileXML = getDevolution();

        qtdDevolucoes = layoutDb.get("valorDevolucoes").toString().equals(fileXML.get("ValorDevolucoes"));
        valorDevolucoes = layoutDb.get("qtdDevolucoes").toString().equals(fileXML.get("QtdDevolucoes"));

        return qtdDevolucoes && valorDevolucoes;
    }

    public JSONObject getDevolution() {
        String fileDevolution = ProjectSettings.FILE_TEMP_PATH + FILE;
        JSONObject devolJson = new JSONObject();

        devolJson.put("ValorDevolucoes", xmlCreateUtil.getTheValueOfAnAttribute(
                fileDevolution, "Devolucoes", "ValorDevolucoes"));
        devolJson.put("QtdDevolucoes", xmlCreateUtil.getTheValueOfAnAttribute(
                fileDevolution, "Devolucoes", "QtdDevolucoes"));

        return devolJson;
    }

    public boolean validateTheEarnedRevenue(int revenueType) {
        JSONObject fileXML;

        boolean valorReceita = false;

        switch (revenueType) {
            case 1:
                fileXML = getRevenueTypeElement("1");

                valorReceita = "0.0".equals(fileXML.get("ValorReceita"));

                return valorReceita;
            case 2:
                fileXML = getRevenueTypeElement("2");

                valorReceita = "0.0".equals(fileXML.get("ValorReceita"));

                return valorReceita;
            case 3:
                fileXML = getRevenueTypeElement("3");

                valorReceita = "0.0".equals(fileXML.get("ValorReceita"));

                return valorReceita;
            default:
                throw new IllegalArgumentException("Tipo de receita Invalida!!: " + revenueType);
        }
    }

    private JSONObject getRevenueTypeElement(String revenueDetailing) {
        NodeList nList = new XMLCreateUtil().returnsChildrenOfTheNode(
                ProjectSettings.FILE_TEMP_PATH + FILE, "Receita");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) nNode).getElementsByTagName("FonteReceita")
                    .item(0).getTextContent().equals(revenueDetailing)) {
                Element eElement = (Element) nNode;
                JSONObject revenueJson = new JSONObject();

                revenueJson.put("ValorReceita",
                        eElement.getElementsByTagName("ValorReceita").item(0).getTextContent());
                revenueJson.put("FonteReceita",
                        eElement.getElementsByTagName("FonteReceita").item(0).getTextContent());

                return revenueJson;
            }
        }

        return null;
    }

    public boolean validateTheTransactionTimeInformation() throws IOException {
        JSONArray layoutDb;
        JSONObject fileXML;
        JSONObject internal;
        JSONObject external;

        boolean perc50TempoExpUsuarioLiqSPI = false;
        boolean perc50TempoExpUsuarioLiqForaSPI = false;
        boolean perc99TempoExpUsuarioLiqSPI = false;
        boolean perc99TempoExpUsuarioLiqForaSPI = false;
        boolean tempoMaxBloqueioCautelar = false;

        layoutDb = (JSONArray) new OrbiApixServiceDb().getInformationOnTransactionTime();
        external = new JSONObject(layoutDb.get(0).toString());
        internal = new JSONObject(layoutDb.get(1).toString());

        String valueExternalPercent50 = String.valueOf(
                new MathUtil().truncDouble(Double.parseDouble(external.get("percentile50").toString()), 2));
        String valueInternalPercent50 = String.valueOf(
                new MathUtil().truncDouble(Double.parseDouble(internal.get("percentile50").toString()), 2));
        String valueExternalPercent99 = String.valueOf(
                new MathUtil().truncDouble(Double.parseDouble(external.get("percentile99").toString()), 2));
        String valueInternalPercent99 = String.valueOf(
                new MathUtil().truncDouble(Double.parseDouble(internal.get("percentile99").toString()), 2));

        fileXML = getTransactionTime();

        perc50TempoExpUsuarioLiqSPI = valueExternalPercent50.equals(fileXML.get("Perc50TempoExpUsuarioLiqSPI"));
        perc50TempoExpUsuarioLiqForaSPI = valueInternalPercent50.equals(fileXML.get("Perc50TempoExpUsuarioLiqForaSPI"));
        perc99TempoExpUsuarioLiqSPI = valueExternalPercent99.equals(fileXML.get("Perc99TempoExpUsuarioLiqSPI"));
        perc99TempoExpUsuarioLiqForaSPI = valueInternalPercent99.equals(fileXML.get("Perc99TempoExpUsuarioLiqForaSPI"));
        tempoMaxBloqueioCautelar = "0.0".equals(fileXML.get("TempoMaxBloqueioCautelar"));

        return perc50TempoExpUsuarioLiqSPI && perc50TempoExpUsuarioLiqForaSPI
                && perc99TempoExpUsuarioLiqSPI && perc99TempoExpUsuarioLiqForaSPI
                && tempoMaxBloqueioCautelar;
    }

    public JSONObject getTransactionTime() {
        String file = ProjectSettings.FILE_TEMP_PATH + FILE;
        JSONObject tranTimeJson = new JSONObject();

        tranTimeJson.put("Perc50TempoExpUsuarioLiqSPI", xmlCreateUtil.getTheValueOfAnAttribute(
                file, "TemposTransacoes", "Perc50TempoExpUsuarioLiqSPI"));
        tranTimeJson.put("Perc50TempoExpUsuarioLiqForaSPI", xmlCreateUtil.getTheValueOfAnAttribute(
                file, "TemposTransacoes", "Perc50TempoExpUsuarioLiqForaSPI"));
        tranTimeJson.put("Perc99TempoExpUsuarioLiqSPI", xmlCreateUtil.getTheValueOfAnAttribute(
                file, "TemposTransacoes", "Perc99TempoExpUsuarioLiqSPI"));
        tranTimeJson.put("Perc99TempoExpUsuarioLiqForaSPI", xmlCreateUtil.getTheValueOfAnAttribute(
                file, "TemposTransacoes", "Perc99TempoExpUsuarioLiqForaSPI"));
        tranTimeJson.put("TempoMaxBloqueioCautelar", xmlCreateUtil.getTheValueOfAnAttribute(
                file, "TemposTransacoes", "TempoMaxBloqueioCautelar"));

        return tranTimeJson;
    }

    public boolean validateTimeDict() throws IOException {
        String layoutDbUserConsult;
        String layoutDbSendRegister;
        String layoutDbUserRegister;
        String layoutDbUserExclusion;
        String layoutDbPortabilityNotification;
        String layoutDbPortabilitySend;
        JSONObject fileXML;

        boolean perc99TempoUsuarioConsulta = false;
        boolean percTempoEnvioRegistro = false;
        boolean percTempoExpUsuarioRegistro = false;
        boolean percTempoExpUsuarioExclusao = false;
        boolean percTempoNotificacaoPortabilidade = false;
        boolean percTempoEnvioPortabilidade = false;

        layoutDbUserConsult = new OrbiApixServiceDb().getDictTimeUserConsult();
        layoutDbSendRegister = new OrbiApixServiceDb().getDictTimeSendRegister();
        layoutDbUserRegister = new OrbiApixServiceDb().getDictTimeUserRegister();
        layoutDbUserExclusion = new OrbiApixServiceDb().getDictTimeUserExclusion();
        layoutDbPortabilityNotification = new OrbiApixServiceDb().getDictTimePortabilityNotification();
        layoutDbPortabilitySend = new OrbiApixServiceDb().getDictTimePortabilitySend();
        fileXML = getTimeDict();

        perc99TempoUsuarioConsulta = layoutDbUserConsult.equals(fileXML.get("Perc99TempoUsuarioConsulta"));
        percTempoEnvioRegistro = layoutDbSendRegister.equals(fileXML.get("PercTempoEnvioRegistro"));
        percTempoExpUsuarioRegistro = layoutDbUserRegister.equals(fileXML.get("PercTempoExpUsuarioRegistro"));
        percTempoExpUsuarioExclusao = layoutDbUserExclusion.equals(fileXML.get("PercTempoExpUsuarioExclusao"));
        percTempoNotificacaoPortabilidade = layoutDbPortabilityNotification
                .equals(fileXML.get("PercTempoNotificacaoPortabilidade"));
        percTempoEnvioPortabilidade = layoutDbPortabilitySend.equals(fileXML.get("PercTempoEnvioPortabilidade"));

        return perc99TempoUsuarioConsulta && percTempoEnvioRegistro
                && percTempoExpUsuarioRegistro && percTempoExpUsuarioExclusao
                && percTempoNotificacaoPortabilidade && percTempoEnvioPortabilidade;
    }

    public JSONObject getTimeDict() {
        String timeDict = ProjectSettings.FILE_TEMP_PATH + FILE;
        JSONObject timeDictJson = new JSONObject();

        timeDictJson.put("Perc99TempoUsuarioConsulta", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "Perc99TempoUsuarioConsulta"));
        timeDictJson.put("PercTempoEnvioRegistro", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "PercTempoEnvioRegistro"));
        timeDictJson.put("PercTempoExpUsuarioRegistro", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "PercTempoExpUsuarioRegistro"));
        timeDictJson.put("PercTempoExpUsuarioExclusao", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "PercTempoExpUsuarioExclusao"));
        timeDictJson.put("PercTempoNotificacaoPortabilidade", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "PercTempoNotificacaoPortabilidade"));
        timeDictJson.put("PercTempoEnvioPortabilidade", xmlCreateUtil.getTheValueOfAnAttribute(
                timeDict, "TemposDict", "PercTempoEnvioPortabilidade"));

        return timeDictJson;
    }

    public boolean validateQueryDict() throws IOException {
        String layoutDb;
        JSONObject fileXML;

        boolean qtdConsultas = false;

        layoutDb = new OrbiApixServiceDb().getQueryDict();
        fileXML = getQueryDict();

        qtdConsultas = layoutDb.equals(fileXML.get("QtdConsultas"));

        return qtdConsultas;
    }

    public JSONObject getQueryDict() {
        String queryDict = ProjectSettings.FILE_TEMP_PATH + FILE;
        JSONObject dictJson = new JSONObject();

        dictJson.put("QtdConsultas", xmlCreateUtil.getTheValueOfAnAttribute(
                queryDict, "ConsultasDict", "QtdConsultas"));

        return dictJson;
    }

    public boolean validateAvailability() throws IOException {
        String layoutDb;
        JSONObject fileXML;

        boolean indiceDisponibilidade = false;

        layoutDb = new OrbiApixServiceDb().getIndexAvailability();
        fileXML = getAvailability();

        indiceDisponibilidade = layoutDb.equals(fileXML.get(INDICEDISPONIBILIDADE));

        return indiceDisponibilidade;
    }

    private JSONObject getAvailability() {
        String availability = ProjectSettings.FILE_TEMP_PATH + FILE;
        JSONObject availabJson = new JSONObject();

        availabJson.put(INDICEDISPONIBILIDADE, xmlCreateUtil.getTheValueOfAnAttribute(
                availability, "Disponibilidade", INDICEDISPONIBILIDADE));

        return availabJson;
    }

    public boolean validatePrecautionaryBlockGroup01() {
        JSONObject fileXML;

        boolean qtdeBloqCaut = false;
        boolean valorBloqCaut = false;

        fileXML = getTransactionBloqCaut("1");

        qtdeBloqCaut = "0".equals(fileXML.get("QtdeBloqCaut"));
        valorBloqCaut = "0".equals(fileXML.get("ValorBloqCaut"));

        return qtdeBloqCaut && valorBloqCaut;
    }

    public boolean validatePrecautionaryBlockGroup02() {
        JSONObject fileXML;

        boolean qtdeBloqCaut = false;
        boolean valorBloqCaut = false;

        fileXML = getTransactionBloqCaut("2");

        qtdeBloqCaut = "0".equals(fileXML.get("QtdeBloqCaut"));
        valorBloqCaut = "0".equals(fileXML.get("ValorBloqCaut"));

        return qtdeBloqCaut && valorBloqCaut;
    }

    public boolean validatePrecautionaryBlockGroup03() {
        JSONObject fileXML;

        boolean qtdeBloqCaut = false;
        boolean valorBloqCaut = false;

        fileXML = getTransactionBloqCaut("3");

        qtdeBloqCaut = "0".equals(fileXML.get("QtdeBloqCaut"));
        valorBloqCaut = "0".equals(fileXML.get("ValorBloqCaut"));

        return qtdeBloqCaut && valorBloqCaut;
    }

    public boolean validatePrecautionaryBlockGroup04() {
        JSONObject fileXML;

        boolean qtdeBloqCaut = false;
        boolean valorBloqCaut = false;

        fileXML = getTransactionBloqCaut("4");

        qtdeBloqCaut = "0".equals(fileXML.get("QtdeBloqCaut"));
        valorBloqCaut = "0".equals(fileXML.get("ValorBloqCaut"));

        return qtdeBloqCaut && valorBloqCaut;
    }

    public JSONObject getTransactionBloqCaut(String transactionBloqCaut) {
        NodeList nList = new XMLCreateUtil().returnsChildrenOfTheNode(
                ProjectSettings.FILE_TEMP_PATH + FILE, "BloqueioCautelar");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) nNode).getElementsByTagName("DetalhamentoTransacoesBloqCaut")
                    .item(0).getTextContent().equals(transactionBloqCaut)) {
                Element eElement = (Element) nNode;
                JSONObject transJson = new JSONObject();

                transJson.put("QtdeBloqCaut",
                        eElement.getElementsByTagName("QtdeBloqCaut").item(0).getTextContent());
                transJson.put("ValorBloqCaut",
                        eElement.getElementsByTagName("ValorBloqCaut").item(0).getTextContent());
                transJson.put("DetalhamentoTransacoesBloqCaut",
                        eElement.getElementsByTagName("DetalhamentoTransacoesBloqCaut").item(0).getTextContent());

                return transJson;
            }
        }

        return null;
    }
}
