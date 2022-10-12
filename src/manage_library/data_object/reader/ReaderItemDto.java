package manage_library.data_object.reader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReaderItemDto {
    private Integer id;
    private String status;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String createdAt;
}
