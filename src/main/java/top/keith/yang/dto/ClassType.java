package top.keith.yang.dto;

import java.io.Serializable;

/**
 * <p>Title: ClassType</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:07
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ClassType implements Serializable {
    private Class<?> mainClass; //类
    private ClassType[] genericTypes;//泛型
    
    public ClassType() {
    }
    
    public ClassType(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    public ClassType(Class<?> mainClass, Class<?>[] genericClasses) {
        this.mainClass = mainClass;
        if (genericClasses != null && genericClasses.length > 0) {
            genericTypes = new ClassType[genericClasses.length];
            for (int i = 0; i < genericClasses.length; i++) {
                genericTypes[i] = new ClassType(genericClasses[i]);
            }
        }
    }
    
    public ClassType(Class<?> mainClass, ClassType[] genericTypes) {
        this.mainClass = mainClass;
        this.genericTypes = genericTypes;
    }
    
    public Class<?> getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    public ClassType[] getGenericTypes() {
        return genericTypes;
    }
    
    public void setGenericTypes(ClassType[] genericTypes) {
        this.genericTypes = genericTypes;
    }
    
    public String getDataType() {
        return mainClass.getSimpleName();
    }
    
    public boolean isGeneric() {
        return genericTypes != null && genericTypes.length > 0;
    }
    
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
