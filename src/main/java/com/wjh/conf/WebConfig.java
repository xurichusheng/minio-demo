package com.wjh.conf;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wjh.interceptor.LoginInterceptor;

/**
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 登录拦截器 */
    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册登录拦截器
        InterceptorRegistration registration = registry.addInterceptor(loginInterceptor);
        registration.addPathPatterns("/**");                      //所有路径都被拦截
        registration.excludePathPatterns(                         //添加不拦截路径
                "/login/tolist",            // 登录
                "/login/page",       // 登录页面
                "/bucket/file/download", // 文件下载
                "/*.html",        // html静态资源
                "/**/*.html",        // html静态资源
                "/js/*.js",   // js静态资源
                "/css/*.css",         // css静态资源
                "/css/images/*.png",
                "/css/images/*.jpg",
                "/css/images/*.gif",
                "/images/*.png",
                "/images/*.jpg",
                "/images/*.gif",
                "/**/*.woff",
                "/**/*.ttf",
                "/*.jpg",
                "/**/*.jpg"
        );
    }
}
