package com.docomodigital.delorean.voucher.web.api.security;

import com.docomodigital.delorean.domain.resource.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.config.SignatureComponent;
import com.docomodigital.microservice.api.logging.CachedHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SignedRequestFilter extends OncePerRequestFilter {
    private final SignatureComponent signatureComponent;

    public SignedRequestFilter(SignatureComponent signatureComponent) {
        this.signatureComponent = signatureComponent;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        CachedHttpServletRequestWrapper requestCacheWrapper = new CachedHttpServletRequestWrapper(request);

        AuthenticatedMerchant merchant = (AuthenticatedMerchant) SecurityContextHolder.getContext().getAuthentication();

        String apiKey = request.getHeader(Constants.API_KEY_HEADER);
        String privateKey = ((Shop) merchant.getPrincipal()).getSignatureKey();
        String signatureKey = request.getHeader(Constants.SIGNATURE_HEADER_NAME);

        byte[] body = requestCacheWrapper.getContent().getBytes(StandardCharsets.UTF_8);

        if (!signatureComponent.validateSignature(apiKey, privateKey, signatureKey, body)) {
            logger.error("Signature not valid");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        filterChain.doFilter(requestCacheWrapper, response);
    }

}
