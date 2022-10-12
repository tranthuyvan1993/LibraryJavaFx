package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import manage_library.util.format.DateFormat;

import java.text.ParseException;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class BorrowAddBookItem {
    private Integer borrowId;
    private Integer bookDetailId;
    private Integer bookDetailStatus;
    private String image;
    private String bookCode;
    private String bookName;
    private String auth;
    private Long price;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String note;
    private String borrowNote;
    private Integer borrowStatus;

    public BorrowAddBookItem(String bookCode) {
        this.bookCode = bookCode;
    }

    public BorrowAddBookItem(BorrowOfUserItem borrowOfUserItem) throws ParseException {
        this.borrowId = borrowOfUserItem.getBorrowId();
        this.bookDetailId = borrowOfUserItem.getBookDetailId();
        this.bookDetailStatus = borrowOfUserItem.getBookDetailStatus();
        this.image = borrowOfUserItem.getImage();
        this.bookCode = borrowOfUserItem.getBookCode();
        this.bookName = borrowOfUserItem.getBookName();
        this.auth = borrowOfUserItem.getAuth();
        this.price = borrowOfUserItem.getPrice() != null ? borrowOfUserItem.getPrice() : 0;
        this.borrowDate = borrowOfUserItem.getBorrowDate() != null ? DateFormat.parseDateStringToLocalDate(borrowOfUserItem.getBorrowDate()) : null;
        this.dueDate = borrowOfUserItem.getDueDate() != null ? DateFormat.parseDateStringToLocalDate(borrowOfUserItem.getDueDate()) : null;
        this.returnDate = borrowOfUserItem.getReturnDate() != null ? DateFormat.parseDateStringToLocalDate(borrowOfUserItem.getReturnDate()) : null;
        this.note = borrowOfUserItem.getNote();
        this.borrowNote = borrowOfUserItem.getBorrowNote();
        this.borrowStatus = borrowOfUserItem.getBorrowStatus();
    }
}
