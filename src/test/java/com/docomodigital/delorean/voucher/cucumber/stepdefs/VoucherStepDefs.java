package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Acceptance action managed for the voucher types
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherStepDefs extends StepDefs {

    @Given("exist the voucher:")
    public void existTheVoucher(List<Map<String, String>> datatable) {
        saveVouchersFromDatatableData(datatable);

    }


    @When("the operator wants to create the voucher {string} with type {string}")
    public void theOperatorWantsToCreateTheVoucherCodeWithTypeType(String code, String type) throws Exception {
        resultActions = mockMvc.perform(post("/v1/voucher")
            .accept(MediaType.APPLICATION_JSON)
            .param("type", type)
            .param("code", code));
    }

    @When("the operator wants to create the voucher without field {string}")
    public void theOperatorWantsToCreateTheVoucherWithoutField(String missingField) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (!missingField.equals("code")) {
            params.put("code", Collections.singletonList("VHUSHUSH"));
        }
        if (!missingField.equals("type")) {
            params.put("type", Collections.singletonList("TIN1M"));
        }

        resultActions = mockMvc.perform(post("/v1/voucher")
            .accept(MediaType.APPLICATION_JSON)
            .params(params));
    }

    @Then("the operator create the voucher correctly with {string} and type {string}")
    public void theOperatorCreateTheVoucherCorrectlyWithCodeAndType(String code, String type) throws Exception {
        resultActions.andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value(code))
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.userId").isEmpty())
            .andExpect(jsonPath("$.transactionId").isEmpty())
            .andExpect(jsonPath("$.transactionDate").isEmpty())
            .andExpect(jsonPath("$.purchaseDate").isEmpty())
            .andExpect(jsonPath("$.redeemDate").isEmpty())
            .andExpect(jsonPath("$.activationUrl").isEmpty());
    }

    @Then("the operator receive the error code {string} and description {string}")
    public void theOperatorReceiveTheErrorCodeAndDescription(String errorCode, String errorMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode))
            .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Then("the operator receive the error 'Invalid request, parameter {string} is mandatory'")
    public void theOperatorReceiveTheErrorInvalidVoucherFieldIsMandatory(String missingField) throws Exception {
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("MISSING_REQUEST_PARAM"))
            .andExpect(jsonPath("$.errorMessage").value("Invalid request, parameter " + missingField + " is mandatory"));

    }

    private void saveVouchersFromDatatableData(List<Map<String, String>> datatable) {
        datatable.forEach(row -> {
            Voucher voucher = getVoucher(row.get("code"), row.get("type"));

            voucherRepository.save(voucher);
        });

    }

    private Voucher getVoucher(String code, String type) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type).get();

        Voucher voucher = new Voucher();
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setCode(code);
        voucher.setType(voucherType);
        return voucher;
    }
}
