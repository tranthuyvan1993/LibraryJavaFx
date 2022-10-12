package manage_library.util;

import com.jfoenix.controls.JFXButton;
import manage_library.database.DatabaseHandler;
import manage_library.model.AdminModel;
import manage_library.model.BookModel;
import manage_library.model.ReaderModel;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class Validation {
    public static boolean textNotEmpty(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static String validationField(String field, List<String> valid, String value, Pattern pattern, JFXButton applyBtn) throws SQLException {
        switch (field) {
            case Constant.FormField.EMAIL:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return "Email" + Constant.FormTextError.REGEX_PATTERN;
                } else if (Boolean.TRUE.equals(ReaderModel.checkExistsByEmail(DatabaseHandler.getInstance().getDbConnection(), value))) {
                    applyBtn.setDisable(true);
                    return "Email" + Constant.FormTextError.DUPLICATE;
                }
                return null;
            case Constant.FormField.EMAIL_USER:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return "Email" + Constant.FormTextError.REGEX_PATTERN;
                } else if (Boolean.TRUE.equals(AdminModel.checkExistsByEmail(DatabaseHandler.getInstance().getDbConnection(), value))) {
                    applyBtn.setDisable(true);
                    return "Email" + Constant.FormTextError.DUPLICATE;
                }
                return null;
            case Constant.FormField.PHONE:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return "Số điện thoại" + Constant.FormTextError.REGEX_PATTERN;
                }
                return null;
            case Constant.FormField.PRICE:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return "Giá tiền" + Constant.FormTextError.REGEX_PATTERN;
                }
                return null;
            case Constant.FormField.NAME:
                if (valid.contains(Constant.Validator.REQUIRED)) {
                    if (value.trim().equals("")) {
                        applyBtn.setDisable(true);
                        return Constant.FormTextError.REQUIRED;
                    }
                }
                return null;
            case Constant.FormField.USER_NAME:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REGEX_PATTERN_USERNAME;
                }else if (Boolean.TRUE.equals(AdminModel.checkExistsByUsername(DatabaseHandler.getInstance().getDbConnection(), value))) {
                    applyBtn.setDisable(true);
                    return "Username" + Constant.FormTextError.DUPLICATE;
                }
                return null;
            case Constant.FormField.PASSWORD:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REGEX_PATTERN_PASS;
                }
                return null;
            case Constant.FormField.FULLNAME:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REGEX_PATTERN_FULLNAME;
                }
                return null;
            case Constant.FormField.AVATAR:
            case Constant.FormField.READER_CODE:
            case Constant.FormField.BOOK_AUTH:
            case Constant.FormField.BOOK_PUBLISHER:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                }
                return null;
            case Constant.FormField.BOOK_NAME:
                if (valid.contains(Constant.Validator.REQUIRED) && value.trim().equals("")) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REQUIRED;
                } else if (!value.trim().equals("") && !pattern.matcher(value).find()) {
                    applyBtn.setDisable(true);
                    return Constant.FormTextError.REGEX_PATTERN_BOOKNAME;
                }
//                else if (Boolean.TRUE.equals(BookModel.checkExistsByBookName(DatabaseHandler.getInstance().getDbConnection(), value))) {
//                    applyBtn.setDisable(true);
//                    return "Tên sách đã tồn tại";
//                }
                return null;
            default:
                return null;
        }
    }
}
