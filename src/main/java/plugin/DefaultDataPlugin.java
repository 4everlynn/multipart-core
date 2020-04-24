package plugin;

import core.Data;
import support.DataPlugin;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2020-04-24
 */
public class DefaultDataPlugin implements DataPlugin {
    private Data data;

    @Override
    public DataPlugin install(Data data) {
        this.data = data;
        return this;
    }

    @Override
    public Data attach() {
        return this.data;
    }

    public DefaultDataPlugin message(String val) {
        this.data.include("message", val);
        return this;
    }

    public DefaultDataPlugin code(Integer val) {
        this.data.include("code", val);
        return this;
    }

    public DefaultDataPlugin data(Object val) {
        this.data.include("data", val);
        return this;
    }
}
