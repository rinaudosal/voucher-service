package com.docomodigital.delorean.voucher.web.api.security;

import com.docomodigital.delorean.client.merchant.model.Shop;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class AuthenticatedMerchant extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;

    private String apiKey;
    private transient Shop shop;

    public AuthenticatedMerchant(String apiKey) {
        super(Collections.emptyList());
        this.apiKey = apiKey;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "AuthenticatedMerchant{" +
            "apiKey='" + apiKey + '\'' +
            ", shop=" + shop +
            '}';
    }
}
