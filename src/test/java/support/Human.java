package support;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Human {
    private String name;
    private Integer age;
    private String gender;
}
