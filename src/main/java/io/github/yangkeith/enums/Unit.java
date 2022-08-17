package io.github.yangkeith.enums;

/**
 * <p>Title: Unit</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 15:34
 */
public enum Unit {
    // 长度 - 一维
    LEN_KILOMETER("km", "千米"), LEN_KILOMETRE("km", "公里"), LEN_METER("m", "米"),
    LEN_DECIMETRE("dm", "分米 "), LEN_CENTIMETRE("cm", "厘米"), LEN_MILLIMETRE("mm", "毫米"),
    // 面积 - 二维
    AREA_SQUARE_MILLIMETER("smm", "平方毫米"), AREA_SQUARE_CENTIMETER("scm", "平方厘米"),
    AREA_SQUARE_DECIMETER("sdm", "平方分米 "), AREA_SQUARE_METRE("sm", "平方米"),
    AREA_SQUARE_KILOMETER("skm", "平方千米"), AREA_SQUARE_KILOMETRE("skm", "平方公里"),
    AREA_HECTARE("ha", "公顷"), AREA_MU("mu", "亩"),
    // 体积 - 三维
    VOL_CUBIC_CENTIMETER("ccm", "立方厘米"), VOL_CUBIC_MILLIMETER("cmm", "立方毫米"),
    VOL_CUBIC_DECIMETER("cdm", "立方分米"), VOL_CUBIC_METER("cm", "立方米"),
    VOL_CUBIC_HECTOLITRE("hl", "公石"), VOL_CUBIC_DECALITRE("dal", "十升"),
    VOL_CUBIC_DECILITRE("dl", "分升"), VOL_CUBIC_CENTILITRE("cl", "厘升"),
    VOL_MILLILITER("ml", "毫升"), VOL_LITRE("l", "升");
    
    private final String value;
    private final String code;
    
    /**
     * 定义一个带参数的构造器
     */
    Unit(String code, String value) {
        this.value = value;
        this.code = code;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getCode() {
        return code;
    }
    
    /**
     * 从价值
     *
     * @param value 价值
     * @return {@link Unit }
     * @author Keith
     * @date 2022-08-17
     */
    public static Unit fromValue(String value) {
        for (Unit unit : Unit.values()) {
            if (unit.getValue().equals(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("value is invalid");
    }
    
    /**
     * 从代码
     *
     * @param code 代码
     * @return {@link Unit }
     * @author Keith
     * @date 2022-08-17
     */
    public static Unit fromCode(String code) {
        for (Unit unit : Unit.values()) {
            if (unit.getCode().equals(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("code is invalid");
    }
}
