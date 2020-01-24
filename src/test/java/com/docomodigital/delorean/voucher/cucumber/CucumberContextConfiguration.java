package com.docomodigital.delorean.voucher.cucumber;

import com.docomodigital.delorean.voucher.VoucherServiceApplication;
import io.cucumber.java.Before;
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

    @Before
    public void setup_cucumber_spring_context() {
        // Dummy method so cucumber will recognize this class as glue
        // and use its context configuration.
    }

}
