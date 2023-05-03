package com.finalproject.dontbeweak.config;

import com.finalproject.dontbeweak.auth.interceptor.JwtAuthenticationInterceptor;
import com.finalproject.dontbeweak.auth.filter.JwtAuthenticationFilter;
import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.jwtwithredis.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final Response customResponse;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //CORS를 적용할 URL패턴을 정의
                .allowedOrigins("*"); //자원 공유를 허락할 Origin을 지정
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthenticationInterceptor(jwtTokenProvider, redisTemplate, customResponse))
                .order(1)
                .addPathPatterns("/**") // 이 경로의 하위 모두 허용
                .excludePathPatterns("/", "/user/signup", "/login", "/css/**", "/*.ico", "/error", "/cat/{username}", "/index.html", "/image/**", "/templates/**", "/static/**", "test/**"); // 제외할 경로들
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/templates/")
                    .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }

//    @Bean
    public FilterRegistrationBean tokenCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate, customResponse));
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/items");
        filterFilterRegistrationBean.addUrlPatterns("/cat");

        return filterFilterRegistrationBean;
    }
}
