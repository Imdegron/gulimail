package com.atguigu.gulimall.pms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * author:hcl
 */
@Configuration
public class PmsSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);

        //http.authorizeRequests().anyRequest().permitAll();
        http.authorizeRequests().antMatchers( "/**").permitAll();
        //一定要防重，csrf功能关闭
        http.csrf().disable();
    }
}
