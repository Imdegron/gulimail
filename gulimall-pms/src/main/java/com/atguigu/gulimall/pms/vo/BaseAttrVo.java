package com.atguigu.gulimall.pms.vo;

import lombok.Data;

/**
 * author:hcl
 */
@Data
public class BaseAttrVo {
    //属性id,属性名，属性值
    private Long attrId;
    private String attrName;
    private String[] valueSelected;
}
