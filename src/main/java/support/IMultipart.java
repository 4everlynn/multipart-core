package support;

import java.util.Optional;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
public interface IMultipart {
    /**
     * putting objects into the implementation of Multipart
     *
     * @param unpack whether to parse the package
     * @param targets objects
     * @return chain
     */
    IMultipart part(boolean unpack, Object... targets);

    /**
     * core method assembling multiple objects
     *
     * @param targets target object array
     * @return chain
     */
    default IMultipart parties(Object... targets) {
        return part(false, targets);
    }

    /**
     * get the object under the deep path from the current object
     *
     * @param tClass target type
     * @param deep   whether to perform deep conversion (consumption of resources)
     * @param path   path index
     * @param <T>    to be transformed generics
     * @return acquired audience
     */
    <T> T get(Class<T> tClass, boolean deep, String... path);

    /**
     * used with java 8optional for null pointer judgment
     *
     * @param tClass target type
     * @param deep   whether to perform deep conversion (consumption of resources)
     * @param paths  path index
     * @param <T>    to be transformed generics
     * @return acquired audience
     */
    default <T> Optional<T> getOptional(Class<T> tClass, boolean deep, String... paths) {
        return Optional.ofNullable(get(tClass, deep, paths));
    }

    /**
     * provide getOptional default non-deep conversion method
     *
     * @param tClass target type
     * @param paths  path index
     * @param <T>    to be transformed generics
     * @return acquired audience
     */
    default <T> Optional<T> getOptional(Class<T> tClass, String... paths) {
        return getOptional(tClass, false, paths);
    }


    /**
     * get the object under the deep path from the current object
     *
     * @param tClass target type
     * @param path   path index
     * @param <T>    to be transformed generics
     * @return acquired audience
     */
    default <T> T get(Class<T> tClass, String... path) {
        return get(tClass, false, path);
    }
}
