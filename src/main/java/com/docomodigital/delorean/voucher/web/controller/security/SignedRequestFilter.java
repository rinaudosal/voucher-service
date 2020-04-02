package com.docomodigital.delorean.voucher.web.controller.security;

import com.docomodigital.delorean.domain.resource.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.config.SignatureComponent;
import com.docomodigital.microservice.api.logging.CachedHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

        String privateKey = ((Shop) merchant.getPrincipal()).getSignatureKey();
        String signatureKey = request.getHeader(Constants.SIGNATURE_HEADER_NAME);

        byte[] body = requestCacheWrapper.getContent().getBytes(StandardCharsets.UTF_8);

        boolean requiredSignedSession = ((Shop) merchant.getPrincipal()).isRequireSignedSession();

        if (requiredSignedSession && StringUtils.isBlank(signatureKey)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "No " + Constants.SIGNATURE_HEADER_NAME + " header provided");
            return;
        }

        if (requiredSignedSession && !signatureComponent.validateSignature(privateKey, signatureKey, body)) {
            logger.error("Signature not valid");
            response.sendError(HttpStatus.FORBIDDEN.value(), "Signature not valid");
            return;
        }

        filterChain.doFilter(requestCacheWrapper, response);
    }

}
