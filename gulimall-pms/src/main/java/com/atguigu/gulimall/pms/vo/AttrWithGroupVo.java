package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import lombok.Data;

/**
 * author:hcl
 */
@Data
public class AttrWithGroupVo extends AttrEntity {
    private AttrGroupEntity group;
}
