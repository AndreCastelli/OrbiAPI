package service;

import base.ProjectSettings;
import base.RequestManager;
import base.util.AzureDevOpsUtil;
import base.util.DateTimeUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import com.microsoft.azure.storage.StorageException;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.List;

public class MarketRisksWorkerService extends BaseService {
    AzureDevOpsUtil azureDevOpsUtil;

    public MarketRisksWorkerService() throws IOException, InvalidKeyException, StorageException, URISyntaxException {
        propertiesUtil = new PropertiesUtil();
        azureDevOpsUtil = new AzureDevOpsUtil();
        RequestManager.shared().setBaseURI(propertiesUtil.getPropertyByName("market.risks.worker.baseURI"));
    }

    public Response forceJobExecutionService(String job) {
        return doForceJobExecute("/v1/" + job);
    }

    public boolean validateTheGenerationOfTheTxtFile(String nomePastaArquivoGerado)
            throws URISyntaxException, StorageException {
        String fileName = azureDevOpsUtil.getFileName(nomePastaArquivoGerado);

        try {
            DateTimeUtil dateTimeUtil = new DateTimeUtil();
            
            return fileName.contains(dateTimeUtil.addOrSubtractDaysInADate(-1, "yyyyMMdd"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean downloadAndValidateTheTxtFile(String folderName)
            throws IOException, StorageException, URISyntaxException {
        String fileName = azureDevOpsUtil.getFileName(folderName);
        String localFile = getTheFileName();
        azureDevOpsUtil.getFileSize(folderName, fileName, localFile);

        return validateTxtFileStructure(localFile);
    }

    public String getTheFileName() {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        return ProjectSettings.FILE_TEMPLATE_PATH + "saldo_diario_"
                + dateTimeUtil.getDateFormatted() + dateTimeUtil.getTImeFormatted();
    }

    public boolean validateTxtFileStructure(String txtFile) throws IOException {
        List<String> fileContent = Files.readAllLines(Paths.get(txtFile));
        boolean validateDate = fileContent.get(0).contains(
                new DateTimeUtil().addOrSubtractDaysInADate(-1, "yyyy-MM-dd"));
        boolean validateSearchType = fileContent.get(0).contains("Conta de Pagamento");
        boolean totalBalance = !fileContent.get(0).split(";")[MathUtil.THREE].equals("");

        return validateDate && validateSearchType && totalBalance;
    }
}
