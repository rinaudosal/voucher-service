package com.docomodigital.delorean.voucher.web.controller.security;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.client.merchant.model.ChannelResponse;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MerchantAuthenticationProvider implements AuthenticationProvider {
    private final MerchantClient merchantClient;

    public MerchantAuthenticationProvider(MerchantClient merchantClient) {
        this.merchantClient = merchantClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        AuthenticatedMerchant authMerchant = (AuthenticatedMerchant) authentication;
        String apiKey = (String) authMerchant.getCredentials();

        try {
            ChannelResponse response = merchantClient.getChannelByApiKey(apiKey);
            authMerchant.setShop(response.getShop());
            authMerchant.setAuthenticated(true);

        } catch (FeignException e) {
            handleFeignException(e);
        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof FeignException) {
                handleFeignException((FeignException) e.getCause());
            } else {
                throw new AuthenticationServiceException("Unable to authenticate", e);
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException("Unable to authenticate", e);
        }

        return authMerchant;
    }

    private void handleFeignException(FeignException e) {
        if (e.status() == 404)
            throw new BadCredentialsException("Invalid credentials", e);
        else
            throw new AuthenticationServiceException("Unable to authenticate", e);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticatedMerchant.class);
    }
}
