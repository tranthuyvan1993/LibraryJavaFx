package manage_library.util;

import com.google.gson.Gson;
import manage_library.data_object.borrow.BorrowDetail;
import manage_library.data_object.borrow.CreateBorrowAnnounce;
import manage_library.util.format.DateFormat;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertQuery {
    private final static Pattern patternListInt = Pattern.compile("^\\[(\\d+(.0)?,?)+\\]$");
    private static Matcher matcher;

    private final static Gson gson = new Gson();
    public static void convertQuery(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i ++) {
            if (params[i] instanceof String) {
                try {
                    matcher = patternListInt.matcher((String) params[i]);
                    if (matcher.find()) {
                        List<Double> listIds = gson.fromJson((String) params[i], List.class);
                        for (int j = 0; j < listIds.size(); j ++) {
                            preparedStatement.setInt(j + 1, listIds.get(j).intValue());
                        }
                    } else {
                        preparedStatement.setString(i + 1, (String) params[i]);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (params[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (Boolean) params[i]);
            } else if (params[i] instanceof Timestamp) {
                preparedStatement.setTimestamp(i + 1, (Timestamp) params[i]);
            } else if (params[i] instanceof LocalDate) {
                preparedStatement.setString(i + 1, DateFormat.parseLocalDateToDateString((LocalDate) params[i], Constant.AddTimeToLocalDate.OPEN));
            } else if (params[i] instanceof CreateBorrowAnnounce) {
                CreateBorrowAnnounce createBorrowAnnounce = ((CreateBorrowAnnounce) params[i]);
                int start = 0;
                for (int j = 0; j < createBorrowAnnounce.getListBooks().size(); j ++) {
                    preparedStatement.setInt(start + 1, createBorrowAnnounce.getUserId());
                    preparedStatement.setString(start + 2, DateFormat.parseLocalDateToDateString(createBorrowAnnounce.getListBooks().get(j).getDueDate().minusDays(1), Constant.AddTimeToLocalDate.OPEN));
                    preparedStatement.setString(start + 3, Constant.AnnounceType.ANNOUNCE_DUE_BORROW);
                    preparedStatement.setInt(start + 4, createBorrowAnnounce.getListBooks().get(j).getBookDetailId());
                    start += 4;
                }
            } else if (params[i] instanceof BorrowDetail) {
                BorrowDetail borrowDetail = ((BorrowDetail) params[i]);
                int start = 0;
                for (int j = 0; j < borrowDetail.getBorrowAddBookItems().size(); j ++) {
                    preparedStatement.setInt(start + 1, borrowDetail.getReaderId());
                    preparedStatement.setInt(start + 2, borrowDetail.getBorrowAddBookItems().get(j).getBookDetailId());
                    preparedStatement.setString(start + 3, DateFormat.parseLocalDateToDateString(borrowDetail.getBorrowAddBookItems().get(j).getDueDate(), Constant.AddTimeToLocalDate.CLOSE));
                    preparedStatement.setString(start + 4, borrowDetail.getBorrowAddBookItems().get(j).getBorrowNote());
                    start += 4;
                }
            } else {
                preparedStatement.setString(i + 1, null);
            }
        }
    }

}
