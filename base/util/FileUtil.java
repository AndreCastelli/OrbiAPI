package base.util;

import base.ProjectSettings;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class FileUtil {

    FileWriter fileWriter;
    File filePath;

    public boolean updateFileName(String newName, String file) {
        File zipFileOld = new File(file);
        File zipFileNew = new File(newName);

        return zipFileOld.renameTo(zipFileNew);
    }

    public String getNameFile(String directory, String searchedFile) {
        File file = new File(directory);
        File[] afile = file.listFiles();
        int i = 0;
        for (int j = afile.length; i < j; i++) {
            File arquivos = afile[i];

            if (arquivos.getName().contains(searchedFile)) {
                return arquivos.getName();
            }
        }

        return "";
    }

    public void createCsvByTemplateFile(String newFileName, String csvTemplateFilePath) throws IOException {
        fileWriter = new java.io.FileWriter(newFileName);
        fileWriter.write(readFile(csvTemplateFilePath));
        fileWriter.close();
    }

    public String readFile(String csvTemplateFilePath) throws IOException {
        filePath = new File(csvTemplateFilePath);
        StringBuilder fileContents = new StringBuilder((int) filePath.length());
        try (Scanner scanner = new Scanner(filePath)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
            }

            return fileContents.toString();
        }
    }

    public void writeToFile(String direcAndFile, String fileContent) throws IOException {
        FileWriter file = new FileWriter(direcAndFile);
        file.write(fileContent);
        file.flush();
        file.close();
    }

    public void deleteFileFromFolder() throws IOException {
        FileUtils.cleanDirectory(new File(ProjectSettings.FILE_TEMPLATE_PATH));
    }

    public String getTheAbsolutePath(String archive) {
        return new File(archive).getAbsolutePath();
    }

    public void createEmptyFile(String pathAndFile) {
        try {
            File file = new File(pathAndFile);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyAndPasteAFile(String copyFile, String pasteFile) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try {
            sourceChannel = new FileInputStream(copyFile).getChannel();
            destinationChannel = new FileOutputStream(pasteFile).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }

            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
    }

    public void createTempDir(String directory) {
        File tempDir = new File(directory);

        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
    }

    public void deleteFile(String directory) {
        File tempDir = new File(directory);
        tempDir.deleteOnExit();
    }
}