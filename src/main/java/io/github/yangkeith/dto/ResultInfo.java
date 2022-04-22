package io.github.yangkeith.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>Title: ResultInfo</p>
 * <p>Description: 结果集</p>
 *
 * @author keith
 * @date 2021/12/20 15:44
 * ------------------- History -------------------
 * <date>      <author>       <desc>
 * 2021/12/20  keith  初始创建
 * -----------------------------------------------
 */
@Data
@ApiModel(value = "返回的结果集")
public class ResultInfo {
    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码")
    private int code;
    /**
     * 返回内容
     */
    @ApiModelProperty(value = "返回的内容")
    private String msg;
    /**
     * 数据对象
     */
    @ApiModelProperty(value = "返回的数据")
    private Object data;
    
    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public ResultInfo() {
    }
    
    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public ResultInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ResultInfo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResultInfo success() {
        return ResultInfo.success("操作成功");
    }
    
    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static ResultInfo success(Object data) {
        return ResultInfo.success("操作成功", data);
    }
    
    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static ResultInfo success(String msg) {
        return ResultInfo.success(msg, null);
    }
    
    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static ResultInfo success(String msg, Object data) {
        return new ResultInfo(200, msg, data);
    }
    
    /**
     * 返回错误消息
     *
     * @return
     */
    public static ResultInfo error() {
        return ResultInfo.error("操作失败");
    }
    
    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ResultInfo error(String msg) {
        return ResultInfo.error(msg, null);
    }
    
    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static ResultInfo error(String msg, Object data) {
        return new ResultInfo(500, msg, data);
    }
    
    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static ResultInfo error(int code, String msg) {
        return new ResultInfo(code, msg, null);
    }
}
