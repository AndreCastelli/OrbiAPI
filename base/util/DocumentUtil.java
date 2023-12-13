package base.util;

public class DocumentUtil {

    public String getRandomValidCPF(boolean withMask) {
        int range = MathUtil.NINE;
        int n1 = MathUtil.getRandomNumber(range);
        int n2 = MathUtil.getRandomNumber(range);
        int n3 = MathUtil.getRandomNumber(range);
        int n4 = MathUtil.getRandomNumber(range);
        int n5 = MathUtil.getRandomNumber(range);
        int n6 = MathUtil.getRandomNumber(range);
        int n7 = MathUtil.getRandomNumber(range);
        int n8 = MathUtil.getRandomNumber(range);
        int n9 = MathUtil.getRandomNumber(range);
        int d1 = n9 * MathUtil.TWO + n8 * MathUtil.THREE + n7 * MathUtil.FOUR
                + n6 * MathUtil.FIVE + n5 * MathUtil.SIX + n4 * MathUtil.SEVEN
                + n3 * MathUtil.EIGHT + n2 * MathUtil.NINE + n1 * MathUtil.TEN;

        d1 = MathUtil.ELEVEN - (MathUtil.getModRound(d1, MathUtil.ELEVEN));

        if (d1 >= MathUtil.TEN) {
            d1 = 0;
        }

        int d2 = d1 * MathUtil.TWO + n9 * MathUtil.THREE + n8 * MathUtil.FOUR
                + n7 * MathUtil.FIVE + n6 * MathUtil.SIX + n5 * MathUtil.SEVEN
                + n4 * MathUtil.EIGHT + n3 * MathUtil.NINE + n2 * MathUtil.TEN
                + n1 * MathUtil.ELEVEN;

        d2 = MathUtil.ELEVEN - (MathUtil.getModRound(d2, MathUtil.ELEVEN));

        if (d2 >= MathUtil.TEN) {
            d2 = MathUtil.ZERO;
        }

        if (withMask) {
            return "" + n1 + n2 + n3 + '.' + n4 + n5 + n6 + '.' + n7 + n8 + n9 + '-' + d1 + d2;
        } else {
            return "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + d1 + d2;
        }
    }

    public String getRandomValidCNPJ(boolean withMask) {
        String cnpj;
        int range = MathUtil.NINE;
        int n1 = MathUtil.getRandomNumber(range);
        int n2 = MathUtil.getRandomNumber(range);
        int n3 = MathUtil.getRandomNumber(range);
        int n4 = MathUtil.getRandomNumber(range);
        int n5 = MathUtil.getRandomNumber(range);
        int n6 = MathUtil.getRandomNumber(range);
        int n7 = MathUtil.getRandomNumber(range);
        int n8 = MathUtil.getRandomNumber(range);
        int n9 = MathUtil.ZERO;
        int n10 = MathUtil.ZERO;
        int n11 = MathUtil.ZERO;
        int n12 = MathUtil.ONE;
        int d1 = n12 * MathUtil.TWO + n11 * MathUtil.THREE + n10 * MathUtil.FOUR
                + n9 * MathUtil.FIVE + n8 * MathUtil.SIX + n7 * MathUtil.SEVEN
                + n6 * MathUtil.EIGHT + n5 * MathUtil.NINE + n4 * MathUtil.TWO
                + n3 * MathUtil.THREE + n2 * MathUtil.FOUR + n1 * MathUtil.FIVE;

        d1 = MathUtil.ELEVEN - (MathUtil.getModRound(d1, MathUtil.ELEVEN));

        if (d1 >= MathUtil.TEN) {
            d1 = MathUtil.ZERO;
        }

        int d2 = d1 * MathUtil.TWO + n12 * MathUtil.THREE + n11 * MathUtil.FOUR
                + n10 * MathUtil.FIVE + n9 * MathUtil.SIX + n8 * MathUtil.SEVEN
                + n7 * MathUtil.EIGHT + n6 * MathUtil.NINE + n5 * MathUtil.TWO
                + n4 * MathUtil.THREE + n3 * MathUtil.FOUR + n2 * MathUtil.FIVE
                + n1 * MathUtil.SIX;

        d2 = MathUtil.ELEVEN - (MathUtil.getModRound(d2, MathUtil.ELEVEN));

        if (d2 >= MathUtil.TEN) {
            d2 = MathUtil.ZERO;
        }

        if (withMask) {
            cnpj = "" + n1 + n2 + '.' + n3 + n4 + n5 + '.' + n6 + n7 + n8 + '.' + n9 + n10 + n11 + n12 + '-' + d1 + d2;
        } else {
            cnpj = "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + d1 + d2;
        }

        return cnpj;
    }

    public String rg(boolean withPoints) {
        int n1 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n2 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n3 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n4 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n5 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n6 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n7 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n8 = MathUtil.getRandomNumber(MathUtil.TEN);
        int n9 = MathUtil.getRandomNumber(MathUtil.TEN);

        if (withPoints) {
            return "" + n1 + n2 + '.' + n3 + n4 + n5 + '.' + n6 + n7 + n8 + "-" + n9;
        } else {
            return "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9;
        }
    }
}
