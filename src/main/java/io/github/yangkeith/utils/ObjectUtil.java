package io.github.yangkeith.utils;

import io.github.yangkeith.interfaces.ObjectFunc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * <p>Title: ObjectUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:19
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ObjectUtil {
    /**
     * 判断 某个 objects 集合里是否包含了某个 object
     *
     * @param objects            object 集合
     * @param compareObject      是否被包 集合 含的对比 object
     * @param compareAttrGetters 需要对比的 getter
     * @param <T>
     * @return true 包含，false 不包含
     */
    public static <T> boolean isContainsObject(Collection<T> objects, T compareObject, ObjectFunc<T>... compareAttrGetters) {
        if (objects == null || objects.isEmpty() || compareObject == null) {
            return false;
        }
        
        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }
        
        
        for (T object : objects) {
            if (isSameObject(object, compareObject, compareAttrGetters)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * 获取 某个 objects 集合里包含的 object
     *
     * @param objects            object 集合
     * @param compareObject      是否被包 集合 含的对比 object
     * @param compareAttrGetters 需要对比的 getter
     * @param <T>
     * @return 返回 objects 结合中对比成功的 object
     */
    public static <T> T getContainsObject(Collection<T> objects, T compareObject, ObjectFunc<T>... compareAttrGetters) {
        if (objects == null || objects.isEmpty() || compareObject == null) {
            return null;
        }
        
        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }
        
        
        for (T object : objects) {
            if (isSameObject(object, compareObject, compareAttrGetters)) {
                return object;
            }
        }
        
        return null;
    }
    
    
    /**
     * 判断两个 Object 是否是同一个 Object，根据传入的 getter 来进行对比
     *
     * @param object1
     * @param object2
     * @param compareAttrGetters
     * @param <T>
     * @return
     */
    public static <T> boolean isSameObject(T object1, T object2, ObjectFunc<T>... compareAttrGetters) {
        if (object1 == null || object2 == null) {
            return object1 == object2;
        }
        
        if (compareAttrGetters == null || compareAttrGetters.length == 0) {
            throw new IllegalArgumentException("compareAttrGetters must not be null");
        }
        
        
        for (ObjectFunc getter : compareAttrGetters) {
            
            if (getter == null) {
                throw new IllegalArgumentException("compareAttrGetter must not be null");
            }
            
            
            if (!Objects.equals(getter.get(object1), getter.get(object2))) {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * 判断两个 Object 是否是同一个 Object，根据传入的 getter 来进行对比
     *
     * @param object1
     * @param object2
     * @param compareAttrGetters
     * @param <T>
     * @return
     */
    public static <T> boolean notSameObject(T object1, T object2, ObjectFunc<T>... compareAttrGetters) {
        return !isSameObject(object1, object2, compareAttrGetters);
    }
    
    
    public static Object convert(Object value, Class<?> targetClass) {
        if (value.getClass().isAssignableFrom(targetClass)) {
            return value;
        }
        if (targetClass == String.class) {
            return value.toString();
        } else if (targetClass == Integer.class || targetClass == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } else if (targetClass == Long.class || targetClass == long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        } else if (targetClass == Double.class || targetClass == double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString());
        } else if (targetClass == Float.class || targetClass == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.parseFloat(value.toString());
        } else if (targetClass == Boolean.class || targetClass == boolean.class) {
            String v = value.toString().toLowerCase();
            if ("1".equals(v) || "true".equals(v)) {
                return Boolean.TRUE;
            } else if ("0".equals(v) || "false".equals(v)) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Can not parse to boolean type of value: \"" + value + "\"");
            }
        } else if (targetClass == java.math.BigDecimal.class) {
            return new java.math.BigDecimal(value.toString());
        } else if (targetClass == java.math.BigInteger.class) {
            return new java.math.BigInteger(value.toString());
        } else if (targetClass == byte[].class) {
            return value.toString().getBytes();
        } else if (targetClass == Date.class) {
            return parseDate(value);
        } else if (targetClass == LocalDateTime.class) {
            return DateUtil.toLocalDateTime(parseDate(value));
        } else if (targetClass == LocalDate.class) {
            return DateUtil.toLocalDate(parseDate(value));
        } else if (targetClass == LocalTime.class) {
            return DateUtil.toLocalTime(parseDate(value));
        } else if (targetClass == Short.class || targetClass == short.class) {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            return Short.parseShort(value.toString());
        }
        
        throw new RuntimeException("\"" + targetClass.getName() + "\" can not be parsed.");
    }
    
    
    private static Date parseDate(Object value) {
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        
        String s = value.toString();
        if (StrUtils.isNumeric(s)) {
            return new Date(Long.parseLong(s));
        }
        return DateUtil.parseDate(s);
    }
    
    public static Object getPrimitiveDefaultValue(Class<?> paraClass) {
        if (paraClass == int.class || paraClass == long.class || paraClass == float.class || paraClass == double.class) {
            return 0;
        } else if (paraClass == boolean.class) {
            return Boolean.FALSE;
        } else if (paraClass == short.class) {
            return (short) 0;
        } else if (paraClass == byte.class) {
            return (byte) 0;
        } else if (paraClass == char.class) {
            return '\u0000';
        } else {
            //不存在这种类型
            return null;
        }
    }
}
