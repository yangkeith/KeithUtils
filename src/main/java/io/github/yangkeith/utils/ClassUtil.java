package io.github.yangkeith.utils;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import io.github.yangkeith.dto.ClassType;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: ClassUtil</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:05
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ClassUtil {
    private static Log LOG = Log.getLog(ClassUtil.class);
    
    private static final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    
    
    /**
     * 获取单例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T singleton(Class<T> clazz) {
        return singleton(clazz, true);
    }
    
    /**
     * 获取单利
     *
     * @param clazz
     * @param createByAop
     * @param <T>
     * @return
     */
    public static synchronized <T> T singleton(Class<T> clazz, boolean createByAop) {
        Object ret = singletons.get(clazz);
        if (ret == null) {
            ret = newInstance(clazz, createByAop);
            if (ret != null) {
                singletons.put(clazz, ret);
            } else {
                LOG.error("cannot new newInstance!!!!");
            }
        }
        return (T) ret;
    }
    
    public static synchronized <T> T singleton(Class<T> clazz, boolean createByAop, boolean inject) {
        Object ret = singletons.get(clazz);
        if (ret == null) {
            ret = newInstance(clazz, createByAop);
            if (ret != null) {
                if (inject && !createByAop) {
                    Aop.inject(ret);
                }
                singletons.put(clazz, ret);
            } else {
                LOG.error("cannot new newInstance!!!!");
            }
        }
        return (T) ret;
    }
    
    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, true);
    }
    
    
    /**
     * 创建新的实例，并传入初始化参数
     *
     * @param clazz
     * @param paras
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz, Object... paras) {
        return newInstance(clazz, true, paras);
    }
    
    
    /**
     * 是否通过 AOP 来实例化
     *
     * @param clazz
     * @param createByAop
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz, boolean createByAop) {
        if (createByAop) {
            return Aop.get(clazz);
        } else {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e) {
                LOG.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
            }
            
            return null;
        }
    }
    
    
    public static <T> T newInstance(Class<T> clazz, boolean createByAop, Object... paras) {
        try {
            Class<?>[] classes = new Class[paras.length];
            for (int i = 0; i < paras.length; i++) {
                Object data = paras[i];
                if (data == null) {
                    throw new IllegalArgumentException("paras must not null");
                }
                classes[i] = data.getClass();
            }
            Constructor<T> constructor = clazz.getDeclaredConstructor(classes);
            constructor.setAccessible(true);
            Object ret = constructor.newInstance(paras);
            if (createByAop) {
                Aop.inject(ret);
            }
            return (T) ret;
        } catch (Exception e) {
            LOG.error("can not newInstance class:" + clazz + "\n" + e.toString(), e);
        }
        
        return null;
    }
    
    
    private static Method getStaticConstruct(String name, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())
                    && method.getReturnType() == clazz) {
                if (StrUtils.isBlank(name)) {
                    return method;
                } else if (name.equals(method.getName())) {
                    return method;
                }
            }
        }
        return null;
    }
    
    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazzName
     * @return
     */
    public static <T> T newInstance(String clazzName) {
        return newInstance(clazzName, true);
    }
    
    /**
     * 创建新的实例
     *
     * @param <T>
     * @param clazzName
     * @return
     */
    public static <T> T newInstance(String clazzName, boolean createByAop) {
        return newInstance(clazzName, createByAop, Thread.currentThread().getContextClassLoader());
    }
    
    
    /**
     * 创建新的实例
     *
     * @param clazzName
     * @param createByAop
     * @param classLoader
     * @param <T>
     * @return
     */
    public static <T> T newInstance(String clazzName, boolean createByAop, ClassLoader classLoader) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName, false, classLoader);
            return newInstance(clazz, createByAop);
        } catch (Exception e) {
            LOG.error("can not newInstance class:" + clazzName + "\n" + e.toString(), e);
        }
        
        return null;
    }
    
    
    private static final String ENHANCER_BY = "$$EnhancerBy";
    private static final String JAVASSIST_BY = "_$$_";
    
    public static Class<?> getUsefulClass(Class<?> clazz) {
        final String name = clazz.getName();
        if (name.contains(ENHANCER_BY) || name.contains(JAVASSIST_BY)){
            return clazz.getSuperclass();
        }
        return clazz;
    }
    
    
    public static ClassType getClassType(Type type, Class<?> runClass) {
        if (type instanceof Class) {
            return new ClassType((Class<?>) type);
        }
        // 泛型定义在参数里，例如 List<String>
        else if (type instanceof ParameterizedType) {
            ClassType classType = new ClassType((Class<?>) ((ParameterizedType) type).getRawType());
            
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            ClassType[] genericTypes = new ClassType[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                genericTypes[i] = getClassType(actualTypeArguments[i], runClass);
            }
            
            classType.setGenericTypes(genericTypes);
            return classType;
        }
        //泛型定义在 class 里，例如 List<T>，其中 T 是在 class 里的参数
        else if (type instanceof TypeVariable && runClass != null) {
            Type variableRawType = getTypeInClassDefined(runClass, ((TypeVariable<?>) type));
            if (variableRawType != null) {
                return getClassType(variableRawType, runClass);
            } else {
                return null;
            }
        }
        
        return null;
    }
    
    
    private static Type getTypeInClassDefined(Class<?> runClass, TypeVariable<?> typeVariable) {
        Type type = runClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArguments.length == 1) {
                return typeArguments[0];
            } else if (typeArguments.length > 1) {
                TypeVariable<?>[] typeVariables = typeVariable.getGenericDeclaration().getTypeParameters();
                for (int i = 0; i < typeVariables.length; i++) {
                    if (typeVariable.getName().equals(typeVariables[i].getName())) {
                        return typeArguments[i];
                    }
                }
            }
        }
        return null;
    }
    
    
    public static String buildMethodString(Method method) {
        
        StringBuilder sb = new StringBuilder()
                .append(method.getDeclaringClass().getName())
                .append(".")
                .append(method.getName())
                .append("(");
        
        Class<?>[] params = method.getParameterTypes();
        int in = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++in < params.length) {
                sb.append(",");
            }
        }
        
        return sb.append(")").toString();
    }
    
    
    public static boolean hasClass(String className) {
        try {
            Class.forName(className, false, getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    
    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : ClassUtil.class.getClassLoader();
    }
}
