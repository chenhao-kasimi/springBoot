package com.wms.entity;

import lombok.Data;

@Data
public class RecordRes extends Record{
    //对Record进行扩展，在联合查询时设定为username和adminname
    //y由于Record并没有这两项，又不打算更改Record，所以就用RecordRes继承
    private String username;
    private String adminname;
    private String goodsname;
    private String storagename;
    private String goodstypename;
}
