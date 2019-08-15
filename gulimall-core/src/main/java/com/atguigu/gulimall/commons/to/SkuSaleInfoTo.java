package com.atguigu.gulimall.commons.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author:hcl
 */
@Data
public class SkuSaleInfoTo {
    private  Long skuId;
    //积分设置信息
    private BigDecimal buyBounds;
    private  BigDecimal growBounds;
    //优惠生效情况[1111（四个状态位，从右到左）;
    // 0 - 无优惠，成长积分是否赠送;
    // 1- 无优惠，购物积分是否赠送;
    // 2 - 有优惠，成长积分是否赠送;
    // 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
    private Integer[] work;
    //阶梯价格的信息
//    "fullCount": 0, 满几件
//            "discount": 0,  打几折
//            "ladderAddOther": 0, 阶梯价格是否可以与其他优惠叠加
    private  Integer fullCount;
    private BigDecimal discount;
    private  Integer ladderAddOther;

    //满减信息
    private BigDecimal fullPrice;
    private  BigDecimal reducePrice;
    private  Integer fullAddOther;
}
