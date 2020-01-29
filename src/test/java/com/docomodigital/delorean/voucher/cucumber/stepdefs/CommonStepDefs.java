package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import io.cucumber.java.en.And;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 2020/01/28
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class CommonStepDefs extends StepDefs {

    @And("today is {string}")
    public void todayIs(String today) {
        Instant instant = LocalDate.parse(today, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay().toInstant(ZoneOffset.UTC);
        setupClockMock(instant);
    }

}
