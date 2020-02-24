package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @When("the user of request the product available by products {string}")
    public void theUserOfRequestTheProductAvailableByProductsProductsIn(String productsIn) throws Exception {
        String[] products = StringUtils.split(productsIn, ",");

        resultActions = mockMvc.perform(get("/v1/products/available")
            .accept(MediaType.APPLICATION_JSON)
            .param("products", products));

    }

    @Then("the user retrieve the list of {string}")
    public void theUserRetrieveTheListOfProductsOut(String productsOut) throws Exception {
        String[] products = StringUtils.split(productsOut, ",");

        resultActions.andExpect(jsonPath("$[*]", hasSize(products.length)));
        resultActions.andExpect(jsonPath("$[*]", containsInAnyOrder(products)));
    }

    @Then("the user receive the error 'No Products available, try later'")
    public void theUserReceiveTheErrorNoProductsAvailableTryLater() throws Exception{
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.[*]", hasSize(0)));
    }
}
