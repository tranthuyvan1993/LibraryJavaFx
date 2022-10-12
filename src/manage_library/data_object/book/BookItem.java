package manage_library.data_object.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookItem {
    private int categoryID, bookId, bookDetailId;
    private String bookName, categoryName, publisher, auth, image, description, code, note, status, price;
}
