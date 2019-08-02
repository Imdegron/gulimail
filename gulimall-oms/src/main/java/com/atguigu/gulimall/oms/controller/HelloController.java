package com.atguigu.gulimall.oms.controller;

import com.atguigu.gulimall.oms.feign.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author:hcl
 */
@RestController
@RefreshScope
public class HelloController {
    @Autowired
    WorldService worldService;
    @Value("${my.content}")
    private  String content ="";
    @GetMapping("/hello")
     public String hello(){
        String msg;
        //远程调用gulimall-pms服务的 /world 请求对应的方法,并接受返回值
        msg =worldService.world();
return "Hello" + msg + ""+content;
     }
}
