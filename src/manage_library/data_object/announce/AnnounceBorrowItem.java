package manage_library.data_object.announce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceBorrowItem {
    private String bookName;
    private Integer announceId;
}
