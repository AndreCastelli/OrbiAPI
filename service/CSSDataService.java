package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.*;
import com.microsoft.azure.storage.StorageException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.Objects;

public class CSSDataService extends BaseService {
    public static final String TXT_FILENAME = "//ccs_text_file_generated.txt";
    private static final String FILE_PREFIX = "CCS_";
    private static final String EXTENSAO_TXT = ".txt";
    XMLCreateUtil xmlCreateUtil;
    DateTimeUtil dateTimeUtil;
    String xmlFileName;
    AzureDevOpsUtil azureDevOpsUtil;
    JsonUtil jsonUtil;
    DockConnectorService dockConnectorService;
    String dateFormater = "ddMMyyyy";
    Object time = new Object();

    public CSSDataService() throws IOException, InvalidKeyException, StorageException, URISyntaxException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("ccs.data.baseURI"));
        azureDevOpsUtil = new AzureDevOpsUtil();
        xmlCreateUtil = new XMLCreateUtil();
        dateTimeUtil = new DateTimeUtil();
        jsonUtil = new JsonUtil();
        dockConnectorService = new DockConnectorService();
    }

    public void downloadTxtFileGenerated(String folderName) throws IOException, StorageException, URISyntaxException {
        String fileName = azureDevOpsUtil.getFileName(
                folderName, FILE_PREFIX + dateTimeUtil
                        .addOrSubtractDaysInADate(Integer.parseInt("-" + 1), dateFormater) + EXTENSAO_TXT);
        long fileSize = 0;
        synchronized (time) {
            for (int i = 0; i < 100; i++) {
                fileSize = azureDevOpsUtil.getFileSize(
                        folderName, fileName, ProjectSettings.FILE_TEMP_PATH + "ccs_text_file_generated.txt");

                if (fileSize > 0) {
                    break;
                }

                try {
                    time.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertTrue("O arquivo na pasta output esta vazio", fileSize > 0);
    }

    public void downloadTxtFileGeneratedCurrentDate(String folderName)
            throws IOException, StorageException, URISyntaxException {
        String fileName = azureDevOpsUtil.getFileName(
                folderName, FILE_PREFIX + dateTimeUtil.getCurrentDate(dateFormater) + EXTENSAO_TXT);
        long fileSize = 0;

        synchronized (time) {
            for (int i = 0; i < 20; i++) {
                fileSize = azureDevOpsUtil.getFileSize(folderName, fileName, ProjectSettings.FILE_TEMP_PATH
                        + "ccs_text_file_generated.txt");

                if (fileSize > 0) {
                    break;
                }

                try {
                    time.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertTrue("O arquivo na pasta output esta vazio", fileSize > 0);
    }

    public String getRegisteredUsersFromFile() throws IOException {
        String strLine;
        JSONArray registeredJson = new JSONArray();

        FileInputStream fstream = new FileInputStream(ProjectSettings.FILE_TEMP_PATH + TXT_FILENAME);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            JSONObject dataJson = new JSONObject();

            int id = Integer.parseInt(strLine.substring(16, 21));
            int personId = Integer.parseInt(strLine.substring(203, 209).trim());
            String registerDate = strLine.substring(149, 157);
            String cancelDate = strLine.substring(158, 166);
            String cpf = strLine.substring(56, 67);
            String agency = strLine.substring(165, 169);
            String account = strLine.substring(169, 179);

            dataJson.put("id", id);
            dataJson.put("personId", personId);
            dataJson.put("registerDate", registerDate);
            dataJson.put("cancelDate", cancelDate);
            dataJson.put("cpf", cpf);
            dataJson.put("agency", agency);
            dataJson.put("account", account);

            registeredJson.put(dataJson);
        }

        fstream.close();

        return registeredJson.toString();
    }

    public void compareRegisteredAccounts() throws ParseException, IOException {
        JSONObject ccsItem = null;
        //atual
        JSONArray ccsRegisteredAccounts = new JSONArray(getRegisteredUsersFromFile());
        //esperado
        JSONArray dockRegisteredAccountsResp = new JSONArray(dockConnectorService.getRegisteredAccountsJobFailure());

        for (int i = 0; i < dockRegisteredAccountsResp.length(); i++) {
            JSONObject dockItem = dockRegisteredAccountsResp.getJSONObject(i);

            for (int j = 0; j < ccsRegisteredAccounts.length(); j++) {
                ccsItem = ccsRegisteredAccounts.getJSONObject(j);

                if (dockItem.get("id").equals(ccsItem.get("id"))) {
                    break;
                } else {
                    ccsItem = null;
                }
            }

            String formatDate = dateTimeUtil.formatAdate(dockItem.get("registerDate").toString().split(
                    "T")[0], "yyyy-MM-dd", "ddMMyyyy");

            try {
                Assert.assertEquals(dockItem.get("personId"), ccsItem.get("personId"));
                Assert.assertEquals(formatDate, ccsItem.get("registerDate"));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void validateRegistedAccountExistsOnTxt(String cpfAccount) throws IOException {
        JSONArray registeredAccounts = new JSONArray(getRegisteredUsersFromFile());

        for (int i = 0; i < registeredAccounts.length(); i++) {
            JSONObject jsonItem = registeredAccounts.getJSONObject(i);
            String cpfTxtFile = jsonItem.getString("cpf");

            if (cpfAccount.equals(cpfTxtFile)) {
                System.out.println("Cliente foi cadastrado");
            }
        }
    }

    public void validateAgencyAndAccountExistOnTxt() throws IOException {
        JSONArray registeredAccounts = new JSONArray(getRegisteredUsersFromFile());
        JSONObject jsonItem = registeredAccounts.getJSONObject(registeredAccounts.length() - 1);
        System.out.println(jsonItem);
        String agencyTxtFile = jsonItem.getString("agency");
        String accountTxtFile = jsonItem.getString("account");

        Assert.assertTrue("Agencia inclusa no arquivo", Objects.nonNull(agencyTxtFile));
        Assert.assertTrue("Conta inclusa no arquivo", Objects.nonNull(accountTxtFile));
    }

    public void validateCancelledAccountExistsOnTxt() throws IOException {
        JSONArray registeredAccounts = new JSONArray(getRegisteredUsersFromFile());
        JSONObject jsonItem = registeredAccounts.getJSONObject(registeredAccounts.length() - 1);
        String cancelledDateTxtFile = jsonItem.getString("cancelDate");

        Assert.assertNotNull("Data de cancelamento inclusa no arquivo", cancelledDateTxtFile);
    }
}