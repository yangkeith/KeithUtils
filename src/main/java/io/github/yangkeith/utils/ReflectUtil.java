package io.github.yangkeith.utils;

import com.jfinal.kit.LogKit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p>Title: ReflectUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:21
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ReflectUtil {
    public static <T> T getStaticFieldValue(Class<?> dClass, String fieldName) {
        return getFieldValue(dClass, fieldName, null);
    }
    
    public static <T> T getFieldValue(Object getFrom, String fieldName) {
        return getFieldValue(getFrom.getClass(), fieldName, getFrom);
    }
    
    private static <T> T getFieldValue(Class<?> dClass, String fieldName, Object getFrom) {
        try {
            if (StrUtils.isBlank(fieldName)) {
                throw new IllegalArgumentException("fieldName must not be null or empty.");
            }
            Field field = searchField(dClass, f -> f.getName().equals(fieldName));
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            
            return getFileValue(getFrom, field);
            
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static <T> T getFileValue(Object getFrom, Field field) {
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return (T) field.get(getFrom);
        } catch (IllegalAccessException e) {
            LogKit.error(e.toString(), e);
        } finally {
            field.setAccessible(accessible);
        }
        return null;
    }
    
    
    public static void setStaticFieldValue(Class<?> dClass, String fieldName, Object value) {
        setFieldValue(dClass, null, fieldName, value);
    }
    
    
    public static void setFieldValue(Object setTo, String fieldName, Object value) {
        setFieldValue(setTo.getClass(), setTo, fieldName, value);
    }
    
    
    private static void setFieldValue(Class<?> dClass, Object setTo, String fieldName, Object value) {
        setFieldValue(dClass, setTo, f -> f.getName().equals(fieldName), value);
    }
    
    
    private static void setFieldValue(Class<?> dClass, Object setTo, Predicate<Field> filter, Object value) {
        Field field = searchField(dClass, filter);
        if (field == null) {
            throw new IllegalArgumentException("No such field");
        }
        
        setFieldValue(setTo, value, field);
    }
    
    public static void setFieldValue(Object setTo, Object value, Field field) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(setTo, value);
        } catch (IllegalAccessException e) {
            LogKit.error(e.toString(), e);
        } finally {
            field.setAccessible(accessible);
        }
    }
    
    
    public static Field searchField(Class<?> dClass, Predicate<Field> filter) {
        if (dClass == null) {
            return null;
        }
        Field[] fields = dClass.getDeclaredFields();
        for (Field field : fields) {
            if (filter.test(field)) {
                return field;
            }
        }
        return searchField(dClass.getSuperclass(), filter);
    }
    
    
    public static List<Field> searchFieldList(Class<?> dClass, Predicate<Field> filter) {
        List<Field> fields = new LinkedList<>();
        doSearchFieldList(dClass, filter, fields);
        return fields;
    }
    
    
    private static void doSearchFieldList(Class<?> dClass, Predicate<Field> filter, List<Field> searchToList) {
        if (dClass == null || dClass == Object.class) {
            return;
        }
        
        Field[] fields = dClass.getDeclaredFields();
        if (fields.length > 0) {
            if (filter != null) {
                for (Field field : fields) {
                    if (filter.test(field)) {
                        searchToList.add(field);
                    }
                }
            } else {
                searchToList.addAll(Arrays.asList(fields));
            }
        }
        
        doSearchFieldList(dClass.getSuperclass(), filter, searchToList);
    }
    
    
    public static Method searchMethod(Class<?> dClass, Predicate<Method> filter) {
        if (dClass == null) {
            return null;
        }
        Method[] methods = dClass.getDeclaredMethods();
        for (Method method : methods) {
            if (filter.test(method)) {
                return method;
            }
        }
        return searchMethod(dClass.getSuperclass(), filter);
    }
    
    
    public static List<Method> searchMethodList(Class<?> dClass, Predicate<Method> filter) {
        List<Method> methods = new LinkedList<>();
        doSearchMethodList(dClass, filter, methods);
        return methods;
    }
    
    
    private static void doSearchMethodList(Class<?> dClass, Predicate<Method> filter, List<Method> searchToList) {
        if (dClass == null) {
            return;
        }
        Method[] methods = dClass.getDeclaredMethods();
        if (methods.length > 0) {
            if (filter != null) {
                for (Method method : methods) {
                    if (filter.test(method)) {
                        searchToList.add(method);
                    }
                }
            } else {
                searchToList.addAll(Arrays.asList(methods));
            }
        }
        
        doSearchMethodList(dClass.getSuperclass(), filter, searchToList);
    }
    
    
    public static <T> T invokeStaticMethod(Class<?> dClass, String methodName, Object... args) {
        return invokeStaticMethod(dClass, m -> m.getName().equals(methodName), args);
    }
    
    
    public static <T> T invokeStaticMethod(Class<?> dClass, Predicate<Method> filter, Object... args) {
        Method method = searchMethod(dClass, filter);
        if (method == null) {
            throw new IllegalArgumentException("No such method.");
        }
        return invokeMethod(null, method, args);
    }
    
    
    public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
        return invokeMethod(obj, m -> m.getName().equals(methodName), args);
    }
    
    
    public static <T> T invokeMethod(Object obj, Predicate<Method> filter, Object... args) {
        Method method = searchMethod(obj.getClass(), filter);
        if (method == null) {
            throw new IllegalArgumentException("No such method.");
        }
        return invokeMethod(obj, method, args);
    }
    
    
    public static <T> T invokeMethod(Object obj, Method method, Object... args) {
        final boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogKit.error(e.toString(), e);
        } finally {
            method.setAccessible(accessible);
        }
        return null;
    }
}
