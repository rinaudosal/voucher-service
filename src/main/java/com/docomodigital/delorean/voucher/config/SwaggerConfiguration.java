package com.docomodigital.delorean.voucher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

/**
 * 2020/01/31
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.docomodigital.delorean.voucher.web.api"))
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(Arrays.asList(
                new ApiKey(Constants.API_KEY_HEADER, Constants.API_KEY_HEADER, "header"),
                new ApiKey(Constants.SIGNATURE_HEADER_NAME, Constants.SIGNATURE_HEADER_NAME, "header")
            ))
            ;
    }
}
