package manage_library.data_object.announce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Announce {
    private String email;
    private String announceTime;
    private String announceType;
    private List<AnnounceBorrowItem> announceBorrowItems;
}
