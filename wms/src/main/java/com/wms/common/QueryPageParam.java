package com.wms.common;

import lombok.Data;

import java.util.HashMap;

//@Date可以获取其属性成员的get，set方法
@Data
public class QueryPageParam {

    //默认
    private static int PAGE_SIZE=20;
    private static int PAGE_NUM=1;

    private int pageSize=PAGE_SIZE;
    private int pageNum=PAGE_NUM;

    private HashMap param=new HashMap();
}
