package com.docomodigital.delorean.voucher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            .securitySchemes(apiKeys())
            .securityContexts(Collections.singletonList(securityContext()))
            ;
    }

    private List<ApiKey> apiKeys() {
        return Arrays.asList(
            new ApiKey("ApiKeyAuth", Constants.API_KEY_HEADER, "header"),
            new ApiKey("SignRequest", Constants.SIGNATURE_HEADER_NAME, "header")
        );
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.regex("/v1/external/*"))
            .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(
            new SecurityReference("ApiKeyAuth", authorizationScopes),
            new SecurityReference("SignRequest", authorizationScopes)
        );
    }

}
