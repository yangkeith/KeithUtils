package io.github.yangkeith.dto;

import java.io.Serializable;

/**
 * <p>Title: ClassType</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:07
 */
public class ClassType implements Serializable {
    private Class<?> mainClass; //类
    private ClassType[] genericTypes;//泛型
    
    public ClassType() {
    }
    
    public ClassType(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    /**
     * 类类型
     *
     * @param mainClass      主类
     * @param genericClasses 泛型类
     * @return
     * @author Keith
     * @date 2022-08-17
     */
    public ClassType(Class<?> mainClass, Class<?>[] genericClasses) {
        this.mainClass = mainClass;
        if (genericClasses != null && genericClasses.length > 0) {
            genericTypes = new ClassType[genericClasses.length];
            for (int i = 0; i < genericClasses.length; i++) {
                genericTypes[i] = new ClassType(genericClasses[i]);
            }
        }
    }
    
    /**
     * 类类型
     *
     * @param mainClass    主类
     * @param genericTypes 泛型类型
     * @return
     * @author Keith
     * @date 2022-08-17
     */
    public ClassType(Class<?> mainClass, ClassType[] genericTypes) {
        this.mainClass = mainClass;
        this.genericTypes = genericTypes;
    }
    
    /**
     * 获取主要类
     *
     * @return {@link Class }<{@link ? }>
     * @author Keith
     * @date 2022-08-17
     */
    public Class<?> getMainClass() {
        return mainClass;
    }
    
    /**
     * 设置主类
     *
     * @param mainClass 主类
     * @author Keith
     * @date 2022-08-17
     */
    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    /**
     * 获取通用类型
     *
     * @return {@link ClassType[] }
     * @author Keith
     * @date 2022-08-17
     */
    public ClassType[] getGenericTypes() {
        return genericTypes;
    }
    
    /**
     * 将泛型类型
     *
     * @param genericTypes 泛型类型
     * @author Keith
     * @date 2022-08-17
     */
    public void setGenericTypes(ClassType[] genericTypes) {
        this.genericTypes = genericTypes;
    }
    
    /**
     * 获取数据类型
     *
     * @return {@link String }
     * @author Keith
     * @date 2022-08-17
     */
    public String getDataType() {
        return mainClass.getSimpleName();
    }
    
    /**
     * 是通用
     *
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public boolean isGeneric() {
        return genericTypes != null && genericTypes.length > 0;
    }
    
    /**
     * 无效
     *
     * @return boolean
     * @author Keith
     * @date 2022-08-17
     */
    public boolean  isVoid(){
        return mainClass == void.class;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mainClass.getSimpleName());
        if (isGeneric()) {
            sb.append("<");
            for (int i = 0; i < genericTypes.length; i++) {
                sb.append(genericTypes[i].toString());
                if (i != genericTypes.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }
}
