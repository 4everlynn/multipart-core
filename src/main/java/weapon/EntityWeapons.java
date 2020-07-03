package weapon;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-07-03
 */
public class EntityWeapons {
    /**
     * Get optional instance of target class
     *
     * @param target class of instance
     * @param <T>    type
     * @return instance of target class
     */
    public static <T> Optional<T> instance(Class<T> target) {
        T instance = null;
        try {
            instance = target.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(instance);
    }

    /**
     * patch object between origin and target
     *
     * @param origin original obj
     * @param target target obj
     * @param <T>    type
     * @return patched object
     */
    public static <T> T patchable(T origin, T target) {
        if (origin == null && target != null) {
            return target;
        }
        if (origin == null) {
            return null;
        }
        Field[] declaredFields = getAllDeclaredFields(origin.getClass());
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                Object targetValue = declaredField.get(target);
                if (null != targetValue) {
                    // use target value to overwrite origin field value
                    declaredField.set(origin, targetValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            declaredField.setAccessible(false);
        }
        return origin;
    }


    public static Field[] getAllDeclaredFields(Class<?> clazz) {
        Class<?> superclass;
        List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        while ((superclass = clazz.getSuperclass()) != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(superclass.getDeclaredFields())));
            clazz = superclass;
        }
        Field[] res = new Field[fieldList.size()];
        res = fieldList.toArray(res);
        return res;
    }

    public static Class<?> getListGenericType (Field field) {
        Type genericType = field.getGenericType();
        ParameterizedType parameterizedType = toParameterizedType(genericType);
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        try {
            return Class.forName(actualTypeArgument.getTypeName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    public static ParameterizedType toParameterizedType(Type type) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType)type;
        } else {
            return type instanceof Class ? toParameterizedType(((Class)type).getGenericSuperclass()) : null;
        }
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }
}
