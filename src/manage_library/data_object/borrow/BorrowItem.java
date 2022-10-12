package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BorrowItem {
    private Integer id;
    private Integer readerId;
    private String borrowDate;
    private Integer status;
    private String readerCode;
    private String readerName;
    private String readerPhone;
    private String bookName;
    private String bookCode;
    private String auth;
    private String price;
    private String deposit;
    private String dueDate;
    private String returnDate;
    private String note;
}
