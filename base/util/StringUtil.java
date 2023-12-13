package base.util;

public class StringUtil {

    StringBuilder stringBuilder;

    // Metodo criado para remover o dia do campo invoiceDueDate que retorna esse formato 2022-04-13
    public String deletePartOfString(String stringComplete, int indexStart, int indexEnd) {
        stringBuilder = new StringBuilder(stringComplete);
        return  stringBuilder.delete(indexStart, indexEnd).toString();
    }
}
