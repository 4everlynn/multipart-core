package support;

import core.MultiPartArray;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-30
 */
public interface MultipartMergeSupport {
    /**
     * Multi-Part merge based on class
     * For multiple MultipartData get, the hash value of the part of the condition class is the same,
     * then we can think that these values ​​can be combined
     *
     * @param conditionClasses condition classes
     * @return merged array
     */
    MultiPartArray merge(Class<?>... conditionClasses);
}
