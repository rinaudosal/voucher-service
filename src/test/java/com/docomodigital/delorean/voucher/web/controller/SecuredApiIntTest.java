package com.docomodigital.delorean.voucher.web.controller;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import feign.FeignException;
import feign.Response;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 2020/03/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class SecuredApiIntTest extends BaseVoucherIntegrationTest {

    private static final String URL_TEMPLATE = "/v1/external/voucher-type/123/reserve";
    private static final String TEST_API_KEY = "TEST_API_KEY";
    private static final String TEST_SIGNATURE_KEY = "TEST_SIGNATURE_KEY";

    @Test
    public void apiCallUnauthorizedWithoutApiKey() throws Exception {

        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.SIGNATURE_HEADER_NAME, TEST_SIGNATURE_KEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isUnauthorized());

    }

    @Test
    public void apiCallUnauthorizedWithEmptyApiKey() throws Exception {
        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.SIGNATURE_HEADER_NAME, TEST_SIGNATURE_KEY)
            .header(Constants.API_KEY_HEADER, "")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isUnauthorized());

    }

    @Test
    public void apiCallUnauthorizedWithEmptySignatureKey() throws Exception {
        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.SIGNATURE_HEADER_NAME, "")
            .header(Constants.API_KEY_HEADER, TEST_API_KEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isUnauthorized());

    }

    @Test
    public void apiCallUnauthorizedWithoutSignatureKey() throws Exception {
        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.API_KEY_HEADER, TEST_API_KEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isUnauthorized());

    }

    @Test
    public void apiCallUnauthorizedWithApiKeyNotFound() throws Exception {

        FeignException feignException = FeignException.errorStatus("ee", Response.builder().status(404).headers(Collections.emptyMap()).build());

        BDDMockito.given(merchantClient.getChannelByApiKey(eq(TEST_API_KEY)))
            .willThrow(feignException);

        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.API_KEY_HEADER, TEST_API_KEY)
            .header(Constants.SIGNATURE_HEADER_NAME, TEST_SIGNATURE_KEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isUnauthorized());

    }

    @Test
    public void apiCallBadGatewayOnFeignException() throws Exception {

        FeignException feignException = FeignException.errorStatus("ee", Response.builder().status(400).headers(Collections.emptyMap()).build());

        BDDMockito.given(merchantClient.getChannelByApiKey(eq(TEST_API_KEY)))
            .willThrow(feignException);

        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.API_KEY_HEADER, TEST_API_KEY)
            .header(Constants.SIGNATURE_HEADER_NAME, TEST_SIGNATURE_KEY)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isBadGateway());

    }

    @Test
    public void apiCallForbiddenOnWrongSecurityKey() throws Exception {

        mvc().perform(post(URL_TEMPLATE)
            .header(Constants.API_KEY_HEADER, TEST_API_KEY)
            .header(Constants.SIGNATURE_HEADER_NAME, "WRONG_SECURITY_KEY")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(new VoucherRequest()))
            .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isForbidden());

    }
}
