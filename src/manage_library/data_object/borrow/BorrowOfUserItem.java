package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BorrowOfUserItem {
    private Integer borrowId;
    private Integer bookDetailId;
    private Integer bookDetailStatus;
    private String image;
    private String bookCode;
    private String bookName;
    private String auth;
    private Long price;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String note;
    private String borrowNote;
    private Integer borrowStatus;
}
