package support;

import core.Data;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2020-04-24
 */
public interface DataPlugin {
    DataPlugin install(Data data);

    Data attach();
}
