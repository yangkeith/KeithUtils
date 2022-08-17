package io.github.yangkeith.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * <p>Title: JsonUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:11
 */
public class JsonUtil {
    
    /**
     * json2 jsonobject
     *
     * @param json json
     * @return {@link JSONObject }
     * @author Keith
     * @date 2022-08-17
     */
    public static JSONObject json2JSONObject(String json){
        return JSONObject.parseObject(json);
    }
    
    /**
     * json实体
     *
     * @param clazz clazz
     * @param json  json
     * @return {@link T }
     * @author Keith
     * @date 2022-08-17
     */
    public static <T> T jsonToEntity(Class<T> clazz, String json){
        return JSON.parseObject(json, clazz);
    }
    
    /**
     * 获取json价值
     *
     * @param json json
     * @param key  关键
     * @return {@link Object }
     * @author Keith
     * @date 2022-08-17
     */
    public static Object getJsonValue(String json,String key){
        return json2JSONObject(json).get(key);
    }
    
    /**
     * json2 json数组
     *
     * @param json json
     * @return {@link JSONArray }
     * @author Keith
     * @date 2022-08-17
     */
    public static JSONArray json2JsonArray(String json){
        return JSON.parseArray(json);
    }
}
