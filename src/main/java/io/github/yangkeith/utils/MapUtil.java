package io.github.yangkeith.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title: MapUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/12 09:33
 */
public class MapUtil {
    
    /**
     * 转换键值
     *
     * @param map 地图
     * @return {@link Map }<{@link String }, {@link String }>
     * @author Keith
     * @date 2022-08-17
     */
    public static Map<String,String> convertKeyValue(Map<String, String> map){
        Iterator it = map.entrySet().iterator();
        Map<String, String> reverseMap = new HashMap<String, String>();
        while (it.hasNext()) {
            Map.Entry<Integer, String> next = (Map.Entry<Integer, String>)it.next();
            if (reverseMap.containsKey(next.getValue())) {
                String sb = reverseMap.get(next.getValue()) + "_" + next.getKey();
                reverseMap.put(next.getValue(), sb);
            } else {
                reverseMap.put(next.getValue(),next.getKey()+"");
            }
        }
        return reverseMap;
    }
    
    /**
     * json映射
     *
     * @param json json
     * @return {@link Map }
     * @author Keith
     * @date 2022-08-17
     */
    public static Map jsonToMap(String json) {
        return JSON.parseObject(json,Map.class);
    }
    
}
