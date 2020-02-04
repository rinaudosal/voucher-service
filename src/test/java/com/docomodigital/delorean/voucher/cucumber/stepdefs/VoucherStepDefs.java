package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        resultActions = mockMvc.perform(post("/v1/voucher/" + code + "/upload")
            .accept(MediaType.APPLICATION_JSON)
            .param("type", type));
    }

    @When("the operator wants to {string} the voucher without field {string}")
    public void theOperatorWantsToUpdateTheVoucherWithoutField(String operation, String missingField) throws Exception {
        MockMultipartFile file = buildVoucherFile(2, null);

        if (missingField.equals("type")) {
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
                .file(file)
                .characterEncoding("UTF-8"));
        } else {
            resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
                .param("type", "TIN1M")
                .characterEncoding("UTF-8"));
        }
    }


    @When("the operator wants to create the voucher without field {string}")
    public void theOperatorWantsToCreateTheVoucherWithoutField(String missingField) throws Exception {
        if (!missingField.equals("code")) {
            resultActions = mockMvc.perform(post("/v1/voucher/VOUCHER21/upload")
                .accept(MediaType.APPLICATION_JSON));
        }

        if (!missingField.equals("type")) {
            resultActions = mockMvc.perform(post("/v1/voucher/upload")
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "TIN1M"));
        }


    }

    @When("the operator wants to upload the voucher file with {int} vouchers for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForType(Integer size, String type) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, null);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));
    }


//    @When("the operator wants to upload the voucher file with <size> vouchers for type {string}")
//    public void theOperatorWantsToUploadTheVoucherFileWithSizeVouchersForTypeType() {
//
//    }
    @When("the operator wants to upload the voucher file with {int} vouchers for type {string} and the voucher file contain also {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForTypeTINMAndTheVoucherFileContainEXISTINGVOUCHER(int size, String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, code);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to redeem the voucher file for the type {string} with the voucher {string}")
    public void theOperatorWantsToRedeemTheVoucherFileForTheTypeTypeWithTheVoucherVoucher(String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(0, code);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/redeem")
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));

    }

    @When("the operator wants to {string} the voucher file malformed for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileMalformedForType(String operation, String type) throws Exception {

        writeVoucherFile(3, "voucher_example.csv", null);
        byte[] fileContent = readVoucherFile();
        MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "fdgbfdv", fileContent);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));

    }

    @When("the operator wants to purchase the voucher {string}")
    public void theOperatorWantsToPurchaseTheVoucherCode(String code) throws Exception {
        resultActions = mockMvc.perform(post("/v1/voucher/" + code + "/purchase")
            .accept(MediaType.APPLICATION_JSON)
            .param("userId", "user_name")
            .param("transactionId", "txt_123456")
            .param("transactionDate", "2020-12-12T17:12:14Z"));
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

    @Then("the operator purchase the voucher {string} correctly")
    public void theOperatorPurchaseTheVoucherCorrectly(String code) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(code))
            .andExpect(jsonPath("$.type").isNotEmpty())
            .andExpect(jsonPath("$.status").value("PURCHASED"))
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andExpect(jsonPath("$.transactionId").isNotEmpty())
            .andExpect(jsonPath("$.transactionDate").isNotEmpty())
            .andExpect(jsonPath("$.purchaseDate").value("2020-02-01"))
            .andExpect(jsonPath("$.redeemDate").isEmpty())
            .andExpect(jsonPath("$.activationUrl").value("https://www.tinder.com/redeem/" + code));

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

    @Then("the operator redeem the voucher correctly for type {string}")
    public void theOperatorRedeemTheVoucherCorrectlyForType(String type) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.uploaded").value(1))
            .andExpect(jsonPath("$.errors").value(0));
    }

    @Then("the operator receive the error code {string} and description {string}")
    public void theOperatorReceiveTheErrorCodeAndDescription(String errorCode, String errorMessage) throws Exception {
        checkBadRequest(errorCode, errorMessage);
    }

    @Then("the operator receive the error 'Invalid request, parameter {string} is mandatory'")
    public void theOperatorReceiveTheErrorInvalidVoucherFieldIsMandatory(String missingField) throws Exception {
        checkBadRequest("MISSING_REQUEST_PARAM", "Invalid request, parameter " + missingField + " is mandatory");
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
            VoucherStatus voucherStatus = row.get("status") != null ? VoucherStatus.valueOf(row.get("status")) : VoucherStatus.ACTIVE;
            Voucher voucher = getVoucher(row.get("code"), row.get("type"), voucherStatus);

            voucherRepository.save(voucher);
        });

    }

    private Voucher getVoucher(String code, String type, VoucherStatus status) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type).get();

        Voucher voucher = new Voucher();
        voucher.setStatus(status);
        voucher.setCode(code);
        voucher.setTypeId(voucherType.getId());
        return voucher;
    }

}
