package com.docomodigital.delorean.voucher;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.client.merchant.model.ChannelResponse;
import com.docomodigital.delorean.client.merchant.model.Shop;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.eq;

/**
 * Base settings for rest integration tests
 * <p>
 * 2019/11/11
 *
 * @author salvatore rinaudo
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@ContextConfiguration(classes = VoucherServiceApplication.class)
@AutoConfigureMockMvc
public abstract class BaseVoucherIntegrationTest {

    @Autowired
    protected VoucherTypeRepository voucherTypeRepository;

    @Autowired
    protected VoucherRepository voucherRepository;

    @MockBean
    protected Clock clock;

    @MockBean
    protected MerchantClient merchantClient;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    protected void setupMerchantClient() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setId("TEST_API_KEY");
        channelResponse.setStatus("enabled");
        channelResponse.setMerchantId("TINDER");
        Shop shop = new Shop();
        shop.setId("vfv");
        shop.setName("Tinder Indonesia");
        shop.setCountry("IN");
        shop.setSignatureKey("TEST_SIGNATURE_KEY");
        shop.setRequireSignedSession(true);
        shop.setContractId("12345");
        channelResponse.setShop(shop);

        BDDMockito.given(merchantClient.getChannelByApiKey(eq("TEST_API_KEY")))
            .willReturn(channelResponse);

        BDDMockito.given(merchantClient.getShopById(eq("asia")))
            .willReturn(shop);

    }


    /**
     * Step before all integration test tha extends this class
     */
    @Before
    public void baseBefore() {
        Instant instant = Instant.now();
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());

        setupMerchantClient();
    }

    /**
     * allows to perform a request
     *
     * @param requestWithUrl request url
     * @param body           body url
     * @return the result action on which you can interact with the response
     * @throws Exception any exception during the call
     */
    public ResultActions doRequest(MockHttpServletRequestBuilder requestWithUrl, Object body) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = requestWithUrl.contentType(MediaType.APPLICATION_JSON_UTF8);

        if (body != null) {
            requestBuilder = requestBuilder.content(objectMapper.writeValueAsString(body));
        }

        return mvc.perform(requestBuilder);
    }

    /**
     * allows to perform a request
     *
     * @param requestWithUrl request url
     * @param body           body url
     * @return the result action on which you can interact with the response
     * @throws Exception any exception during the call
     */
    public <T> T doRequest(MockHttpServletRequestBuilder requestWithUrl, Object body, Class<T> returnObjectType) throws Exception {
        ResultActions returnAction = this.doRequest(requestWithUrl, body);

        MvcResult result = returnAction.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        return objectMapper.readValue(contentAsString, returnObjectType);
    }

    /**
     * Allows to get the mvc
     *
     * @return the mvc
     */
    public MockMvc mvc() {
        return mvc;
    }

    /**
     * Allows to get the object mapper
     *
     * @return the object mapper
     */
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    protected void setupClockMock(Instant instant) {
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());
    }

    /**
     * Deletes the database after each method test is finished
     */
    @After
    public void deleteDb() {
        voucherTypeRepository.deleteAll();
        voucherRepository.deleteAll();
    }

}
