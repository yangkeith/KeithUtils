package io.github.yangkeith.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: CollectionUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:08
 */
public class CollectionUtil {
    
    /**
     * String转Map
     *
     * @param s 年代
     * @return {@link Map }<{@link String }, {@link String }>
     * @author Keith
     * @date 2022-08-17
     */
    public static Map<String,String> string2Map(String s){
        Map<String,String> map =  new HashMap();
        String[] strings = s.split(",");
        for (String kv : strings) {
            if (kv != null && kv.contains(":")) {
                String[] keyValue = kv.split(":");
                if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return map;
    }
    
    /**
     * 是空
     *
     * @param collection 集合
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }
}
