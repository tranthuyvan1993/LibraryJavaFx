package manage_library.util.format;

import com.jfoenix.controls.JFXDatePicker;
import javafx.util.StringConverter;
import manage_library.util.Validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormat {
    private static final SimpleDateFormat DATE_TIME_FORMAT_DATABASE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_TIME_FORMAT_RENDER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static String parseDateDatabaseToRender(String date) throws ParseException {
        return date == null ? null : DATE_TIME_FORMAT_RENDER.format(new Date(DATE_TIME_FORMAT_DATABASE.parse(date).getTime()));
    }

    public static String parseDateRenderToDatabase(String date) throws ParseException {
        return date == null ? null : DATE_TIME_FORMAT_DATABASE.format(new Date(DATE_TIME_FORMAT_RENDER.parse(date).getTime()));
    }

    public static Date parseDateStringToDate(String date) throws ParseException {
        return date == null ? null : DATE_TIME_FORMAT_DATABASE.parse(date);
    }

    public static String parseDateToDateString(Date date) {
        return date == null ? null : DATE_TIME_FORMAT_DATABASE.format(date);
    }

    public static LocalDate parseDateStringToLocalDate(String date) throws ParseException {
        return date == null ? null : Instant.ofEpochMilli(new Date(DATE_TIME_FORMAT_DATABASE.parse(date).getTime()).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String parseLocalDateToDateString(LocalDate date, String time) {
        if (date == null) return null;
        String month = date.getMonthValue() < 10 ? "0" + date.getMonthValue() : String.valueOf(date.getMonthValue());
        String day = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : String.valueOf(date.getDayOfMonth());
        return date.getYear() + "-" +
                       month + "-" + day + time;
    }

    public static void formatDatePicker(Object... params) {
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (Validation.textNotEmpty(string)) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        for (int i = 0; i < params.length; i ++) {
            ((JFXDatePicker) params[i]).setConverter(converter);
        }
    }
}
