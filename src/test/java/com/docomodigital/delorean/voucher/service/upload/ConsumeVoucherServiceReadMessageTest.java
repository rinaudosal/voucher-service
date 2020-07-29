package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class ConsumeVoucherServiceReadMessageTest extends BaseUnitTest {
    private ConsumeVoucherService target;

    @Before
    public void setUp() {
        target = new ConsumeVoucherServiceImpl(null, null, null, null, null, null);
    }

    @Test
    public void readFileProcessedCorrectly() throws Exception {
        String exampleRequest = FileUtils.readFileToString(new File("src/test/resources/exampleWLMRequestQueue.json"), StandardCharsets.UTF_8);

        VoucherConsumer output = target.readMessage(exampleRequest);

        Assertions.assertThat(output.getMerchantId()).isEqualTo("MRNor1014793");
        Assertions.assertThat(output.getShopId()).isEqualTo("STNor4937907");
        Assertions.assertThat(output.getPaymentProvider()).isEqualTo("VFONE");
        Assertions.assertThat(output.getCountry()).isEqualTo("AU");
        Assertions.assertThat(output.getProductId()).isEqualTo("VDFAU 1 Month");
        Assertions.assertThat(output.getUserId()).isEqualTo("5da85c48a0cf582e93858dc7");
        Assertions.assertThat(output.getTransactionId()).isEqualTo("7c24a089-d2fc-4011-b711-891d69");
        Assertions.assertThat(output.getRequestId()).isEqualTo("2372a681-2164-421a-99a1-c48d89301e6a");
        Assertions.assertThat(output.getTransactionDate()).isEqualTo(LocalDateTime.of(2020, 1, 20, 7, 59, 29).toInstant(ZoneOffset.UTC));
        Assertions.assertThat(output.getBillingStatus()).isEqualTo("BILLED");
    }

    @Test
    public void readFileRetrieveNullObjectWithoutExceptionReading() {
        VoucherConsumer voucherConsumer = target.readMessage("{\"attributes\": { \n + \"code\": \"2372a681-2164-421a-99a1-c48d89301e6a\"}}");
        Assertions.assertThat(voucherConsumer.getMerchantId()).isNull();
        Assertions.assertThat(voucherConsumer.getPaymentProvider()).isNull();
        Assertions.assertThat(voucherConsumer.getCountry()).isNull();
        Assertions.assertThat(voucherConsumer.getProductId()).isNull();
        Assertions.assertThat(voucherConsumer.getUserId()).isNull();
        Assertions.assertThat(voucherConsumer.getTransactionId()).isNull();
        Assertions.assertThat(voucherConsumer.getRequestId()).isNull();
        Assertions.assertThat(voucherConsumer.getTransactionDate()).isNull();
        Assertions.assertThat(voucherConsumer.getBillingStatus()).isNull();
    }
}
