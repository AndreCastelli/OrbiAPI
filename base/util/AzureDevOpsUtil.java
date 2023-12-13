package base.util;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.*;
import org.apache.commons.io.FileUtils;
import service.BaseService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class AzureDevOpsUtil extends BaseService {

    private static final String AZURE_STORAGE_FILE_SHARE_NAME = "azure.storage.fileShareName";
    private static final String AZURE_STORAGE_ACCOUNT_NAME = "azure.storage.accountName";
    private static final String AZURE_STORAGE_ACCOUNT_KEY = "azure.storage.accountKey";
    String storageConnectionString;
    CloudStorageAccount storageAccount;
    CloudFileDirectory rootDir;
    CloudFileDirectory sampleDir;
    CloudFile cloudFile;
    String file;

    public AzureDevOpsUtil() throws IOException, URISyntaxException, InvalidKeyException, StorageException {
        propertiesUtil = new PropertiesUtil();
        CloudFileClient fileClient = getAuth().createCloudFileClient();
        CloudFileShare share = fileClient.getShareReference(
                propertiesUtil.getPropertyByName(AZURE_STORAGE_FILE_SHARE_NAME));
        rootDir = share.getRootDirectoryReference();
    }

    public String getStorageConnectionString() {
        storageConnectionString = "DefaultEndpointsProtocol=https;"
                + "AccountName=" + propertiesUtil.getPropertyByName(AZURE_STORAGE_ACCOUNT_NAME)
                + "AccountKey=" + propertiesUtil.getPropertyByNameBase64(AZURE_STORAGE_ACCOUNT_KEY);

        return storageConnectionString;
    }

    public CloudStorageAccount getAuth() throws URISyntaxException, InvalidKeyException {
        storageAccount = CloudStorageAccount.parse(getStorageConnectionString());

        return storageAccount;
    }

    public CloudFileDirectory getAzureStorage() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudFileClient fileClient = getAuth().createCloudFileClient();
        CloudFileShare share = fileClient.getShareReference(
                propertiesUtil.getPropertyByName(AZURE_STORAGE_FILE_SHARE_NAME));
        rootDir = share.getRootDirectoryReference();

        return rootDir;
    }

    public CloudFileDirectory getAzureFolder(String folderName)
            throws InvalidKeyException, StorageException, URISyntaxException {
        sampleDir = getAzureStorage().getDirectoryReference(folderName);

        return sampleDir;
    }

    public void uploadFileToAzureFolder(String fileName, String toFolder)
            throws URISyntaxException, StorageException, IOException {
        cloudFile = sampleDir.getFileReference(fileName);
        cloudFile.uploadFromFile(toFolder);
    }

    public void uploadFileToAzureFolderDataAPI(String fileName, String toFolder)
            throws URISyntaxException, StorageException, IOException {
        cloudFile = sampleDir.getFileReference(fileName);
        cloudFile.uploadFromFile(toFolder);
    }

    public String downloadFileFromAzureFolder(String nomeArquivo)
            throws URISyntaxException, StorageException, IOException {
        CloudFile fileDownloaded = sampleDir.getFileReference(nomeArquivo);

        return fileDownloaded.downloadText();
    }

    public long getFileSize(String folderName, String fileName, String downloadToFile)
            throws URISyntaxException, StorageException, IOException {
        sampleDir = rootDir.getDirectoryReference(folderName);
        cloudFile = sampleDir.getFileReference(fileName);
        //cloudFile.downloadToFile(ProjectSettings.FILE_CCS_PATH + "ccs_text_file_generated.txt");
        cloudFile.downloadToFile(downloadToFile);
        File fileCriado = new File(downloadToFile);

        if (!fileCriado.exists()) {
            cloudFile.downloadToFile(downloadToFile);
            fileCriado = new File(downloadToFile);
        }

        return FileUtils.sizeOf(fileCriado);
    }

    public void clearFolder(String folderName) throws URISyntaxException, StorageException {
        sampleDir = rootDir.getDirectoryReference(folderName);
        Iterable<ListFileItem> fileNameList = sampleDir.listFilesAndDirectories();
        for (ListFileItem fileName : fileNameList) {
            cloudFile = sampleDir.getFileReference(((CloudFile) fileName).getName());
            cloudFile.deleteIfExists();
        }
    }

    public String getFileName(String folderName) throws URISyntaxException, StorageException {
        sampleDir = rootDir.getDirectoryReference(folderName);
        Iterable<ListFileItem> list = sampleDir.listFilesAndDirectories();
        for (ListFileItem fileItem02 : list) {
            file = ((CloudFile) fileItem02).getName();
        }

        return file;
    }

    public String getFileName(String folderName, String fileNameToCompare) throws URISyntaxException, StorageException {
        for (int i = 0; i < 5; i++) {
            file = searchForTheFileInTheFolder(folderName, fileNameToCompare);

            if (file != null) {
                return file;
            }
        }

        return null;
    }

    public String searchForTheFileInTheFolder(String folderName, String fileNameToCompare)
            throws URISyntaxException, StorageException {
        sampleDir = rootDir.getDirectoryReference(folderName);
        Iterable<ListFileItem> list = sampleDir.listFilesAndDirectories();

        for (ListFileItem fileItem02 : list) {
            file = ((CloudFile) fileItem02).getName();

            if (file.equals(fileNameToCompare)) {
                return file;
            }
        }

        return null;
    }

    public void createDirectory(String folderName) throws URISyntaxException, StorageException {
        CloudFileDirectory directory = rootDir.getDirectoryReference(folderName);
        directory.createIfNotExists();
    }
}
