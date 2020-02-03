package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    protected String getElementOrDefault(Map<String, String> row, String field, String defaultValue) {
        return row.get(field) != null ? row.get(field) : defaultValue;
    }

    protected void checkBadRequest(String errorCode, String errorMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode))
            .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    protected static void writeVoucherFile(int size,String fileName,  String code) throws Exception {
        Writer fstream = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);

        if(code != null) {
            fstream.append(code);
            fstream.append("\n");
        }

        for (int i = 0; i < size; i++) {
            fstream.append(UUID.randomUUID().toString());
            fstream.append("\n");
        }

        fstream.flush();
        fstream.close();
    }

}
