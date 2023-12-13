package base.util;

import java.io.*;
import java.util.Base64;

public class EncodedUtil {
    private static final String IMAGE_NOT_FOUND = "Image not found ";
    private static final String EXCEPTION_READING_IMAGE = "Exception while reading the Image ";

    public String getBase64TextDecoder(String encodedText) {
        byte[] decodedArr;
        try {
            decodedArr = Base64.getDecoder().decode(encodedText);

            return new String(decodedArr, "UTF-8");
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException("Não foi encontrado a String Base64: " + encodedText);
        }
    }

    public String base64ImageEncoder(String imagePath) {
        File file = new File(imagePath);

        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte[] imageData = new byte[(int) file.length()];
            imageInFile.read(imageData);

            return Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(IMAGE_NOT_FOUND + e);
        } catch (IOException ioe) {
            throw new RuntimeException(EXCEPTION_READING_IMAGE + ioe);
        }
    }

    public void base64ImageDecoder(String base64Image, String pathFile) {
        try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
            byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
            imageOutFile.write(imageByteArray);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(IMAGE_NOT_FOUND + e);
        } catch (IOException ioe) {
            throw new RuntimeException(EXCEPTION_READING_IMAGE + ioe);
        }
    }
}
