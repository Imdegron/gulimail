package com.atguigu.gulimall.pms.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * author:hcl
 */
@Service
@FeignClient("gulimall-sms")
public interface SmsSkuSaleInfoFeignService {
    @PostMapping("/sms/skubounds/saleinfo/save")
    Resp<Object> saveSkuSaleInfos(SkuSaleInfoTo to);
}
