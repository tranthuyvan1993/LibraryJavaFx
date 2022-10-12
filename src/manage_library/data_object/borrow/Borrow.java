package manage_library.data_object.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
    private Integer id;
    private Integer readerId;
    private Integer bookDetailId;
    private Timestamp borrowDate;
    private Timestamp dueDate;
    private Timestamp returnDate;
    private String note;
}
