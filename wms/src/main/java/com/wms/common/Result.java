package com.wms.common;

import lombok.Data;

//后端返回前端的数据封装
@Data
public class Result {

    private int code;       //编码 200/400/404
    private String msg;     //成功/失败
    private Long total;     //总记录数
    private Object data;    //数据

    //失败
    public static Result fail(){
        return result(400,"失败",0L,null);
    }

    //不带参数的成功
    public static Result suc(){
        return result(200,"成功",0L,null);
    }

    //带参数的成功
    public static Result suc(Object data){
        return result(200,"成功",0L,data);
    }

    //带参数和列表的成功
    public static Result suc(Object data,Long total){
        return result(200,"成功",total,data);
    }

    public static Result result(int code,String msg,Long total,Object data){
        Result res=new Result();
        res.setData(data);
        res.setCode(code);
        res.setMsg(msg);
        res.setTotal(total);
        return res;
    }
}
