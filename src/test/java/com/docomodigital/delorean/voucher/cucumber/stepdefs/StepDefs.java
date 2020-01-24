package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import java.time.*;
import java.util.TimeZone;

public abstract class StepDefs {

    protected MockMvc mockMvc;

    protected ResultActions resultActions;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected VoucherRepository voucherRepository;

    @Autowired
    protected VoucherTypeRepository voucherTypeRepository;

    @Autowired
    private Clock clock;

    @PostConstruct
    public void init() {
        setupClockMock(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }

    protected void setupClockMock(Instant instant) {
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());
    }


}
