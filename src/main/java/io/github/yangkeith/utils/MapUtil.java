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
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/12  Keith  初始创建
 * -----------------------------------------------
 */
public class MapUtil {
    
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
    
    public static Map jsonToMap(String json) {
        return JSON.parseObject(json,Map.class);
    }
    
}
