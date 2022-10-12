package manage_library.util;

public class Constant {
    public static class Database {
        private Database() {}
        public static final String HOST = "localhost";
        public static final String USERNAME = "root";
        public static final String PASSWORD = "12345";
        public static final String DATABASE_NAME = "library";
    }

    public static class Status {
        private Status() {}
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;
        public static final int DELETED = -1;
    }

    public static class LibraryInfo {
        private LibraryInfo() {}
        public static final String phoneNumber = "0987654321";
    }

    public enum Gender {
        MALE, FEMALE
    }

    public static class ContentEmail {
        private ContentEmail() {}
        public static final String SUBJECT_FORGET_PASSWORD = "Email quên mật khẩu";
        public static final String SUBJECT_TWOAUTH = "Check mã đăng nhập";
        public static final String SUBJECT_ANNOUNCE_DUE_DATE_BORROW_BOOK = "Thông báo đến hạn trả sách!";
        public static final String SUBJECT_ANNOUNCE_EXPIRED_DATE_BORROW_BOOK = "Thông báo quá hạn trả sách!";
        public static final String ANNOUNCE_DUE_DATE_BORROW_BOOK = "Bạn đang có %d cuốn sách đến hạn trả, gồm: %s. Vui lòng mang sách đến thư viện trong khoảng thời gian từ %s đến %s hoặc liên hệ số điện thoại %s nếu bạn không thể đến trong thời gian trên. Nếu hết thời hạn mà bạn vẫn chưa trả sách hoặc liên hệ lại, thư viện sẽ không chấp nhận trả sách sau đó!";
        public static final String ANNOUNCE_EXPIRED_DATE_BORROW_BOOK = "Bạn có %d cuốn sách trả không đúng hạn gồm %s (Lịch trả từ %s đến %s). Do bạn không liên hệ lại với thư viện để xin trả muộn nên chúng tôi sẽ không chấp nhận trả lại sau đó. Mọi ý kiến thắc mắc vui lòng liên hệ số điện thoại %s!";
        public static final String FORGET_PASSWORD = "Mật khẩu mới của bạn là %s";
        public static final String TWO_AUTH = "Mã check bảo mật đăng nhập là %s";
    }

    public static class Role {
        private Role() {}
        public static final String SUPER_ADMIN = "SUPER_ADMIN";
        public static final String ADMIN = "ADMIN";
        public static final String READER_MANAGER = "READER_MANAGER";
        public static final String ADMIN_MANAGER = "ADMIN_MANAGER";
        public static final String BOOK_MANAGER = "BOOK_MANAGER";
    }

    public static class SizeScreen {
        private SizeScreen() {}
        public static final int SCREEN_PADDING_WIDTH = 50;
        public static final int SCREEN_PADDING_HEIGHT = 20;

        public static final int SCREEN_SMALL_WIDTH = 600;
        public static final int SCREEN_SMALL_HEIGHT = 380;

        public static final int SCREEN_NAV_WIDTH = 300;
        public static final int SCREEN_HEADER_HEIGHT = 70;
    }

    public enum NAVIGATION_REDIRECT_ACTION {
        VIEW, UPDATE
    }

    public static class NavigationNameAction {
        private NavigationNameAction() {}
        public static final String TRASH_ICON = "trashIcon";
        public static final String EDIT_ICON = "editIcon";
        public static final String INFO_ICON = "infoIcon";
    }

    public static class SnackBarAction {
        private SnackBarAction() {}
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
        public static final String WARNING = "WARNING";
    }

    public static class SnackBarMessage {
        private SnackBarMessage() {}
        public static final String USER_CURRENT_BORROW_BOOK_CANNOT_BLOCK = "Không thể khóa Người đọc đang mượn sách!";

        public static final String UPDATE_ACCEPT_EMAIL_READER_ERROR = "Cập nhật trạng thái nhận Email của Người đọc thất bại!";
        public static final String UPDATE_STATUS_BOOK_ERROR = "Cập nhật trạng thái sách thất bại!";

        public static final String DELETE_ANNOUNCE_ERROR = "Xóa thông báo thất bại!";

        public static final String CREATE_SUCCESS = "Thêm mới thành công!";
        public static final String CREATE_ERROR = "Thêm mới thất bại!";

        public static final String DELETE_SUCCESS = "Xóa thành công!";
        public static final String DELETE_ERROR = "Xóa thất bại!";

        public static final String AUTO_DELETE_FILE_ERROR = "Xóa File tự động thất bại!";

        public static final String UPDATE_SUCCESS = "Cập nhật thành công!";
        public static final String UPDATE_ERROR = "Cập nhật thất bại!";

        public static final String INTERNAL_ERROR = "Lỗi hệ thống!";
        public static final String COMMON_ERROR = "Có lỗi xảy ra!";

        public static final String MAX_BORROW_BOOK_PER_READER = "Bạn chỉ được mượn tối đa 3 cuốn sách!";
        public static final String ADD_BORROW_BOOK_SUCCESS = "Thêm sách thành công!";
        public static final String REMOVE_BORROW_BOOK_SUCCESS = "Xóa sách thành công!";
        public static final String MUST_HAS_LEAST_A_BOOK_VALID = "Phải mượn ít nhất 1 cuốn sách hợp lệ!";
        public static final String DUE_DATE_MUST_GREATER_THAN_BORROW_DATE = "Ngày dự trả phải lớn hơn ngày mượn sách!";
        public static final String RETURN_DATE_MUST_GREATER_THAN_BORROW_DATE = "Ngày trả sách phải lớn hơn ngày mượn!";
        public static final String BORROW_SUCCESS = "Đăng ký mượn sách thành công!";
        public static final String MUST_HAS_REASON_READER_DO_NOT_RETURN_BOOK = "Phải ghi lý do người dùng không trả được sách!";
        public static final String MUST_HAS_REASON_READER_RETURN_BOOK_LATE = "Phải ghi thời gian người dùng trả muộn!";
    }

    public static class RegexForm {
        private RegexForm() {}
        public static final String REGEX_EMAIL = "^s*[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        public static final String REGEX_PHONE = "^(\\+84|0084|84|0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$";
        public static final String REGEX_BOOK_PRICE = "^[1-9]{1}[0-9]{3,7}$";
        public static final String REGEX_USERNAME = "^(?=.{4,20}$)(?:[a-zA-Z\\d]+(?:(?:\\.|-|_)[a-zA-Z\\d])*)+$";
        public static final String REGEX_FULLNAME = "^(?=.{6,50}$)(?:[a-zA-Z]+(?:(?:\\.| |)[a-zA-Z])*)+$";
        public static final String REGEX_BOOKNAME = "^(?=.{1,100}$)";
//        public static final String REGEX_PASS = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";
        public static final String REGEX_PASS = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&().%^*–[{}]:;',?/*~$^+=<>]).{8,}$";

    }

    public static class FormField {
        private FormField() {}
        public static final String EMAIL = "EMAIL";
        public static final String READER_CODE = "READER_CODE";
        public static final String EMAIL_USER = "EMAIL_USER";
        public static final String NAME = "NAME";
        public static final String AVATAR = "AVATAR";
        public static final String FULLNAME = "FULLNAME";
        public static final String USER_NAME = "USER_NAME";
        public static final String PHONE = "PHONE";
        public static final String PASSWORD = "PASSWORD";
        public static final String BOOK_CODE = "BOOK_CODE";
        public static final String BOOK_NAME = "BOOK_NAME";
        public static final String BOOK_AUTH = "BOOK_AUTH";
        public static final String BOOK_PUBLISHER = "BOOK_PUBLISHER";
        public static final String PRICE = "PRICE";
    }

    public static class FormTextError {

        private FormTextError() {}
        public static final String REQUIRED = "Đây là trường bắt buộc!";
        public static final String REGEX_PATTERN = " không đúng định dạng";
        public static final String REGEX_PATTERN_USERNAME = "Username phải có 4-20 ký tự và không có ký tự đặc biệt";
        public static final String REGEX_PATTERN_FULLNAME = "Họ và tên phải có 6-50 ký tự và không có ký tự đặc biệt/số";
        public static final String REGEX_PATTERN_BOOKNAME= "Tên sách phải có 4-20 ký tự";
        public static final String REGEX_PATTERN_PASS = "Mật khẩu cần có ít nhất 8 ký tự bao gồm số, chữ thường, chữ hoa, một ký tự đặc biệt";
        public static final String DUPLICATE = " đã tồn tại";
        public static final String BOOK_NOT_AVAILABLE = "Book đang được mượn bởi người khác!";
        public static final String IMAGE_NOT_AVAILABLE = "Ảnh đã bị xóa hoặc không tồn tại. Vui lòng tải ảnh mới!";

        public static final String READER_IS_CURRENT_BLOCKED_CANNOT_BORROW_BOOK = "Người đọc hiện đang bị khóa, không thể mượn sách!";
        public static final String BOOK_CODE_IS_EXISTS = "Mã cuốn sách đã tồn tại!";
        public static final String REGEX_PATTERN_BOOK_PRICE = "Số tiền nhập chưa đúng";
    }

    public static class Validator {

        private Validator() {}
        public static final String REQUIRED = "REQUIRED";
        public static final String REGEX_PATTERN = "REGEX_PATTERN";
        public static final String DUPLICATE = "DUPLICATE";
        public static final String NOT_EXISTS = "NOT_EXISTS";
    }

    public static class ReportInterval {
        private ReportInterval() {}
        public static final String DAY = "DAY";
        public static final String MONTH = "MONTH";
        public static final String YEAR = "YEAR";
    }

    public static class AnnounceType {
        private AnnounceType() {}
        public static final String ANNOUNCE_DUE_BORROW = "ANNOUNCE_DUE_BORROW";
        public static final String ANNOUNCE_EXPIRED_BORROW = "ANNOUNCE_EXPIRED_BORROW";
    }

    public static class BorrowStatus {
        private BorrowStatus() {}
        public static final String YET_RETURN = "yetReturnBtn";
        public static final String HAS_RETURN = "hasReturnBtn";
        public static final String LATE_RETURN = "lateReturnBtn";
        public static final String NOT_RETURN = "notReturnBtn";
    }

    public static class BorrowStatusInt {
        private BorrowStatusInt() {}
        public static final int YET_RETURN = 0;
        public static final int HAS_RETURN = 1;
        public static final int LATE_RETURN = 2;
        public static final int NOT_RETURN = 3;
    }

    public enum STATUS_BOOK {
        LOST_BOOK, BORROWED, AVAILABLE
    }

    public static class ActionEmail {
        private ActionEmail() {}
        public static final String RESET_PASSWORD = "RESET_PASSWORD";
        public static final String TWO_AUTH = "TWO_AUTH";
        public static final String ANNOUNCE_DUE_BORROW = "ANNOUNCE_DUE_BORROW";
        public static final String ANNOUNCE_EXPIRED_BORROW = "ANNOUNCE_EXPIRED_BORROW";
    }

    public static class AddTimeToLocalDate {
        private AddTimeToLocalDate() {}
        public static final String START_DAY = " 00:00:00";
        public static final String END_DAY = " 23:59:59";
        public static final String OPEN = " 08:00:00";
        public static final String CLOSE = " 22:00:00";
    }

    public static class PlusDayDueDate {
        private PlusDayDueDate() {}
        public static final long PLUS_DAY = 7;
    }
}
