package com.docomodigital.delorean.voucher.web.controller.security;

import com.docomodigital.delorean.voucher.config.SignatureComponent;
import com.docomodigital.delorean.voucher.web.api.model.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper mapper;
    private final MerchantAuthenticationProvider authProvider;
    private final SignatureComponent signatureComponent;

    public SecurityConfig(ObjectMapper mapper, MerchantAuthenticationProvider authProvider, SignatureComponent signatureComponent) {
        this.mapper = mapper;
        this.authProvider = authProvider;
        this.signatureComponent = signatureComponent;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/v1/external/**").authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .addFilterBefore(new HeaderKeysAuthFilter(), BasicAuthenticationFilter.class)
            .authenticationProvider(authProvider)
            .addFilterAfter(new SignedRequestFilter(signatureComponent), SwitchUserFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling()
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, accessDeniedException);
            })
            .authenticationEntryPoint((request, response, authenticationException) -> {
                if (authenticationException instanceof AuthenticationServiceException) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_GATEWAY, authenticationException);
                } else if (authenticationException instanceof BadCredentialsException) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, authenticationException);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, authenticationException);
                }
            });
    }

    private void sendErrorResponse(HttpServletResponse response, int errorCode, Exception authenticationException) throws IOException {
        log.error("Exception on authentication", authenticationException);

        ErrorDetails details = new ErrorDetails();
        details.setErrorCode("AUTHENTICATION_ERROR");
        details.setErrorMessage(authenticationException.getMessage());

        response.sendError(errorCode, mapper.writeValueAsString(details));
    }

    @Configuration
    @Order(2)
    class InternalSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/*").permitAll()
                .and()
                .csrf().disable()
            ;
        }
    }
}
