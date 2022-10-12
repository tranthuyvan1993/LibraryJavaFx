package manage_library.data_object.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReport {
    private String time;
    private long revenue;
    private long reader;
}
