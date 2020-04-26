package support;

import annotion.ProcessExtraExpression;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
public interface ComplexOptionalSupport<K> {

    /**
     * get entities from map parentKey
     *
     * @param key    parentKey type stored in map
     * @param tClass target class
     * @param <T>    target entity generics
     * @param deep   whether to perform deep conversion
     * @return target entity
     */
    <T> T getPart(K key, Class<T> tClass, boolean deep);

    /**
     * get entities from map parentKey
     *
     * @param key    parentKey type stored in map
     * @param tClass target class
     * @param <T>    target entity generics
     * @return target entity
     */
    default <T> T getPart(K key, Class<T> tClass) {
        return getPart(key, tClass, false);
    }

    /**
     * get entity from map according to parentKey value
     *
     * @param tClass    goal conversion type
     * @param <T>       target entity generics
     * @param deep      whether to perform deep conversion
     * @param enablePee whether to enable parsing of additional expressions
     * @return target entity
     */
    default <T> T get(Class<T> tClass, boolean deep, boolean enablePee) {
        if (enablePee) {
            ProcessExtraExpression extraExpression = tClass.getDeclaredAnnotation(ProcessExtraExpression.class);
            if (null != extraExpression) {
                return get(extraExpression.prefix(), extraExpression.suffix(), tClass, deep);
            }
        }
        return get("", "", tClass, deep);
    }

    /**
     * get entity from map according to parentKey value
     *
     * @param tClass goal conversion type
     * @param <T>    target entity generics
     * @param deep   whether to perform deep conversion
     * @return target entity
     */
    default <T> T get(Class<T> tClass, boolean deep) {
        return get(tClass, deep, false);
    }

    /**
     * get entity from map according to parentKey value
     *
     * @param prefix uniform prefix
     * @param suffix uniform suffix
     * @param tClass goal conversion type
     * @param <T>    target entity generics
     * @param deep   whether to perform deep conversion
     * @return target entity
     */
    <T> T get(String prefix, String suffix, Class<T> tClass, boolean deep);

    /**
     * get entities from map
     *
     * @param tClass goal conversion type
     * @param <T>    target entity generics
     * @return target entity
     */
    default <T> T get(Class<T> tClass) {
        if (null == tClass) {
            return null;
        }

        if (null != tClass.getDeclaredAnnotation(ProcessExtraExpression.class)) {
            return get(tClass, false, true);
        }

        return get(tClass, false);
    }
}
