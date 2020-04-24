package core;

import annotion.ProcessExtraExpression;
import plugin.DefaultDataPlugin;
import support.*;
import weapon.EntityWeapons;
import weapon.KeyWeapons;
import weapon.LambdaUtils;
import weapon.Strings;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class Data extends LinkedHashMap<String, Object>
        implements BasicDataOptionalSupport<String>, CollectionOptionalSupport<String>, ComplexOptionalSupport<String> {
    private static Set<Class<?>> BASIC_WRAPPED_TYPES;
    private boolean memoryOptimization = false;
    private static final Byte SINGE_LEN_STR = 1;

    private DataPlugin plugin;

    public void setMemoryOptimization(boolean memoryOptimization) {
        this.memoryOptimization = memoryOptimization;
    }

    // initialize the underlying data type collection
    static {
        BASIC_WRAPPED_TYPES = new HashSet<>(8);
        BASIC_WRAPPED_TYPES.add(Byte.class);
        BASIC_WRAPPED_TYPES.add(Short.class);
        BASIC_WRAPPED_TYPES.add(Integer.class);
        BASIC_WRAPPED_TYPES.add(Long.class);
        BASIC_WRAPPED_TYPES.add(Float.class);
        BASIC_WRAPPED_TYPES.add(Double.class);
        BASIC_WRAPPED_TYPES.add(Boolean.class);
        BASIC_WRAPPED_TYPES.add(Character.class);
    }

    static Data from(Map<String, Object> map) {
        Data data = new Data();
        data.putAll(map);
        return data;
    }

    public DefaultDataPlugin plugin() {
        return this.plugin(DefaultDataPlugin.class);
    }

    public <T extends DataPlugin> T plugin(Class<T> tClass) {
        if (null == this.plugin) {
            Optional<T> instance = EntityWeapons.instance(tClass);
            instance.ifPresent(t -> this.plugin = t.install(this));
        }
        if (tClass.isAssignableFrom(this.plugin.getClass())) {
            return tClass.cast(this.plugin);
        }
        return null;
    }

    protected Class<?> getClazz(String key) {
        Object o = get(key);
        if (null != o) {
            return o.getClass();
        }
        return null;
    }

    /**
     * rewrite the get method to get the name method
     *
     * @param key target key can be underlined or camel case
     * @return data
     */
    Object get(String key) {
        Object target = super.get(KeyWeapons.convert(key));
        if (target != null) {
            return target;
        } else if ((target = super.get(KeyWeapons.convertLine(key))) != null) {
            return target;
        } else {
            return super.get(key);
        }
    }

    public Data include(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public <T> T includeAndReturn(String key, T value) {
        super.put(key, value);
        return value;
    }

    public Date getDatePart(String key) {
        return this.getPart(key, Date.class);
    }

    public String getStringPart(String key) {
        return this.getPart(key, String.class);
    }

    /**
     * remove value
     *
     * @param key target key
     * @return self
     */
    public Data exclude(String key) {
        super.remove(key);
        super.remove(KeyWeapons.convert(key));
        super.remove(KeyWeapons.convertLine(key));
        return this;
    }

    /**
     * remove field
     *
     * @param function lambda field collection function
     * @param <T>      type
     * @return self
     */
    @SafeVarargs
    public final <T> Data exclude(FunctionalFieldCollector<T>... function) {
        for (FunctionalFieldCollector<T> target : function) {
            String key = LambdaUtils.fieldName(target);
            if (Strings.isNullOrEmpty(key)) {
                continue;
            }
            this.exclude(key);
        }
        return this;
    }


    /**
     * Remove all keys except for ignore
     * (used to reconstruct the structure of the Data object)
     *
     * @param ignore key set to keep
     * @return self
     */
    public Data retain(String... ignore) {
        Set<String> ignores = new HashSet<>(Arrays.asList(ignore));
        this.keySet().removeIf(key -> !ignores.contains(key));
        return this;
    }

    @SafeVarargs
    public final <T> Data retain(FunctionalFieldCollector<T>... ignore) {
        Set<String> keys = new HashSet<>();
        for (FunctionalFieldCollector<T> tFunctionalFieldCollector : ignore) {
            String name = LambdaUtils.fieldName(tFunctionalFieldCollector);
            keys.add(name);
        }
        this.retain(keys.toArray(new String[0]));
        return this;
    }


    /**
     * remove data of a certain class based on key
     *
     * @param clazz     target class
     * @param enablePee whether to parse @ProcessExtraExpression
     * @return self
     */
    public Data exclude(Class<?> clazz, boolean enablePee) {
        Field[] declaredFields = clazz.getDeclaredFields();
        ProcessExtraExpression processExtraExpression = clazz.getDeclaredAnnotation(ProcessExtraExpression.class);
        for (Field declaredField : declaredFields) {
            if (enablePee && null != processExtraExpression) {
                this.exclude(processExtraExpression.prefix()
                        .concat(declaredField.getName()).concat(processExtraExpression.suffix()));
            } else {
                this.exclude(declaredField.getName());
            }
        }
        return this;
    }

    /**
     * remove data not parsed  @ProcessExtraExpression
     *
     * @param clazz target class
     * @return self
     */
    public Data exclude(Class<?> clazz) {
        return exclude(clazz, false);
    }


    /**
     * use with java 8's optional
     *
     * @param key    target key
     * @param target target type
     * @param <T>    target generics
     * @return result（Optional）
     */
    public <T> Optional<T> getOptionalPart(String key, Class<T> target) {
        return Optional.ofNullable(getPart(key, target));
    }

    @Override
    public Byte getBytePart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (Byte.class.isAssignableFrom(clazz)) {
            return (Byte) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Double.valueOf(o.toString()).byteValue();
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).byteValue();
        }
        return null;
    }

    @Override
    public Short getShortPart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (Short.class.isAssignableFrom(clazz)) {
            return (Short) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Double.valueOf(o.toString()).shortValue();
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).shortValue();
        }
        return null;
    }

    @Override
    public Integer getIntegerPart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (Integer.class.isAssignableFrom(clazz)) {
            return (Integer) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Double.valueOf(o.toString()).intValue();
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).intValue();
        }
        return null;
    }

    @Override
    public Long getLongPart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();

        if (Long.class.isAssignableFrom(clazz)) {
            return (Long) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Double.valueOf(o.toString()).longValue();
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).longValue();
        }
        return null;
    }

    @Override
    public Float getFloatPart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (Float.class.isAssignableFrom(clazz)) {
            return (Float) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Float.parseFloat((String) o);
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).floatValue();
        }

        return null;
    }

    @Override
    public Double getDoublePart(String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (Double.class.isAssignableFrom(clazz)) {
            return (Double) o;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return Double.parseDouble((String) o);
        } else if (Number.class.isAssignableFrom(clazz)) {
            return ((Number) o).doubleValue();
        }
        return null;
    }

    @Override
    public Boolean getBooleanPart(String key) {
        Object o = get(key);
        if (null != o) {
            Class<?> clazz = o.getClass();
            if (Boolean.class.isAssignableFrom(clazz)) {
                return (Boolean) o;
            } else if (String.class.isAssignableFrom(clazz)) {
                return Boolean.parseBoolean((String) o);
            }
        }
        return null;
    }

    @Override
    public Character getCharPart(String key) {
        Object o = get(key);
        if (null != o) {
            Class<?> clazz = o.getClass();
            if (Character.class.isAssignableFrom(clazz)) {
                return (Character) o;
            }
            // if it is a string of length one, it can also be converted to CHAR
            if (String.class.isAssignableFrom(clazz)) {
                String tmp = (String) o;
                if (tmp.length() == SINGE_LEN_STR) {
                    return tmp.charAt(0);
                }
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getListPart(String key, Class<T> tClass, boolean deep) {
        if (isPresent(key)) {
            Object o = get(key);
            if (null == o) {
                return null;
            }
            try {
                if (List.class.isAssignableFrom(o.getClass())) {
                    List list = (List) o;
                    if (list.size() > 0) {
                        Object firstItem = list.get(0);
                        Class<?> listClazz = firstItem.getClass();
                        if (Object.class.equals(listClazz)) {
                            //noinspection unchecked
                            return list;
                        }
                        if (tClass.equals(listClazz)) {
                            //noinspection unchecked
                            return (List<T>) o;
                        }
                        // if deep conversion is turned on,
                        // need to continue to judge the type
                        if (deep) {
                            if (Map.class.isAssignableFrom(listClazz)) {
                                List<T> res = new ArrayList<>();
                                for (Object item : list) {
                                    Data data;
                                    if (Data.class.isAssignableFrom(listClazz)) {
                                        data = (Data) item;
                                    } else {
                                        //noinspection unchecked
                                        data = Data.from((Map<String, Object>) item);
                                    }
                                    T t = data.get(tClass, true);
                                    res.add(t);
                                }
                                if (memoryOptimization) {
                                    this.remove(key);
                                }
                                return res;
                            }
                        }
                    } else {
                        // noinspection unchecked
                        return list;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * get the reflection type under a single key
     * if the target object is a list use getListPart
     *
     * @param key    parentKey type stored in map
     * @param tClass target class
     * @param deep   whether to perform deep conversion
     * @param <T>    target type
     * @return target
     */
    @Override
    public <T> T getPart(String key, Class<T> tClass, boolean deep) {
        if (BASIC_WRAPPED_TYPES.contains(tClass)) {
            return this.dispatch(key, tClass);
        }
        Object o = get(key);
        if (null == o) {
            return null;
        }
        if (tClass.isAssignableFrom(o.getClass())) {
            return tClass.cast(o);
        }
        return deep ? this.deepTrans(tClass, key) : null;
    }

    private <T> T deepTrans(Class<T> tClass, String key) {
        Object o = get(key);
        if (null == o) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (!List.class.isAssignableFrom(clazz)) {
            Data temp = null;
            if (Data.class.isAssignableFrom(clazz)) {
                temp = (Data) o;
            } else if (Map.class.isAssignableFrom(clazz)) {
                // noinspection unchecked
                temp = Data.from((Map<String, Object>) o);
            }
            T res = null == temp ? null : temp.get(tClass, true);
            if (this.memoryOptimization) {
                this.remove(key);
            }
            return res;
        }
        return null;
    }

    private <T> T dispatch(String key, Class<T> tClass) {
        if (Byte.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getBytePart(key));
        }
        if (Integer.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getIntegerPart(key));
        }
        if (Long.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getLongPart(key));
        }
        if (Float.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getFloatPart(key));
        }
        if (Double.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getDoublePart(key));
        }
        if (Boolean.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getBooleanPart(key));
        }
        if (Character.class.isAssignableFrom(tClass)) {
            return tClass.cast(this.getCharPart(key));
        }
        return null;
    }


    @Override
    public <T> T get(String prefix, String suffix, Class<T> tClass, boolean deep) {
        Optional<T> instance = EntityWeapons.instance(tClass);
        if (instance.isPresent()) {
            Field[] fields = EntityWeapons.getAllDeclaredFields(tClass);
            T target = instance.get();
            for (Field field : fields) {
                parseField(field, target, deep, prefix, suffix);
            }
            return target;
        }
        return null;
    }

    public boolean isPresent(String value) {
        return super.containsKey(KeyWeapons.convert(value))
                || super.containsKey(KeyWeapons.convertLine(value));
    }

    private <T> void parseField(Field field, T target, boolean deep, String prefix, String suffix) {
        field.setAccessible(true);
        // non collection object parsing injection
        Class<?> type = field.getType();
        String fieldName = field.getName();
        if (!List.class.isAssignableFrom(type)) {
            String realKey = prefix.concat(fieldName).concat(suffix);
            if (!this.isPresent(realKey)) {
                return;
            }
            try {
                field.set(target, getPart(realKey, type, deep));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                field.set(target, getListPart(fieldName, EntityWeapons.getListGenericType(field), deep));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        field.setAccessible(false);
    }
}
