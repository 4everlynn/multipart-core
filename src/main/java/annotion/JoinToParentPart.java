package annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-28
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinToParentPart {
    Class<?> value();

    String nodeKey() default "children";

    String parentKey() default "";
}
