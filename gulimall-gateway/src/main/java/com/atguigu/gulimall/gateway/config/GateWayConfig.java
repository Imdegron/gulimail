package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * author:hcl
 */
@Configuration
public class GateWayConfig {
    @Bean
   public CorsWebFilter filter(){
       //设置跨域允许配置
      CorsConfiguration config= new CorsConfiguration();
           config.addAllowedHeader("*");
           config.addAllowedMethod("*");
           config.addAllowedOrigin("*");
           config.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source =new UrlBasedCorsConfigurationSource();

      source.registerCorsConfiguration("/**",config);
      CorsWebFilter filter= new CorsWebFilter(source);
      return  filter;
   }
}
