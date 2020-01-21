package com.docomodigital.delorean.voucher;

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

    @MockBean
    protected Clock clock;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    /**
     * Step before all integration test tha extends this class
     */
    @Before
    public void baseBefore() {
        Instant instant = Instant.now();
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());
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
    }

}
