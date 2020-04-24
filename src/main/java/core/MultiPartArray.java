package core;

import java.util.ArrayList;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-28
 */
public class MultiPartArray extends ArrayList<MultipartData> {

    public MultiPartArray merge(Class<?> entranceClass) {
        return MultipartData.merge(this, entranceClass, true);
    }

}
