package top.keith.yang.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Title: ArrayUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:01
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ArrayUtil {
    /**
     * 判断数组是否不为空
     * @param list 数组
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> list) {
        return list != null && list.size() > 0;
    }
    /**
     * 判断Map是否不为空
     * @param map Map
     * @return boolean
     */
    public static boolean isNotEmpty(Map map) {
        return map != null && map.size() > 0;
    }
    
    public static boolean isNotEmpty(Object[] objects) {
        return objects != null && objects.length > 0;
    }
    
    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }
    
    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.size() == 0;
    }
    
    public static boolean isNullOrEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }
    
    public static <T> T[] concat(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    
    public static <T> boolean contains(T[] array, T element) {
        if (array == null || array.length == 0) {
            return false;
        }
        
        for (T t : array) {
            if (Objects.equals(t, element)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSameElements(Collection<?> c1, Collection<?> c2) {
        if (c1 == c2) {
            return true;
        }
        
        if ((c1 == null || c1.isEmpty()) && (c2 == null || c2.isEmpty())) {
            return true;
        }
        
        if (c1 != null && c2 != null && c1.size() == c2.size() && c1.containsAll(c2)) {
            return true;
        }
        
        return false;
    }
}
