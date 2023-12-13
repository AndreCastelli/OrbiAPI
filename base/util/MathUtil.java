package base.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;

public class MathUtil {

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int ELEVEN = 11;
    public static final int TWELVE = 12;
    public static final int EIGHTEEN = 18;
    public static final int TWENTY = 20;
    public static final int TWENTY_EIGHT = 28;
    public static final int FORTY = 40;
    public static final int SIXTY = 60;
    public static final int SEVENTY = 70;
    public static final int HUNDRED = 100;
    public static final int TWO_HUNDRED = 200;
    public static final int ONE_THOUSAND_NINE_HUNDRED_SIXTY = 1960;
    public static final int TEN_NINETY_TWO = 1092;
    public static final int TWO_THOUSAND = 2000;
    public static final int ONE_HUNDRED_THOUSAND = 100000;
    public static final int ONE_THOUSAND = 1000;
    public static final int TEN_THOUSAND = 10000;
    public static final int TEN_MILLION = 10000000;
    public static final int NINETY_NINE_MILLION = 99999999;
    public static final int ONE_BILLION = 1000000000;
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final Double EIGHTY = 80.00;

    public static int getRandomNumber(int range) {
        return (int) (java.lang.Math.random() * range);
    }

    public static int getModRound(int dividend, int divider) {
        return (int) java.lang.Math.round(dividend - (java.lang.Math.floor(dividend / divider) * divider));
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static int minAndMaxRandomNumber(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public String setNumberOfDecimalEntries(double number) {
        return new DecimalFormat("0.00").format(number).replace(COMMA, DOT);
    }

    public String setNumberOfDecimalEntries(double number, String format) {
        return new DecimalFormat(format).format(number).replace(COMMA, DOT);
    }

    public double truncDouble(double valor, int casas) {
        double returnDouble;

        if (valor != 0) {
            double divisor = Math.pow(MathUtil.TEN, casas);
            returnDouble = valor * divisor;

            int inteiro = (int) returnDouble;
            returnDouble = inteiro / divisor;
        } else {
            returnDouble = 0.0;
        }

        return returnDouble;
    }

    public static BigDecimal parseDoubleToBigDecimal(double value) {
        return BigDecimal.valueOf(value);
    }
}