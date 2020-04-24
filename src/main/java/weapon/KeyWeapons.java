package weapon;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2019/1/9
 */
public class KeyWeapons {

    private static final String SPLIT_STRING = "_";

    /**
     * site_id  -> siteId
     * underline nomenclature camel nomenclature
     *
     * @param key target parentKey
     * @return camel case parentKey
     */
    public static String convert(String key) {
        if (key.contains(SPLIT_STRING)) {
            StringBuilder builder = new StringBuilder();
            Deque<String> queue = new ArrayDeque<>();
            boolean flag = false;
            // 入队 -> 出队 全部完成后就完成了转化
            for (int i = 0; i < key.length(); i++) {
                String currentKey = key.charAt(i) + "";
                if (!SPLIT_STRING.equals(currentKey)) {
                    if (flag) {
                        queue.add(currentKey.toUpperCase());
                        flag = false;
                    } else {
                        queue.add(currentKey);
                    }
                } else {
                    flag = true;
                }
                if (queue.peek() != null) {
                    builder.append(queue.pop());
                }
            }
            return builder.toString();
        }
        return key;
    }

    public static String convertLine(String key) {
        StringBuilder builder = new StringBuilder();
        Deque<String> queue = new ArrayDeque<>();
        for (int i = 0; i < key.length(); i++) {
            String currentKey = key.charAt(i) + "";
            if (Character.isUpperCase(key.charAt(i)) && i > 0) {
                queue.add(SPLIT_STRING);
                queue.add(currentKey.toLowerCase());
            } else {
                queue.add(currentKey);
            }
            while (queue.peek() != null) {
                builder.append(queue.pop());
            }
        }
        return builder.toString();
    }
}
