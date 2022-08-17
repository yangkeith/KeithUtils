package io.github.yangkeith.constant;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: TypeDef</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:29
 */
public class TypeDef<T> {
    /**
     * 字符串列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_STRING = new TypeDef<List<String>>() {};
    /**
     * 整数列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_INTEGER = new TypeDef<List<Integer>>() {};
    /**
     * 长列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_LONG = new TypeDef<List<Long>>() {};
    /**
     * 双列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_DOUBLE = new TypeDef<List<Double>>() {};
    /**
     * 浮动列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_FLOAT= new TypeDef<List<Float>>() {};
    /**
     * biginteger列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_BIGINTEGER = new TypeDef<List<BigInteger>>() {};
    /**
     * bigdecimal列表
     *
     * @see TypeDef
     */
    public static final TypeDef LIST_BIGDECIMAL = new TypeDef<List<BigDecimal>>() {};
    
    /**
     * 设置字符串
     *
     * @see TypeDef
     */
    public static final TypeDef SET_STRING = new TypeDef<Set<String>>() {};
    /**
     * 组整数
     *
     * @see TypeDef
     */
    public static final TypeDef SET_INTEGER = new TypeDef<Set<Integer>>() {};
    /**
     * 设置长
     *
     * @see TypeDef
     */
    public static final TypeDef SET_LONG = new TypeDef<Set<Long>>() {};
    /**
     * 设置双
     *
     * @see TypeDef
     */
    public static final TypeDef SET_DOUBLE = new TypeDef<Set<Double>>() {};
    /**
     * 设置浮动
     *
     * @see TypeDef
     */
    public static final TypeDef SET_FLOAT = new TypeDef<Set<Float>>() {};
    /**
     * 设置biginteger
     *
     * @see TypeDef
     */
    public static final TypeDef SET_BIGINTEGER = new TypeDef<Set<BigInteger>>() {};
    /**
     * 设置bigdecimal
     *
     * @see TypeDef
     */
    public static final TypeDef SET_BIGDECIMAL = new TypeDef<Set<BigDecimal>>() {};
    
    
    /**
     * 映射字符串
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_STRING = new TypeDef<Map<String, String>>() {};
    /**
     * 地图整数
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_INTEGER = new TypeDef<Map<String, Integer>>() {};
    /**
     * 地图长
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_LONG = new TypeDef<Map<String, Long>>() {};
    /**
     * 地图上双
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_DOUBLE = new TypeDef<Map<String, Double>>() {};
    /**
     * 地图上浮动
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_FLOAT= new TypeDef<Map<String, Float>>() {};
    /**
     * 地图biginteger
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_BIGINTEGER = new TypeDef<Map<String, BigInteger>>() {};
    /**
     * 地图bigdecimal
     *
     * @see TypeDef
     */
    public static final TypeDef MAP_BIGDECIMAL = new TypeDef<Map<String, BigDecimal>>() {};
    
    
    protected Type type;
    protected Class<?> defClass;
    
    /**
     * def类型
     *
     * @return
     * @author Keith
     * @date 2022-08-17
     */
    protected TypeDef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass == TypeDef.class) {
            throw new IllegalArgumentException("Must appoint generic class in TypeDef.");
        }
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType paraType = (ParameterizedType) type;
            this.type = paraType;
            this.defClass = (Class<?>) paraType.getRawType();
        } else {
            this.type = type;
            this.defClass = (Class<?>) type;
        }
    }
    
    
    /**
     * 获取类型
     *
     * @return {@link Type }
     * @author Keith
     * @date 2022-08-17
     */
    public Type getType() {
        return type;
    }
    
    
    /**
     * 获取def类
     *
     * @return {@link Class }<{@link ? }>
     * @author Keith
     * @date 2022-08-17
     */
    public Class<?> getDefClass() {
        return defClass;
    }
}
