package com.atguigu.gulimall.oms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * author:hcl
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {
    @Bean
    public Docket webApiConfig(){
        return  new Docket(DocumentationType.SWAGGER_2)
                .groupName("订单管理系统")
                .apiInfo(webApiInfo())
                .select()
                .build();

    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-订单管理系统")
                .description("本文描述了商品系统微服务接口定义")
                .version("1.2")
                .build();
    }



}

