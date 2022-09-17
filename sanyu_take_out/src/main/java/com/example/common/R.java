package com.example.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> implements Serializable {

    private Integer code;//1成功0失败

    private String msg;//错误信息

    private T data;//数据

    private Map map=new HashMap();

    /**
     * 成功
     * @param object
     * @param <T>
     * @return
     */
    public static <T> R<T> success(T object){
        R<T> r=new R<T>();
        r.data=object;
        r.code=1;
        return r;
    }

    /**
     * 失败
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> error(String msg){
        R r=new R();
        r.msg=msg;
        r.code=0;
        return r;
    }

    /**
     * 操作动态数据
     * @param key
     * @param value
     * @return
     */
    public  R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
