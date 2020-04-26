package support;

import annotion.ProcessExtraExpression;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 4everlynn
 * @version V1.0
 * @description
 * @date 2020-04-26
 */

@Accessors(chain = true)
@Data
@ProcessExtraExpression(prefix = "act_")
public class Action {
    private String name;
}
