package com.docomodigital.delorean.voucher.web.api.security;

import com.docomodigital.delorean.voucher.config.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter to retrieve APIKey data from header
 */
public class HeaderKeysAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String apiKey = request.getHeader(Constants.API_KEY_HEADER);

            if (StringUtils.isBlank(apiKey)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "No " + Constants.API_KEY_HEADER + " header provided");
                return;
            }

            Authentication auth = new AuthenticatedMerchant(apiKey);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
