
package support;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author 4everlynn
 */
public interface FunctionalFieldCollector<T> extends Serializable, Function<T, String>{
}