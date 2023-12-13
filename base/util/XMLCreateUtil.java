package base.util;

import base.ProjectSettings;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class XMLCreateUtil {

    FileWriter fileWriter;
    File filePath;

    public void createXmlByTemplateFile(String newFileName, String xmlTemplateFilePath) throws IOException {
        fileWriter = new java.io.FileWriter(newFileName);
        fileWriter.write(readFile(xmlTemplateFilePath));
        fileWriter.close();
    }

    public String readFile(String xmlTemplateFilePath) throws IOException {
        filePath = new File(xmlTemplateFilePath);
        StringBuilder fileContents = new StringBuilder((int) filePath.length());
        try (Scanner scanner = new Scanner(filePath)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
                System.out.println(scanner.nextLine());
            }

            return fileContents.toString();
        }
    }

    private void deleteFileFromFolder() throws IOException {
        FileUtils.cleanDirectory(new File(ProjectSettings.FILE_TEMPLATE_PATH));
    }

    public String getXmlHeader(String xmlTemplateFilePath) {
        filePath = new File(xmlTemplateFilePath);
        try {
            Scanner scanner = new Scanner(filePath);

            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String getTheRootNodeAttributes(String xmlTemplateFilePath) {
        filePath = new File(xmlTemplateFilePath);
        try {
            Scanner scanner = new Scanner(filePath);
            scanner.nextLine();

            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public NodeList returnsChildrenOfTheNode(String xmlFile, String searchedNode) {
        return rootElement(xmlFile).getElementsByTagName(searchedNode);
    }

    public String getRootAttributeValue(String xmlFile, String attributeName) {
        return rootElement(xmlFile).getDocumentElement().getAttribute(attributeName);
    }

    public String getTheValueOfAnAttribute(String xmlFile, String nodeXML, String attributeName) {
        NodeList nList = returnsChildrenOfTheNode(xmlFile, nodeXML);
        Node node = nList.item(0);

        return ((Element) node).getAttribute(attributeName);
    }

    public String getRootElementTag(String xmlFile) {
        return rootElement(xmlFile).getDocumentElement().getNodeName();
    }

    public Document rootElement(String xmlFile) {
        try {
            File file = new File(xmlFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }
}
