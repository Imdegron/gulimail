package com.atguigu.gulimall.pms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author:hcl
 */
@RestController
public class WorldController {
    @GetMapping("/world")
    public String world(){
        String msg ="world";
        return msg;
    }
}
