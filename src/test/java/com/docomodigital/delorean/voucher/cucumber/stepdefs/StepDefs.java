package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.client.merchant.model.ChannelResponse;
import com.docomodigital.delorean.client.merchant.model.Shop;
import com.docomodigital.delorean.voucher.config.SignatureComponent;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.service.VoucherQueueReceiverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class StepDefs {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ResultComponent resultComponent;

    @Autowired
    protected MerchantClient merchantClient;

    @Autowired
    private SignatureComponent signatureComponent;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected VoucherQueueReceiverService voucherQueueReceiverService;

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Autowired
    protected VoucherRepository voucherRepository;

    @Autowired
    protected VoucherErrorRepository voucherErrorRepository;

    @Autowired
    protected VoucherTypeRepository voucherTypeRepository;

    @Autowired
    private Clock clock;

    @PostConstruct
    public void init() {
        setupClockMock(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));

        setupMerchantClient();

        BDDMockito.given(signatureComponent.validateSignature(
            eq("TEST_PRIVATE_KEY"),
            eq("TEST_SIGNATURE_KEY"),
            Mockito.any(byte[].class)))
            .willReturn(true);

    }

    protected void setupMerchantClient() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setId("TEST_API_KEY");
        channelResponse.setStatus("enabled");
        channelResponse.setMerchantId("TINDER");
        Shop shop = new Shop();
        shop.setId("my_shop_id");
        shop.setName("Tinder Indonesia");
        shop.setCountry("IN");
        shop.setSignatureKey("TEST_PRIVATE_KEY");
        channelResponse.setShop(shop);

        BDDMockito.given(merchantClient.getChannelByApiKey(eq("TEST_API_KEY")))
            .willReturn(channelResponse);
    }

    protected void setupClockMock(Instant instant) {
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());
    }

    protected String getElementOrDefault(Map<String, String> row, String field, String defaultValue) {
        return row.get(field) != null ? row.get(field) : defaultValue;
    }

    protected void checkBadRequest(String errorCode, String errorMessage) throws Exception {
        resultComponent.resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode))
            .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    protected static void writeVoucherFile(int size, String fileName, String code) throws Exception {
        Writer fstream = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);

        if (code != null) {
            String[] codes = code.split(",");
            for (String line : codes) {
                fstream.append(line);
                fstream.append("\n");
            }
        }

        for (int i = 0; i < size; i++) {
            fstream.append(UUID.randomUUID().toString());
            fstream.append("\n");
        }

        fstream.flush();
        fstream.close();
    }

}
