package com.finalproject.dontbeweak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Configuration
@EnableSwagger2 // Swagger2 활성화
public class SwaggerConfig implements WebMvcConfigurer {

    private static final String API_NAME = "Don't Be Weak API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "Don't Be Weak API 명세서";

    // .select()
    // ApiSelectorBuilder를 생성
    // .apis()
    // api 스펙이 작성되어 있는 패키지를 지정 함.
    // 컨트롤러가 존재하는 패키지를 basepackage로 지정.
    // RequestMapping( GetMapping, PostMapping ... )이 선언된 API를 문서화 함.
    // .paths(PathSelectors.any())
    // apis()로 선택되어진 API중 특정 path 조건에 맞는 API들을 다시 필터링하여 문서화
    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.finalproject.dontbeweak"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    // API의 이름은 무엇이며 현재 버전은 어떻게 되는지 해당 API에 대한 정보
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .version("v1")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
