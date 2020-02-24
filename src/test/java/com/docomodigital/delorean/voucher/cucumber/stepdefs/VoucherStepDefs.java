package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @When("the operator wants to {string} the voucher file with {int} vouchers for type {string} and the voucher file contain also {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForTypeAndTheVoucherFileContain(String operation, int size, String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, code);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
            .file(file)
            .param("type", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to {string} the voucher file for the type {string} with the voucher {string}")
    public void theOperatorWantsToRedeemTheVoucherFileForTheTypeTypeWithTheVoucherVoucher(String operation, String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(0, code);

        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
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

    @When("the operator requires the vouchers with type {string}, status {string}, userId {string}, merchantId {string} and transactionId {string}")
    public void theOperatorRequiresTheVouchersWithTypeStatusAndUserId(String type, String status, String userId, String merchantId, String transactionId) throws Exception {
        resultActions = mockMvc.perform(get("/v1/voucher")
            .accept(MediaType.APPLICATION_JSON)
            .param("typeId", type)
            .param("status", status)
            .param("userId", userId)
            .param("merchantId", merchantId)
            .param("transactionId", transactionId)

        );
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

    @Then("the operator receive the voucher correctly for type {string}")
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

    @Then("the operator {string} the {int} vouchers correctly and {int} with error {string} and message {string}")
    public void theOperatorUploadTheVouchersCorrectlyAndWithErrorVoucherWithCode(String operation, int uploaded, int errors, String errorCode, String errorMessage) throws Exception {
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.operation").value(operation.toUpperCase()))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.type").value("TIN1M"))
            .andExpect(jsonPath("$.total").value(uploaded + errors))
            .andExpect(jsonPath("$.uploaded").value(uploaded))
            .andExpect(jsonPath("$.errors").value(errors));

        //find the repository errors and find the single errorcode
        List<VoucherError> voucherErrors = voucherErrorRepository.findAll();
        Assertions.assertThat(voucherErrors).hasSize(1);
        VoucherError voucherError = voucherErrors.get(0);
        Assertions.assertThat(voucherError.getCode()).isNotNull();
        Assertions.assertThat(voucherError.getLineNumber()).isEqualTo(1);
        Assertions.assertThat(voucherError.getErrorCode()).isEqualTo(errorCode);
        Assertions.assertThat(voucherError.getErrorMessage()).isEqualTo(errorMessage);

    }

    @Then("the user retrieve the list with {string} vouchers")
    public void theUserRetrieveTheListWithVouchers(String vouchers) throws Exception {
        resultActions.andExpect(status().isOk());

        String[] voucherList = StringUtils.split(vouchers, ",");
        resultActions.andExpect(jsonPath("$[*]", hasSize(voucherList.length)));
        for (String code : voucherList) {
            resultActions.andExpect(jsonPath("$[?(@.code== '" + code + "')]").exists());
        }
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

            Voucher voucher = getVoucher(
                StringUtils.trimToNull(row.get("code")),
                StringUtils.trimToNull(row.get("type")),
                voucherStatus,
                StringUtils.trimToNull(row.get("userId")),
                StringUtils.trimToNull(row.get("transactionId"))

            );

            voucherRepository.save(voucher);
        });

    }

    private Voucher getVoucher(String code, String type, VoucherStatus status, String userId, String transactionId) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type).get();

        Voucher voucher = new Voucher();
        voucher.setStatus(status);
        voucher.setCode(code);
        voucher.setTypeId(voucherType.getId());
        voucher.setUserId(userId);
        voucher.setTransactionId(transactionId);
        return voucher;
    }

    @When("the operator wants to consume the voucher billed for merchant {string}, product {string}, country {string} and paymentProvider {string}")
    public void theOperatorWantsToConsumeTheVoucher(String merchant, String product, String country, String paymentProvider) throws Exception {
        String message = createJsonMessage(merchant, product, country, paymentProvider);

        voucherQueueReceiverService.handleMessage(message);
    }

    @When("the operator wants to consume the voucher billed for merchant {string}, product {string}, country {string} and paymentProvider {string} receiving the error code {string} and description {string}")
    public void theOperatorWantsToConsumeTheVoucher(String merchant, String product, String country, String paymentProvider, String errorCode, String errorDescription) throws Exception {
        String message = createJsonMessage(merchant, product, country, paymentProvider);

        Assertions.assertThatThrownBy(() -> voucherQueueReceiverService.handleMessage(message))
            .isInstanceOf(BadRequestException.class)
            .hasMessage(errorDescription)
            .hasFieldOrPropertyWithValue("errorCode", errorCode);

    }

    @Then("the operator receive the voucher {string} correctly")
    public void theOperatorReceiveTheVoucherCodeCorrectly(String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode).get();

        Assertions.assertThat(voucher.getPurchaseDate()).isNotNull();
        Assertions.assertThat(voucher.getUserId()).isNotNull();
        Assertions.assertThat(voucher.getTransactionId()).isNotNull();
        Assertions.assertThat(voucher.getTransactionDate()).isNotNull();
        Assertions.assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.PURCHASED);
    }

    @And("notification will be sent to requestor without error")
    public void notificationWillBeSentToRequestorWithoutError() {

    }

    @And("notification will be sent to requestor with errors")
    public void notificationWillBeSentToRequestorWithErrors() {

    }

    private String createJsonMessage(String merchant, String product, String country, String paymentProvider) throws Exception {
        DocumentContext jsonContext = JsonPath.parse(FileUtils.readFileToString(new File("src/test/resources/exampleWLMRequestQueue.json"), StandardCharsets.UTF_8));

        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product'].['attributes']", "merchantCode", merchant);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['telco'].['attributes']", "code", paymentProvider);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product']", "type", product);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['country'].['attributes']", "code", country);

        return jsonContext.jsonString();
    }
}
