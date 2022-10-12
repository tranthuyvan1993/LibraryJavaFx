package manage_library.data_object.reader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import manage_library.util.Trimble;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Reader implements Trimble {
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
    private Timestamp createdAt;
}
