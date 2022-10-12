package manage_library.data_object.book;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import manage_library.util.Constant;
import manage_library.util.SnackBar;
import manage_library.util.Validation;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookRenderDto {
    private int index, categoryID, bookId, bookDetailId;
    private String bookName, categoryName, publisher, auth, description, code, note, status;
    private String price;
    private Timestamp imported_date;
    private ImageView image;

    public BookRenderDto(BookItem bookItem) throws IOException {
        this.categoryID = bookItem.getCategoryID();
        this.bookId = bookItem.getBookId();
        this.bookDetailId = bookItem.getBookDetailId();
        this.bookName = bookItem.getBookName();
        this.categoryName = bookItem.getCategoryName();
        this.publisher = bookItem.getPublisher();
        this.auth = bookItem.getAuth();
        this.description = bookItem.getDescription();
        this.code = bookItem.getCode();
        this.note = bookItem.getNote();
        this.status = bookItem.getStatus();
        this.price = bookItem.getPrice();
        if (Validation.textNotEmpty(bookItem.getImage())) {
            String imageBookName = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/" + bookItem.getImage();
            try (FileInputStream inputStream = new FileInputStream(imageBookName)) {
                this.image = new ImageView(new Image(inputStream));
                this.image.setFitWidth(60);
                this.image.setFitHeight(80);
            } catch (Exception e) {
                System.out.println("File not found!");
            }
        }
    }
}