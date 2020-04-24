package support;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
public interface BasicDataOptionalSupport<K> {
    /**
     * @param key parentKey type stored in map
     * @return Byte
     */
    Byte getBytePart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Short
     */
    Short getShortPart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Digital
     */
    Integer getIntegerPart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Long
     */
    Long getLongPart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Float
     */
    Float getFloatPart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Double
     */
    Double getDoublePart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Boolean
     */
    Boolean getBooleanPart(K key);

    /**
     * @param key parentKey type stored in map
     * @return Character
     */
    Character getCharPart(K key);
}
