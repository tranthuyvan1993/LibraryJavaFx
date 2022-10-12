package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BorrowDetail {
    private Integer readerId;
    private String readerCode;
    private Integer readerStatus;
    private String readerName;
    private String readerPhone;
    private Boolean readerAcceptEmail;
    private List<BorrowAddBookItem> borrowAddBookItems = new ArrayList<>();
}
