package io.github.yangkeith.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @program: org-people-service
 * @package: com.supermap.digicity.webadmin.utils
 * @description: 身份证工具类
 * @author: Keith
 * @date：Created in 2022-11-29 11:05
 */
public class IdentityCardUtil {
    
    /**
     * 身份证长度
     */
    private static final int CARD_LENGTH = 18;
    /**
     * 身份证最后一位
     *
     * @see char[]
     */
    private static final char[] CARD_END = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    
    /**
     * 从第一位到第十七位的系数
     *
     * @see int[]
     */
    private static final int[] COFF = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    
    /**
     * 验证
     *
     * @param idCardNo 身份证
     * @return boolean
     * @author Keith
     * @date 2022-11-29
     */
    public static boolean verify(String idCardNo) {
        boolean result = true;
        if (idCardNo.length() != CARD_LENGTH) {
            return false;
        }
        String tempId = getStr(idCardNo, 0, 16);
        int sum = 0;
        for (int i = 0; i < tempId.length(); i++) {
            int bye = tempId.charAt(i) - '0';
            sum += bye * COFF[i];
        }
        sum %= 11;
        if (CARD_END[sum] != getStr(idCardNo, 17, 17).charAt(0)) {
            result = false;
        }
        return result;
    }
    
    private static String getStr(String str, int a, int b) {
        return str.substring(a, b + 1);
    }
    
    /**
     * 法官性别
     *
     * @param idCardNo 没有身份证
     * @return {@link String }
     * @throws IllegalArgumentException 非法参数异常
     * @author Keith
     * @date 2022-11-29
     */
    public static String judgeGender(String idCardNo) throws IllegalArgumentException {
        if (verify(idCardNo)) {
            throw new IllegalArgumentException("身份证号错误");
        }
        char c = idCardNo.charAt(idCardNo.length() - 2);
        int gender = Integer.parseInt(String.valueOf(c));
        if (gender % 2 == 1) {
            return "男";
        } else {
            return "女";
        }
    }
    
    /**
     * 计算年龄
     *
     * @param idCardNo 没有身份证
     * @return int
     * @throws ParseException           格式化异常
     * @throws IllegalArgumentException 非法参数异常
     * @author Keith
     * @date 2022-11-29
     */
    public static int calculateAge(String idCardNo) throws ParseException, IllegalArgumentException {
        if (verify(idCardNo)) {
            throw new IllegalArgumentException("身份证号错误");
        }
        String birth = idCardNo.substring(6, 14);
        Calendar curTime = Calendar.getInstance();
        int curYear = curTime.get(Calendar.YEAR);
        int curMonth = curTime.get(Calendar.MONTH) + 1;
        int curDay = curTime.get(Calendar.DAY_OF_MONTH);
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        curTime.setTime(format.parse(birth));
        
        int peopleYear = curTime.get(Calendar.YEAR);
        int peopleMonth = curTime.get(Calendar.MONTH) + 1;
        int peopleDay = curTime.get(Calendar.DAY_OF_MONTH);
        
        // 用当前年月日减去生日年月日
        int yearMinus = curYear - peopleYear;
        int monthMinus = curMonth - peopleMonth;
        int dayMinus = curDay - peopleDay;
        
        //先大致赋值
        int age = yearMinus;
        //出生年份为当前年份
        if (yearMinus != 0) {
            //出生月份小于当前月份时，还没满周岁
            if (monthMinus < 0) {
                age = age - 1;
            }
            //当前月份为出生月份时，判断日期
            if (monthMinus == 0) {
                //出生日期小于当前月份时，没满周岁
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }
        }
        return age;
    }
}
