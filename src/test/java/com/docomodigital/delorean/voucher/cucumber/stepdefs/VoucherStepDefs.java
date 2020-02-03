package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @When("the operator wants to upload the voucher without field {string}")
    public void theOperatorWantsToUpdateTheVoucherWithoutField(String missingField) throws Exception {
        MockMultipartFile file = buildVoucherFile(2, null);

        if (missingField.equals("type")) {
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
                .file(file)
                .characterEncoding("UTF-8"));
        } else {
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
                .param("type", "TIN1M")
                .characterEncoding("UTF-8"));
        }
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

    @Timed
    @When("the operator wants to upload the voucher file with {int} vouchers for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForType(Integer size, String type) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, null);

        long startTime = System.currentTimeMillis();

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Tempo dell'operazione dell'api: " + elapsedTime);

    }

    @When("the operator wants to upload the voucher file with {int} vouchers for type {string} and the voucher file contain also {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForTypeTINMAndTheVoucherFileContainEXISTINGVOUCHER(int size, String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, code);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to upload the voucher file malformed for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileMalformedForType(String type) throws Exception {

        writeVoucherFile(3, "voucher_example.csv", null);
        byte[] fileContent = readVoucherFile();
        MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "fdgbfdv", fileContent);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));

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

    @Then("the operator upload the {int} vouchers correctly for type {string}")
    public void theOperatorUploadTheSizeVouchersCorrectly(int size, String type) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.total").value(size))
            .andExpect(jsonPath("$.uploaded").value(size))
            .andExpect(jsonPath("$.errors").value(0));

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

    @Then("the operator upload the {int} vouchers correctly and {int} with error 'Voucher with code {string} already exist'")
    public void theOperatorUploadTheVouchersCorrectlyAndWithErrorVoucherWithCode(int uploaded, int errors, String code) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.type").value("TIN1M"))
            .andExpect(jsonPath("$.total").value(uploaded + errors))
            .andExpect(jsonPath("$.uploaded").value(uploaded))
            .andExpect(jsonPath("$.errors").value(errors));
    }

    private MockMultipartFile buildVoucherFile(Integer size, String code) throws Exception {

        writeVoucherFile(size, "voucher_example.csv", code);

        byte[] fileContent = readVoucherFile();

        return new MockMultipartFile("file", "voucher_example.csv", "text/csv", fileContent);
    }

    private byte[] readVoucherFile() throws IOException {
        File file = new File("voucher_example.csv");
        return Files.readAllBytes(file.toPath());
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
