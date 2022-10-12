package manage_library.data_object.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private int index, categoryID, bookId, bookDetailId;
    private String bookName, categoryName, publisher, auth, image, description, code, note, status;
    private long price;
    Timestamp imported_date;

    public BookDto( int bookDetailId, int bookId ,String status, String bookName,
                   String categoryName, String publisher, String auth, String image, String description,
                   String code, String note, long price) {
        this.status = status;
        this.bookId = bookId;
        this.bookDetailId = bookDetailId;
        this.bookName = bookName;
        this.categoryName = categoryName;
        this.publisher = publisher;
        this.auth = auth;
        this.image = image;
        this.description = description;
        this.code = code;
        this.note = note;
        this.price = price;
    }

    public BookDto(int categoryID, String status, String bookName, String categoryName, String publisher, String auth, String image, String description, String code, String note, long price) {
        this.categoryID = categoryID;
        this.status = status;
        this.bookName = bookName;
        this.categoryName = categoryName;
        this.publisher = publisher;
        this.auth = auth;
        this.image = image;
        this.description = description;
        this.code = code;
        this.note = note;
        this.price = price;
    }

    public BookDto(String status, int categoryID, int bookId, String bookName, String categoryName, String publisher, String auth, String image,
                   String description, String code, String note, Timestamp imported_date, long price) {
        this.status = status;
        this.categoryID = categoryID;
        this.bookId = bookId;
        this.bookName = bookName;
        this.categoryName = categoryName;
        this.publisher = publisher;
        this.auth = auth;
        this.image = image;
        this.description = description;
        this.code = code;
        this.note = note;
        this.imported_date = imported_date;
        this.price = price;
    }
}