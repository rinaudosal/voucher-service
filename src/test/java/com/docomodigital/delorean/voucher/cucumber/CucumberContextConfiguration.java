package com.docomodigital.delorean.voucher.cucumber;

import com.docomodigital.delorean.voucher.VoucherServiceApplication;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Clock;

@SpringBootTest(properties = "spring.profiles.active=test")
@WebAppConfiguration
@ContextConfiguration(classes = VoucherServiceApplication.class)
@AutoConfigureMockMvc
@MockBean(classes = {Clock.class})
public class CucumberContextConfiguration {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherTypeRepository voucherTypeRepository;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        voucherRepository.deleteAll();
        voucherTypeRepository.deleteAll();
    }
}
