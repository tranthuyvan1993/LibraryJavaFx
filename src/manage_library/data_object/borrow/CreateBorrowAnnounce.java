package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowAnnounce {
    private int userId;
    private List<BorrowAddBookItem> listBooks;

    public CreateBorrowAnnounce(BorrowDetail borrowDetail) {
        this.userId = borrowDetail.getReaderId();
        this.listBooks = borrowDetail.getBorrowAddBookItems();
    }
}
