package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Amount;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Acceptance action managed for the voucher types
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherTypeStepDef extends StepDefs {

    @Given("exist the voucher types:")
    public void existTheVoucherTypes(List<Map<String, String>> datatable) {
        Map<VoucherType, List<Voucher>> voucherTypes = extractDatatableData(datatable);

        //save all
        voucherTypes.forEach((key, value) -> {
            voucherTypeRepository.save(key);
            voucherRepository.saveAll(value);
        });
    }

    @And("today is {string}")
    public void todayIs(String today) {
        Instant instant = LocalDate.parse(today, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay().toInstant(ZoneOffset.UTC);
        setupClockMock(instant);
    }

    @When("the user of the merchant {string} request the product available for the payment provider {string} in country {string}")
    public void theUserOfTheMerchantTinderRequestTheProductAvailable(String merchant, String paymentProvider, String country) throws Exception {

        resultActions = mockMvc.perform(get("/v1/voucher-type/available")
            .accept(MediaType.APPLICATION_JSON)
            .param("merchant", merchant)
            .param("paymentProvider", paymentProvider)
            .param("country", country)
        );

    }

    @When("the operator requires the voucher with {string} {string}")
    public void theOperatorRequiresTheVoucherWithParameterValue(String parameterName, String value) throws Exception {
        if (StringUtils.isNotBlank(parameterName)) {
            resultActions = mockMvc.perform(get("/v1/voucher-type")
                .accept(MediaType.APPLICATION_JSON)
                .param(parameterName, value));
        } else {
            resultActions = mockMvc.perform(get("/v1/voucher-type")
                .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Then("the user retrieve the list:")
    public void theUserRetrieveTheList(List<Map<String, String>> datatable) throws Exception {
        resultActions.andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        List<AvailableVoucherTypes> resultList = objectMapper.readValue(contentAsString, new TypeReference<List<AvailableVoucherTypes>>() {
        });

        this.checkResultList(datatable, resultList);

    }

    @Then("the user receive the error 'No Vouchers available, try later'")
    public void theUserReceiveTheErrorNoVouchersAvailableTryLater() throws Exception {
        resultActions.andExpect(status().isNotFound());
    }

    @Then("the user retrieve the list with {int} Element")
    public void theUserRetrieveTheListWithResultElement(int size) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.[*]", hasSize(size)));
    }

    private void checkResultList(List<Map<String, String>> expectedList, List<AvailableVoucherTypes> resultList) {

        Assertions.assertThat(resultList).hasSize(expectedList.size());

        //check each row
        for (int i = 0; i < expectedList.size(); i++) {
            Map<String, String> expectedValue = expectedList.get(i);
            AvailableVoucherTypes resultValue = resultList.get(i);

            //check single row values
            Assertions.assertThat(resultValue.getCode()).isEqualTo(expectedValue.get("code"));
            Assertions.assertThat(resultValue.getDescription()).isEqualTo(expectedValue.get("description"));
            Assertions.assertThat(resultValue.getAmount()).isEqualTo(expectedValue.get("amount"));
            Assertions.assertThat(resultValue.getCurrency()).isEqualTo(expectedValue.get("currency"));
            Assertions.assertThat(resultValue.getVoucherAvailable().toString()).isEqualTo(expectedValue.get("Voucher Available"));
        }
    }

    private Map<VoucherType, List<Voucher>> extractDatatableData(List<Map<String, String>> datatable) {
        Map<VoucherType, List<Voucher>> returnValue = new HashMap<>();

        datatable.forEach(row -> {
            VoucherType voucherType = new VoucherType();
            voucherType.setCode(row.get("code"));
            voucherType.setDescription(row.get("description"));

            Amount amount = new Amount();
            amount.setValue(new BigDecimal(row.get("amount")));
            amount.setCurrency(row.get("currency"));
            voucherType.setAmount(amount);
            voucherType.setMerchantId(row.get("merchant"));
            voucherType.setPaymentProvider(row.get("paymentProvider"));
            voucherType.setCountry(row.get("country"));
            voucherType.setShopId(row.get("shop"));
            voucherType.setEnabled(row.get("enabled").equals("true"));
            voucherType.setStartDate(LocalDate.parse(row.get("startDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            voucherType.setEndDate(LocalDate.parse(row.get("endDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));


            int voucherPurchased = StringUtils.isBlank(row.get("Voucher Purchased")) ? 0 : Integer.parseInt(row.get("Voucher Purchased"));
            int voucherActive = StringUtils.isBlank(row.get("Voucher Active")) ? 0 : Integer.parseInt(row.get("Voucher Active"));

            List<Voucher> listVouchersToSave = buildListVouchers(voucherType,
                voucherPurchased,
                voucherActive);
            returnValue.put(voucherType, listVouchersToSave);
        });

        return returnValue;
    }

    private List<Voucher> buildListVouchers(VoucherType voucherType, int voucherPurchased, int voucherActive) {
        List<Voucher> vouchers = new ArrayList<>();

        IntStream.rangeClosed(1, voucherPurchased)
            .forEach(m -> vouchers.add(this.getVoucher(voucherType, VoucherStatus.PURCHASED)));

        IntStream.rangeClosed(1, voucherActive)
            .forEach(m -> vouchers.add(this.getVoucher(voucherType, VoucherStatus.ACTIVE)));

        return vouchers;
    }

    private Voucher getVoucher(VoucherType voucherType, VoucherStatus status) {
        Voucher voucher = new Voucher();

        voucher.setCode(RandomStringUtils.random(15, true, true));
        voucher.setStatus(status);
        voucher.setType(voucherType);
        voucher.setUserId(RandomStringUtils.random(30, true, true));
        voucher.setTransactionId(RandomStringUtils.random(30, true, true));

        return voucher;
    }
}
