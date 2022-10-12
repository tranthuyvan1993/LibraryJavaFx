package manage_library.util.format;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormat {
    private static final Locale format = new Locale("vi", "VN");
    private static final NumberFormat simpleFormat = NumberFormat.getCurrencyInstance(format);

    public static String simpleCurrencyFormat(Long currency) {
        return currency == null ? "0" : simpleFormat.format(currency);
    }

    public static String ceilCurrencyFormat(Double currency) {
        return currency == null ? "0" : simpleFormat.format(Math.ceil(currency / 1000.0) * 1000);
    }

    public static String ceilCurrencyFormat(Long currency) {
        return currency == null ? "0" : simpleFormat.format(Math.ceil(currency / 1000.0) * 1000);
    }
}
