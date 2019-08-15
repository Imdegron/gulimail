package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * author:hcl
 * spu全量信息【】
 */
@Data
public class SpuAllSaveVo extends SpuInfoEntity {
    //当前spu的所有基本属性名值对
           private List<BaseAttrVo> baseAttrs;
           //当前sku的所有信息
          private  List<SkuVo> skus;
          //图片
    private  String[] spuImages;

}
