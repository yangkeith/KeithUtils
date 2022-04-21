package top.keith.yang.utils;

import top.keith.yang.enums.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>Title: ConverterUtils</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 15:26
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2022/04/02  Keith  初始创建
 * -----------------------------------------------
 */
public class ConverterUtils {
    
    //region 长度计算
    
    public static Object lengthConversion(Unit fromUnit, Unit toUnit, Object value) {
        return lengthConversion(fromUnit, toUnit, value, RoundingMode.HALF_UP);
    }
    
    public static Object lengthConversion(Unit fromUnit, Unit toUnit, Object value, int scale) {
        return lengthConversion(fromUnit, toUnit, value, scale, RoundingMode.HALF_UP);
    }
    
    public static Object lengthConversion(Unit fromUnit, Unit toUnit, Object value, RoundingMode mode) {
        BigDecimal fromValue = getUnitBase(fromUnit);
        BigDecimal toValue = getUnitBase(toUnit);
        int scale = fromValue.toString().length() - toValue.toString().length();
        return lengthConversion(fromUnit, toUnit, value, scale < 0 ? Math.abs(scale) : 0, mode);
    }
    
    public static Object lengthConversion(Unit fromUnit, Unit toUnit, Object value, int scale, RoundingMode mode) {
        if (isLengthUnit(fromUnit) && isLengthUnit(toUnit)) {
            if (isNumber(value)) {
                BigDecimal fromValue = getUnitBase(fromUnit);
                BigDecimal toValue = getUnitBase(toUnit);
                BigDecimal ratio = fromValue.divide(toValue, scale, mode);
                return ratio.multiply(new BigDecimal(value + ""));
            }
            throw new IllegalArgumentException("value is not a number");
        }
        throw new IllegalArgumentException("params is not length unit");
    }
    //endregion
    
    //region 面积计算
    
    public static Object areaConversion(Unit fromUnit, Unit toUnit, Object value) {
        return areaConversion(fromUnit, toUnit, value, RoundingMode.HALF_UP);
    }
    
    public static Object areaConversion(Unit fromUnit, Unit toUnit, Object value, int scale) {
        return areaConversion(fromUnit, toUnit, value, scale, RoundingMode.HALF_UP);
    }
    
    public static Object areaConversion(Unit fromUnit, Unit toUnit, Object value, RoundingMode mode) {
        BigDecimal fromValue = getUnitBase(fromUnit);
        BigDecimal toValue = getUnitBase(toUnit);
        int scale = fromValue.toString().length() - toValue.toString().length();
        return areaConversion(fromUnit, toUnit, value, scale < 0 ? Math.abs(scale) : 0, mode);
    }
    
    public static Object areaConversion(Unit fromUnit, Unit toUnit, Object value, int scale, RoundingMode mode) {
        if (isAreaUnit(fromUnit) && isAreaUnit(toUnit)) {
            if (isNumber(value)) {
                BigDecimal fromValue = getUnitBase(fromUnit);
                BigDecimal toValue = getUnitBase(toUnit);
                BigDecimal ratio = fromValue.divide(toValue, scale, mode);
                return ratio.multiply(new BigDecimal(value + ""));
            }
            throw new IllegalArgumentException("value is not a number");
        }
        throw new IllegalArgumentException("params is not area unit");
    }
    // endregion
    
    //region 体积计算
    
    public static Object volumeConversion(Unit fromUnit, Unit toUnit,Object value){
        return volumeConversion(fromUnit, toUnit, value,RoundingMode.HALF_UP);
    }
    
    public static Object volumeConversion(Unit fromUnit, Unit toUnit,Object value,int scale){
        return volumeConversion(fromUnit, toUnit, value, scale,RoundingMode.HALF_UP);
    }
    
    public static Object volumeConversion(Unit fromUnit, Unit toUnit, Object value, RoundingMode mode){
        BigDecimal fromValue = getUnitBase(fromUnit);
        BigDecimal toValue = getUnitBase(toUnit);
        int scale = fromValue.toString().length() - toValue.toString().length();
        return volumeConversion(fromUnit, toUnit, value, scale < 0 ? Math.abs(scale) : 0, mode);
    }
    
    public static Object volumeConversion(Unit fromUnit, Unit toUnit, Object value,int scale, RoundingMode mode){
        if (isVolumeUnit(fromUnit) && isVolumeUnit(toUnit)) {
            if (isNumber(value)) {
                BigDecimal fromValue = getUnitBase(fromUnit);
                BigDecimal toValue = getUnitBase(toUnit);
                BigDecimal ratio = fromValue.divide(toValue, scale, mode);
                return ratio.multiply(new BigDecimal(value + ""));
            }
            throw new IllegalArgumentException("value is not a number");
        }
        throw new IllegalArgumentException("params is not volume unit");
    }
    // endregion
    private static BigDecimal getUnitBase(Unit unit) {
        // 基础单位 长度是毫米 面积是平方毫米 体积是立方毫米
        BigDecimal decimal = null;
        switch (unit) {
            // region  长度
            case LEN_KILOMETER:
            case LEN_KILOMETRE:
                decimal = new BigDecimal(1000000);
                break;
            case LEN_METER:
                decimal = new BigDecimal(1000);
                break;
            case LEN_DECIMETRE:
                decimal = new BigDecimal(100);
                break;
            case LEN_CENTIMETRE:
                decimal = new BigDecimal(10);
                break;
            // endregion
            // region 面积
            case AREA_SQUARE_CENTIMETER:
                decimal = new BigDecimal(100);
                break;
            case AREA_SQUARE_KILOMETER:
            case AREA_SQUARE_KILOMETRE:
                decimal = new BigDecimal(1000000000000L);
                break;
            case AREA_HECTARE:
                decimal = new BigDecimal(10000000000L);
                break;
            case AREA_MU:
                decimal = new BigDecimal("666666666.6666666");
                break;
            case AREA_SQUARE_METRE:
                decimal = new BigDecimal(1000000);
                break;
            case AREA_SQUARE_DECIMETER:
                decimal = new BigDecimal(10000);
                break;
            // endregion
            // region 体积
            case VOL_CUBIC_METER:
                decimal = new BigDecimal(1000000000);
                break;
            case VOL_CUBIC_HECTOLITRE:
                decimal = new BigDecimal(100000000);
                break;
            case VOL_CUBIC_DECALITRE:
                decimal = new BigDecimal(10000000);
                break;
            case VOL_LITRE:
            case VOL_CUBIC_DECIMETER:
                decimal = new BigDecimal(1000000);
                break;
            case VOL_CUBIC_DECILITRE:
                decimal = new BigDecimal(100000);
                break;
            case VOL_CUBIC_CENTILITRE:
                decimal = new BigDecimal(10000);
                break;
            case VOL_CUBIC_CENTIMETER:
            case VOL_MILLILITER:
                decimal = new BigDecimal(1000);
                break;
            // endregion
            
            // region 基础单位
            case LEN_MILLIMETRE:
            case AREA_SQUARE_MILLIMETER:
            case VOL_CUBIC_MILLIMETER:
            default:
                decimal = new BigDecimal(1);
            // endregion
        }
        return decimal;
    }
    
    private static boolean isLengthUnit(Unit unit) {
        return unit.name().toLowerCase().startsWith("len");
    }
    
    private static boolean isAreaUnit(Unit unit) {
        return unit.name().toLowerCase().startsWith("area");
    }
    
    private static boolean isVolumeUnit(Unit unit) {
        return unit.name().toLowerCase().startsWith("vol");
    }
    
    private static boolean isNumber(Object value) {
        if (value == null){
            return false;
        }
        String className = value.getClass().getSimpleName();
        switch (className) {
            case "Double":
            case "Integer":
            case "Float":
            case "Long":
            case "Byte":
            case "Short":
                return true;
            default:
                return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.println(volumeConversion(Unit.VOL_CUBIC_METER, Unit.VOL_LITRE, 1,12, RoundingMode.HALF_UP));
    }
}
