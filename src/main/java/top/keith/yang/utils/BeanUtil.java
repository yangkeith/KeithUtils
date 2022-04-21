package top.keith.yang.utils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: BeanUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/13 16:53
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/13  Keith  初始创建
 * -----------------------------------------------
 */
public class BeanUtil {
    
    
    /**
     * 对象转成Map（利用反射）
     *
     * @param obj 要转换的对象
     * @return Map
     * @throws IllegalAccessException
     */
    public static Map<String, Object> bean2Map(Object obj) throws IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        Class<? extends Object> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            result.put(field.getName(), field.get(obj));
        }
        return result;
    }
    
    /**
     * Map转成对象
     * @param bean 空对象或要被覆盖的对象
     * @param map Map数据
     * @param <T> 任意对象
     * @return 返回Bean
     * @throws IllegalAccessException
     */
    public static <T> Object map2Bean(T bean, Map<?, ?> map) throws IllegalAccessException {
        if (map == null) {
            return null;
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(bean, map.get(field.getName()));
        }
        return bean;
    }
    
    /**
     * Map转对象
     * @param object 空对象或要被覆盖的对象
     * @param map Map数据
     * @return 返回Bean
     */
    public static Object mapToObject(Object object, Map<?, ?> map) {
        BeanMap beanMap = BeanMap.create(object);
        beanMap.putAll(map);
        return object;
    }
    
    /**
     * 对象转成Map（利用反射）
     * @param obj 要转换的对象
     * @return Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj != null) {
            BeanMap beanMap = BeanMap.create(obj);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }
    
    /**
     * 属性复制
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        beanCopier.copy(source, target, null);
    }
    
    /**
     * 获取JPA中配置Table的值以获取对应的表名
     * @param clazz 对象类
     * @return 返回表名
     */
    public static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return null;
    }
    
    /**
     * 获取JPA中配置Column的值以获取对应的字段
     * @param clazz 对象类
     * @return 返回字段名
     */
    public static List<String> getColumns(Class<?> clazz) {
        List<String> columns = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }
        if (columns.size() > 0) {
            return columns;
        }
        return null;
    }
    
    /**
     * 获取JPA中配置Column的值以获取对应的字段和对象的属性对应表
     * @param clazz 对象类
     * @return 字段和对象的属性对应表
     */
    public static Map<String, String> getColumnsMap(Class<?> clazz) {
        Map<String, String> columns = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.put(field.getName(), column.name());
            }
        }
        if (columns.size() > 0) {
            return columns;
        }
        return null;
    }
    
    /**
     * 获取JPA中配置Id的值以获取对应的主键字段
     * @param clazz 对象类
     * @return 字段名
     */
    public static String getPrimaryKey(Class<?> clazz) throws IllegalAccessException {
        String primaryKey = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKey = field.getName();
                break;
            }
        }
        if (primaryKey == null) {
            throw new IllegalAccessException(clazz.getSimpleName() + " has no primary key");
        }
        return primaryKey;
    }
    
    /**
     * 获取JPA中配置Id的值以获取对应的主键字段对应的属性值
     * @param object 对象
     * @return 键字段对应的属性值
     * @throws IllegalAccessException
     */
    public static String getPrimaryKeyValue(Object object) throws IllegalAccessException {
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getName().equalsIgnoreCase(getPrimaryKey(object.getClass()))) {
                return String.valueOf(f.get(object).toString());
            }
        }
        return null;
    }
    
    /**
     * 获取JPA中配置Id的值以获取对应的主键字段对应的属性值数组
     * @param objects 对象数组
     * @return 键字段对应的属性值数组
     * @throws IllegalAccessException
     */
    public static String[] getPrimaryKeyValues(List<Object> objects) throws IllegalAccessException {
        String[] ids = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            ids[i] = getPrimaryKeyValue(objects.get(i));
        }
        return ids;
    }
    
    /**
     * 清除对象的主键值
     * @param object 对象
     * @throws IllegalAccessException
     */
    public static void clearPrimaryKey(Object object) throws IllegalAccessException {
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getName().equalsIgnoreCase(getPrimaryKey(object.getClass()))) {
                Integer i = null;
                f.set(object, i);
            }
        }
    }
    
    /**
     * 给对象添加当前时间（自动获取时间字段）
     * @param object 要添加时间的对象
     * @throws IllegalAccessException
     */
    public static void addNow(Object object) throws IllegalAccessException {
        boolean flag = false;
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().equals(Date.class)) {
                flag = true;
                java.util.Date now = new java.util.Date();
                Date date = new Date(now.getTime());
                f.set(object, date);
            }
        }
        if (!flag) {
            throw new IllegalAccessException("No field type of " + object.getClass().getSimpleName() + " is  java.util.Date");
        }
    }
    
    /**
     * 给对象添加指定字段当前时间
     * @param object 要添加时间的对象
     * @param fieldName 字段名字
     * @throws IllegalAccessException
     */
    public static void addNow(Object object, String fieldName) throws IllegalAccessException {
        boolean flag = false;
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getName().equalsIgnoreCase(fieldName)) {
                flag = true;
                if (f.getType().equals(Date.class)) {
                    java.util.Date now = new java.util.Date();
                    Date date = new Date(now.getTime());
                    f.set(object, date);
                } else {
                    throw new IllegalAccessException("The type of the " + fieldName + " field is not a java.util.Date");
                }
            }
        }
        if (!flag) {
            throw new IllegalAccessException(fieldName + " field does not exit");
        }
    }
    
    /**
     * 给当前对象的指定字段添加值
     * @param object 需要添加值的对象
     * @param fieldName 属性名
     * @param value 添加的值
     * @throws IllegalAccessException
     */
    public static void addField(Object object, String fieldName, Object value) throws IllegalAccessException {
        boolean flag = false;
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getName().equalsIgnoreCase(fieldName)) {
                flag = true;
                f.set(object, value);
            }
        }
        if (!flag) {
            throw new IllegalAccessException(fieldName + " field does not exit");
        }
    }
}
