package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.AzureDevOpsUtil;
import base.util.DateTimeUtil;
import base.util.FileUtil;
import base.util.PropertiesUtil;
import com.microsoft.azure.storage.StorageException;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class DataAPIService extends BaseService {

    private static final String SWAGGER_RETRIEVE = "/transactions/retrieve";
    private static final String SWAGGER_PROCESS = "/transactions/process";
    private static final String SEPARADOR = "_";
    private static final String ZIP = ".zip";
    private static final String FOLDER_PREFIX = "pld/";
    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    FileUtil csvCreateUtil;
    DateTimeUtil dateTimeUtil;
    AzureDevOpsUtil azureDevOpsUtil;

    public DataAPIService() throws IOException, InvalidKeyException, StorageException, URISyntaxException {
        propertiesUtil = new PropertiesUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("integration.pld.baseURI"));
        azureDevOpsUtil = new AzureDevOpsUtil();
        csvCreateUtil = new FileUtil();
        dateTimeUtil = new DateTimeUtil();
    }

    public String createCSVToFolder()
            throws InvalidKeyException, StorageException, URISyntaxException, IOException {
        String folderName = FOLDER_PREFIX + dateTimeUtil.getCurrentDate(DATE_FORMATTER);
        azureDevOpsUtil.createDirectory(folderName);
        azureDevOpsUtil.clearFolder(folderName);

        return folderName;
    }

    public String clearFolderDirectory()
            throws InvalidKeyException, StorageException, URISyntaxException, IOException {
        String folderName = FOLDER_PREFIX + dateTimeUtil.getCurrentDate(DATE_FORMATTER);
        azureDevOpsUtil.createDirectory(folderName);
        azureDevOpsUtil.clearFolder(folderName);

        return folderName;
    }

    public void uploadFile(String folderName, String fileName)
            throws InvalidKeyException, StorageException, URISyntaxException, IOException {
        azureDevOpsUtil.getAzureFolder(folderName);
        azureDevOpsUtil.uploadFileToAzureFolder(fileName,
                ProjectSettings.FILE_TEMP_PATH + fileName);
    }

    public Response createTicketDayService() {
        String json = "";

        return doPostRequestJson(json, SWAGGER_RETRIEVE);
    }

    public Response processesTicketsD1Services() {
        return doForceJobExecute(SWAGGER_PROCESS);
    }

    public String rechargesCSV() throws IOException {
        FileUtil fileUtil = new FileUtil();
        String rechargesPath;
        String rechargesTempPath;
        String newNameRechargesTemp;

        String newName = "150_RECHARGES_" + dateTimeUtil.getPreviousDateFormatted()
                + SEPARADOR + dateTimeUtil.getTImeFormatted() + ZIP;
        String fileName = fileUtil.getNameFile(ProjectSettings.FILE_TEMPLATE_PATH, "150_RECHARGES");

        rechargesPath = ProjectSettings.FILE_TEMPLATE_PATH + fileName;
        rechargesTempPath = ProjectSettings.FILE_TEMP_PATH + fileName;
        newNameRechargesTemp = ProjectSettings.FILE_TEMP_PATH + newName;

        fileUtil.copyAndPasteAFile(rechargesPath, rechargesTempPath);
        fileUtil.updateFileName(newNameRechargesTemp, rechargesTempPath);

        return newName;
    }

    public String transportCardCSV() throws IOException {
        FileUtil fileUtil = new FileUtil();
        String transportcardsPath;
        String transportcardsTempPath;
        String newNameTransportcardsTemp;

        String newName = "150_TRANSPORTCARDS_" + dateTimeUtil.getPreviousDateFormatted()
                + SEPARADOR + dateTimeUtil.getTImeFormatted() + ZIP;
        String fileName = fileUtil.getNameFile(ProjectSettings.FILE_TEMPLATE_PATH, "150_TRANSPORTCARDS");

        transportcardsPath = ProjectSettings.FILE_TEMPLATE_PATH + fileName;
        transportcardsTempPath = ProjectSettings.FILE_TEMP_PATH + fileName;
        newNameTransportcardsTemp = ProjectSettings.FILE_TEMP_PATH + newName;

        fileUtil.copyAndPasteAFile(transportcardsPath, transportcardsTempPath);
        fileUtil.updateFileName(newNameTransportcardsTemp, transportcardsTempPath);

        return newName;
    }
}