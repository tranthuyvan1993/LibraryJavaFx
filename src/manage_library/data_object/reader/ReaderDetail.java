package manage_library.data_object.reader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReaderDetail {
    private Integer id;
    private String code;
    private String name;
    private String avatar;
    private String phone;
    private String email;
    private String address;
    private Integer status;
    private Boolean acceptEmail;
    private String gender;
}
