package com.wz.pilipili.result;

import lombok.Getter;

/**
 * 统一数据返回类
 */
@Getter
public class R<T> {

    private String code;

    private String msg;

    private T data;

    public R(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public R(T data){
        this.data = data;
        msg = "成功";
        code = "0";
    }

    public static R<String> success(){
        return new R<>(null);
    }

    public static R<String> success(String data){
        return new R<>(data);
    }

    public static R<String> fail(){
        return new R<>("1", "失败");
    }

    public static R<String> fail(String code, String msg){
        return new R<>(code, msg);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
