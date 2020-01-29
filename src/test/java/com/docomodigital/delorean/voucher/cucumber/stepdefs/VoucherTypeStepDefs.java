package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Amount;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Acceptance action managed for the voucher types
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherTypeStepDefs extends StepDefs {

    @Given("exist the voucher types:")
    public void existTheVoucherTypes(List<Map<String, String>> datatable) {
        Map<VoucherType, List<Voucher>> voucherTypes = extractDatatableData(datatable);

        //save all
        voucherTypes.forEach((key, value) -> {
            voucherTypeRepository.save(key);
            voucherRepository.saveAll(value);
        });
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

    @When("the operator requires the voucher with merchant {string} and country {string} and paymentProvider {string}")
    public void theOperatorRequiresTheVoucherWithMultiple(String merchant, String country, String paymentProvider) throws Exception {
        resultActions = mockMvc.perform(get("/v1/voucher-type")
            .accept(MediaType.APPLICATION_JSON)
            .param("merchant", merchant)
            .param("country", country)
            .param("paymentProvider", paymentProvider)
        );
    }

    @When("the operator requires the voucher type with code {string}")
    public void theOperatorRequiresTheVoucherTypeWithCode(String voucherCode) throws Exception {
        resultActions = mockMvc.perform(get("/v1/voucher-type/" + voucherCode)
            .accept(MediaType.APPLICATION_JSON));
    }

    @When("the operator wants to create the voucher type:")
    public void theOperatorWantsToCreateTheVoucherType(List<Map<String, String>> datatable) throws Exception {
        VoucherTypes voucherTypes = buildVoucherTypes(datatable, null);

        resultActions = mockMvc.perform(post("/v1/voucher-type")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(voucherTypes))
            .accept(MediaType.APPLICATION_JSON));
    }

    @When("the operator wants to update the voucher type {string}:")
    public void theOperatorWantsToUpdateTheVoucherType(String code, List<Map<String, String>> datatable) throws Exception {
        VoucherTypes voucherTypes = buildVoucherTypes(datatable, null);

        resultActions = mockMvc.perform(put("/v1/voucher-type/" + code)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(voucherTypes))
            .accept(MediaType.APPLICATION_JSON));
    }

    @When("the operator wants to create the voucher type without field {string}:")
    public void theOperatorWantsToCreateTheVoucherTypeWithoutFieldField(String missingField, List<Map<String, String>> datatable) throws Exception {
        VoucherTypes voucherTypes = buildVoucherTypes(datatable, missingField);

        resultActions = mockMvc.perform(post("/v1/voucher-type")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(voucherTypes))
            .accept(MediaType.APPLICATION_JSON));

    }

    @When("the operator wants to update the voucher type {string} without field {string}:")
    public void theOperatorWantsToUpdateTheVoucherTypeWithoutField(String code, String missingField, List<Map<String, String>> datatable) throws Exception {
        VoucherTypes voucherTypes = buildVoucherTypes(datatable, missingField);

        resultActions = mockMvc.perform(put("/v1/voucher-type/" + code)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(voucherTypes))
            .accept(MediaType.APPLICATION_JSON));
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

    @Then("the user retrieve the voucher type")
    public void theUserRetrieveTheVoucherType() throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.code").isNotEmpty());
    }

    @Then("the user receive the error 'No Voucher type found'")
    public void theUserReceiveTheErrorNoVoucherTypeFound() throws Exception {
        resultActions.andExpect(status().isNotFound());
    }

    @Then("the operator create the voucher type correctly")
    public void theOperatorCreateTheVoucherTypeCorrectly() throws Exception {
        resultActions.andExpect(status().isCreated());
    }

    @Then("the operator update the voucher type correctly")
    public void theOperatorUpdateTheVoucherTypeCorrectly() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Then("the operator receive the error 'Voucher Type already exist'")
    public void theOperatorReceiveTheErrorVoucherTypeAlreadyExist() throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("ALREADY_EXIST"))
            .andExpect(jsonPath("$.errorMessage").value("Voucher Type already exist"));
    }

    @Then("the operator receive the error 'Invalid Voucher Type, {string} is mandatory'")
    public void theOperatorReceiveTheErrorInvalidVoucherTypeFieldIsMandatory(String missingField) throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("MISSING_FIELD"))
            .andExpect(jsonPath("$.errorMessage").value("Invalid voucherTypes, " + missingField + " is mandatory"));
    }

    @Then("the operator receive the error 'Voucher Type exist with the same period'")
    public void theOperatorReceiveTheErrorVoucherTypeExistWithTheSamePeriod() throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("SAME_PRODUCT_AND_ORDER"))
            .andExpect(jsonPath("$.errorMessage").value("Voucher Type exist with the same period"));
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

    private VoucherTypes buildVoucherTypes(List<Map<String, String>> datatable, String missingField) {
        Map<String, String> firstRow = datatable.get(0);

        VoucherTypes voucherType = new VoucherTypes();
        if (!StringUtils.equals(missingField, "code")) {
            voucherType.setCode(firstRow.get("code"));
        }
        if (!StringUtils.equals(missingField, "promo")) {
            voucherType.setPromo(firstRow.get("promo"));
        }
        if (!StringUtils.equals(missingField, "product")) {
            voucherType.setProduct(firstRow.get("product"));
        }
        if (!StringUtils.equals(missingField, "description")) {
            voucherType.setDescription(firstRow.get("description"));
        }
        if (!StringUtils.equals(missingField, "currency")) {
            voucherType.setCurrency(firstRow.get("currency"));
        }
        if (!StringUtils.equals(missingField, "amount")) {
            voucherType.setAmount(new BigDecimal(firstRow.get("amount")));
        }
        if (!StringUtils.equals(missingField, "merchant")) {
            voucherType.setMerchant(firstRow.get("merchant"));
        }
        if (!StringUtils.equals(missingField, "paymentProvider")) {
            voucherType.setPaymentProvider(firstRow.get("paymentProvider"));
        }
        if (!StringUtils.equals(missingField, "country")) {
            voucherType.setCountry(firstRow.get("country"));
        }
        if (!StringUtils.equals(missingField, "shop")) {
            voucherType.setShop(firstRow.get("shop"));
        }
        if (!StringUtils.equals(missingField, "enabled")) {
            voucherType.setEnabled(firstRow.get("enabled").equals("true"));
        }
        if (!StringUtils.equals(missingField, "startDate")) {
            voucherType.setStartDate(LocalDate.parse(firstRow.get("startDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (!StringUtils.equals(missingField, "endDate")) {
            voucherType.setEndDate(LocalDate.parse(firstRow.get("endDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (!StringUtils.equals(missingField, "order")) {
            voucherType.setOrder(Integer.parseInt(firstRow.get("order")));
        }

        return voucherType;
    }

    private Map<VoucherType, List<Voucher>> extractDatatableData(List<Map<String, String>> datatable) {
        Map<VoucherType, List<Voucher>> returnValue = new HashMap<>();

        datatable.forEach(row -> {
            VoucherType voucherType = new VoucherType();
            voucherType.setCode(getElementOrDefault(row, "code", "DEFCODE"));
            voucherType.setProduct(getElementOrDefault(row, "product", "DEFPRODUCT"));
            voucherType.setPromo(getElementOrDefault(row, "promo", "DEFPROMO"));
            voucherType.setDescription(getElementOrDefault(row, "description", "DEFDESCRIPTION"));

            Amount amount = new Amount();
            amount.setValue(new BigDecimal(getElementOrDefault(row, "amount", "10.0")));
            amount.setCurrency(getElementOrDefault(row, "currency", "INR"));
            voucherType.setAmount(amount);
            voucherType.setMerchantId(getElementOrDefault(row, "merchant", "TINDER"));
            voucherType.setPaymentProvider(getElementOrDefault(row, "paymentProvider", "PAYTM"));
            voucherType.setCountry(getElementOrDefault(row, "country", "IN"));
            voucherType.setShopId(getElementOrDefault(row, "shop", "shop2"));
            voucherType.setEnabled(getElementOrDefault(row, "enabled", "true").equals("true"));
            voucherType.setStartDate(LocalDate.parse(getElementOrDefault(row, "startDate", "01/01/2020"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            voucherType.setEndDate(LocalDate.parse(getElementOrDefault(row, "endDate", "31/12/2020"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            voucherType.setOrder(Integer.parseInt(getElementOrDefault(row, "order", "5")));

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
