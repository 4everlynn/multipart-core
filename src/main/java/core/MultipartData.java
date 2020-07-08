package core;

import annotion.JoinToParentPart;
import annotion.ProcessExtraExpression;
import support.IMultipart;
import weapon.EntityWeapons;
import weapon.KeyWeapons;
import weapon.Strings;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019-12-27
 */
public class MultipartData extends Data implements IMultipart {

    private static <T> MultiPartArray merge(MultiPartArray data, Class<T> entranceClass, boolean deep, String lastNodeKey) {
        Field[] fields = EntityWeapons.getAllDeclaredFields(entranceClass);
        Map<String, Field> fieldMap = new HashMap<>(5);
        for (Field field : fields) {
            field.setAccessible(true);
            JoinToParentPart declaredAnnotation = field.getDeclaredAnnotation(JoinToParentPart.class);
            if (null != declaredAnnotation) {
                fieldMap.put(field.getName(), field);
            }
            field.setAccessible(false);
        }
        if (fieldMap.size() == 0) {
            // unpack and return the real entity field
            for (MultipartData multipartData : data) {
                T t = multipartData.get(entranceClass);
                multipartData.exclude(entranceClass);
                multipartData.part(false, t);
            }
            return data;
        }
        return mergeProcess(fieldMap, entranceClass, data, deep, lastNodeKey);
    }

    public static <T> MultiPartArray merge(MultiPartArray data, Class<T> entranceClass, boolean deep) {
        return merge(data, entranceClass, deep, "");
    }

    private static <T> MultiPartArray mergeProcess(Map<String, Field> fieldMap, Class<T> entranceClass,
                                                   List<MultipartData> data, boolean deep, String lastNodeKey) {
        MultiPartArray res = new MultiPartArray();
        for (String fieldName : fieldMap.keySet()) {
            Field v = fieldMap.get(fieldName);
            v.setAccessible(true);
            JoinToParentPart joinParentPart = v.getDeclaredAnnotation(JoinToParentPart.class);
            Class<?> parentClass = joinParentPart.value();
            String key = joinParentPart.parentKey();
            if (Strings.isNullOrEmpty(key)) {
                key = fieldName;
            }
            final String finalKey = key;
            MultiPartArray temporary = res;
            data.stream()
                    .collect(Collectors.groupingBy(item -> {
                        Object o = item.get(finalKey);
                        return null == o ? "default" : o;
                    }))
                    .forEach((pK, children) -> {
                        if (children.size() > 0) {
                            MultipartData multipartData = new MultipartData();
                            MultipartData basicData = children.get(0).translate2Camel(true);
                            if (!deep) {
                                // inject parent
                                multipartData.part(false, getT(parentClass, basicData));
                            } else {
                                multipartData.parties(basicData);
                            }
                            multipartData.exclude(entranceClass, true);
                            if (!"".equals(lastNodeKey)) {
                                multipartData.exclude(lastNodeKey);
                            }
                            for (MultipartData child : children) {
                                child.translate2Camel(true);
                                T t = getT(entranceClass, child);
                                String processKey;
                                if ("".equals(lastNodeKey)) {
                                    processKey = joinParentPart.nodeKey();
                                } else {
                                    processKey = lastNodeKey;
                                }
                                Object c = child.get(processKey);
                                child.clear();
                                // unpack and return the real entity field
                                child.part(true, t);
                                if (null != c) {
                                    child.include(processKey, c);
                                }
                            }
                            multipartData.include(joinParentPart.nodeKey(), children);
                            temporary.add(multipartData);
                        }
                    });
            v.setAccessible(false);
            if (deep) {
                res = merge(temporary, parentClass, true, joinParentPart.nodeKey());
                return res;
            }
        }
        return res;
    }

    private static <T> T getT(Class<T> entranceClass, MultipartData multipartData) {
        T t;
        ProcessExtraExpression processExtraExpression
                = entranceClass.getDeclaredAnnotation(ProcessExtraExpression.class);
        if (null != processExtraExpression) {
            t = multipartData.get(processExtraExpression.prefix(),
                    processExtraExpression.suffix(), entranceClass, false);
        } else {
            t = multipartData.get(entranceClass);
        }
        return t;
    }

    @Override
    public MultipartData part(boolean unpack, Object... targets) {
        for (Object target : targets) {
            if (Map.class.isAssignableFrom(target.getClass())) {
                Map map = (Map) target;
                //noinspection unchecked
                this.putAll(map);
                this.translate2Camel(true);
            } else {
                this.dissect(unpack, target);
            }
        }
        return this;
    }

    @Override
    public MultipartData parties(Object... targets) {
        part(true, targets);
        return this;
    }

    private void dissect(boolean unpack, Object target) {
        Field[] declaredFields = EntityWeapons.getAllDeclaredFields(target.getClass());
        Object current;
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                String key;
                key = declaredField.getName();
                // get current value
                current = declaredField.get(target);
                if (unpack) {
                    this.resolveExtraExpression(key, target.getClass(), current);
                } else {
                    this.include(key, current);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            declaredField.setAccessible(false);
        }
    }

    private void resolveExtraExpression(String original, Class<?> tClass, Object target) {
        ProcessExtraExpression extraExpression = tClass
                .getDeclaredAnnotation(ProcessExtraExpression.class);
        if (null != extraExpression) {
            include(KeyWeapons.convertLine(extraExpression.prefix()
                    .concat(original).concat(extraExpression.suffix())), target);
        } else {
            include(original, target);
        }
    }

    @Override
    public MultipartData include(String key, Object value) {
        super.include(key, value);
        return this;
    }

    @Override
    public MultipartData exclude(String key) {
        super.exclude(key);
        return this;
    }

    public MultipartData translate2Camel(boolean deep) {
        MultipartData data = new MultipartData();
        for (String key : this.keySet()) {
            Object o = get(key);
            if (deep) {
                if (null == o) {
                    String targetKey = KeyWeapons.convert(key);
                    data.put(targetKey, null);
                } else if (MultipartData.class.isAssignableFrom(o.getClass())) {
                    String targetKey = KeyWeapons.convert(key);
                    data.put(targetKey, ((MultipartData) o).translate2Camel(true));
                } else {
                    String targetKey = KeyWeapons.convert(key);
                    data.put(targetKey, o);
                }
            } else {
                String targetKey = KeyWeapons.convert(key);
                data.put(targetKey, o);
            }
        }
        return data;
    }

    public MultipartData translate2Camel() {
        return translate2Camel(false);
    }

    @Override
    public <T> T get(Class<T> tClass, boolean deep, String... paths) {
        if (paths.length == 1) {
            return getPart(paths[0], tClass, deep);
        }
        MultipartData temp = new MultipartData();
        Object obj;
        int index = 0;
        for (String path : paths) {
            if (index == paths.length - 1) {
                T part = temp.getPart(path, tClass, deep);
                //noinspection UnusedAssignment
                temp = null;
                return part;
            }
            if (temp.size() == 0) {
                obj = get(path);
            } else {
                obj = temp.get(path);
            }
            temp.parties(obj);
            index++;
        }
        return null;
    }
}
