package weapon;


import support.FunctionalFieldCollector;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author 4everlynn
 */
public class LambdaUtils {
    private static Map<Class, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

    private static final String METHOD_GETTER = "get";

    private static final String METHOD_IS = "is";

    /***
     * conversion method reference as attribute name
     */
    public static <T> String fieldName(FunctionalFieldCollector<T> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if (methodName.startsWith(METHOD_GETTER)) {
            prefix = METHOD_GETTER;
        } else if (methodName.startsWith(METHOD_IS)) {
            prefix = METHOD_IS;
        }
        if (prefix == null) {
            return methodName;
        }

        Function<String, String> resolve = str -> {
            StringBuilder builder = new StringBuilder(str);
            builder.replace(0, 1, (str.charAt(0) + "").toLowerCase());
            return builder.toString();
        };

        return resolve.apply(methodName.replaceFirst(prefix, ""));
    }


    private static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }
}
