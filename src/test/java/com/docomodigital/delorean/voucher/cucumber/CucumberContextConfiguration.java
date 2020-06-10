package com.docomodigital.delorean.voucher.cucumber;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.client.merchant.model.Shop;
import com.docomodigital.delorean.voucher.VoucherServiceApplication;
import com.docomodigital.delorean.voucher.config.SignatureComponent;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.repository.VoucherFileRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.service.AccountingService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.mockito.BDDMockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.time.Clock;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(properties = "spring.profiles.active=test")
@WebAppConfiguration
@ContextConfiguration(classes = VoucherServiceApplication.class)
@AutoConfigureMockMvc
@MockBean(classes = {Clock.class, RabbitTemplate.class, MerchantClient.class, SignatureComponent.class, AccountingService.class})
public class CucumberContextConfiguration {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherTypeRepository voucherTypeRepository;

    @Autowired
    private VoucherFileRepository voucherFileRepository;

    @Autowired
    private VoucherErrorRepository voucherErrorRepository;

    @Autowired
    private MerchantClient merchantClient;

    @Before
    public void setUp() {
        Shop shop = new Shop();
        shop.setId("vfv");
        shop.setName("Tinder Indonesia");
        shop.setCountry("IN");
        shop.setSignatureKey("TEST_SIGNATURE_KEY");
        shop.setRequireSignedSession(true);
        shop.setContractId("12345");

        BDDMockito.given(merchantClient.getShopById(eq("asia")))
            .willReturn(shop);

    }

    @After
    public void tearDown() {

        File fileToDelete = FileUtils.getFile("voucher_example.csv");

        if (fileToDelete.exists()) {
            boolean success = FileUtils.deleteQuietly(fileToDelete);
            Assertions.assertThat(success).isTrue();
        }

        voucherRepository.deleteAll();
        voucherTypeRepository.deleteAll();
        voucherFileRepository.deleteAll();
        voucherErrorRepository.deleteAll();
    }
}
