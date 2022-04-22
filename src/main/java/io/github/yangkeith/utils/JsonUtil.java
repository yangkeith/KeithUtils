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
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class JsonUtil {
    
    public static JSONObject json2JSONObject(String json){
        return JSONObject.parseObject(json);
    }
    public static <T> T jsonToEntity(Class<T> clazz, String json){
        return JSON.parseObject(json, clazz);
    }
    public static Object getJsonValue(String json,String key){
        return json2JSONObject(json).get(key);
    }
    public static JSONArray json2JsonArray(String json){
        return JSON.parseArray(json);
    }
}
