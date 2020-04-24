package support;

import java.util.List;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
public interface CollectionOptionalSupport<K> {
    /**
     * get List<T> from a certain map parentKey
     *
     * @param key    parentKey
     * @param tClass target class
     * @param <T>    generic of list
     * @return List<T>
     */
    default <T> List<T> getListPart(K key, Class<T> tClass) {
        return getListPart(key, tClass, false);
    }

    <T> List<T> getListPart(K key, Class<T> tClass, boolean deep);
}
