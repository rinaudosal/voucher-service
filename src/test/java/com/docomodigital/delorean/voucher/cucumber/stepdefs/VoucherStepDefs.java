package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
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

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @When("the operator want to gets the voucher {string} with type {string}")
    public void theOperatorWantToGetsTheVoucherCodeWithTypeTypeId(String code, String typeId) throws Exception {
        resultComponent.resultActions = mockMvc.perform(get("/v1/external/voucher-type/" + typeId + "/voucher/" + code)
            .header(Constants.API_KEY_HEADER, "TEST_API_KEY")
            .header(Constants.SIGNATURE_HEADER_NAME, "TEST_SIGNATURE_KEY")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON));
    }

    @When("the operator want to gets the voucher without field {string}")
    public void theOperatorWantToGetsTheVoucherWithoutFieldField(String field) throws Exception {
        boolean typeId = field.equals("typeId");
        String path = typeId ?
            "/v1/external/voucher-type//voucher/VOUCHERPUR" :
            "/v1/external/voucher-type/TIN1M/voucher/";

        resultComponent.resultActions = mockMvc.perform(get(path)
            .header(Constants.API_KEY_HEADER, "TEST_API_KEY")
            .header(Constants.SIGNATURE_HEADER_NAME, "TEST_SIGNATURE_KEY")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON));
    }

    @When("the operator wants to {string} the voucher without field {string}")
    public void theOperatorWantsToUpdateTheVoucherWithoutField(String operation, String missingField) throws Exception {
        if (missingField.equals("typeId")) {
            Writer fstream = new OutputStreamWriter(new FileOutputStream("voucher_example.csv"), StandardCharsets.UTF_8);

            fstream
                .append("promo_campaign_name,promo_code,date_redeemed\n")
                .append("PartnerA_TesterBatch_Set1_1MonthPlus,")
                .append("fdgfdgdfgfdgfd")
                .append(",2/5/20 21:41\n");

            fstream.flush();
            fstream.close();

            byte[] fileContent = readVoucherFile();

            MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "text/csv", fileContent);

            resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
                .file(file)
                .characterEncoding("UTF-8"));
        } else {
            resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
                .param("typeId", "TIN1M")
                .characterEncoding("UTF-8"));
        }
    }

    @When("the operator wants to upload the voucher file with {int} vouchers for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForType(Integer size, String type) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, null);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to 'redeem' the voucher file with {int} vouchers for type {string} and the voucher file contain also {string}")
    public void theOperatorWantsToRedeemTheVoucherFileWithVouchersForTypeAndTheVoucherFileContain(int size, String type, String code) throws Exception {
        Writer fstream = new OutputStreamWriter(new FileOutputStream("voucher_example.csv"), StandardCharsets.UTF_8);

        fstream
            .append("promo_campaign_name,promo_code,date_redeemed\n");

        String[] codes = code.split(",");
        for (String line : codes) {
            fstream
                .append("PartnerA_TesterBatch_Set1_1MonthPlus,")
                .append(line)
                .append(",2/5/20 21:41\n");
        }

        fstream.flush();
        fstream.close();

        byte[] fileContent = readVoucherFile();

        MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "text/csv", fileContent);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/redeem")
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to 'upload' the voucher file with {int} vouchers for type {string} and the voucher file contain also {string}")
    public void theOperatorWantsToUploadTheVoucherFileWithVouchersForTypeAndTheVoucherFileContain(int size, String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(size, code);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));
    }

    @When("the operator wants to 'redeem' the voucher file for the type {string} with the voucher {string}")
    public void theOperatorWantsToTheVoucherFileForTheTypeTypeWithTheVoucherVoucher(String type, String code) throws Exception {
        Writer fstream = new OutputStreamWriter(new FileOutputStream("voucher_example.csv"), StandardCharsets.UTF_8);

        fstream.append("promo_campaign_name,promo_code,date_redeemed\n");

        if (code != null) {
            String[] codes = code.split(",");
            for (String line : codes) {
                fstream
                    .append("PartnerA_TesterBatch_Set1_1MonthPlus,")
                    .append(line)
                    .append(",2/25/20 21:41")
                    .append("\n");
            }
        }

        fstream.flush();
        fstream.close();

        byte[] fileContent = readVoucherFile();

        MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "text/csv", fileContent);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/redeem")
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));

    }

    @When("the operator wants to 'upload' the voucher file for the type {string} with the voucher {string}")
    public void theOperatorWantsToUploadTheVoucherFileForTheTypeTypeWithTheVoucherVoucher(String type, String code) throws Exception {
        MockMultipartFile file = buildVoucherFile(0, code);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/upload")
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));

    }

    @When("the operator wants to {string} the voucher file malformed for type {string}")
    public void theOperatorWantsToUploadTheVoucherFileMalformedForType(String operation, String type) throws Exception {

        writeVoucherFile(3, "voucher_example.csv", null);
        byte[] fileContent = readVoucherFile();
        MockMultipartFile file = new MockMultipartFile("file", "voucher_example.csv", "fdgbfdv", fileContent);

        resultComponent.resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/voucher/" + operation)
            .file(file)
            .param("typeId", type)
            .characterEncoding("UTF-8"));

    }

    @When("the operator requires the vouchers with type {string}, status {string}, userId {string}, merchantId {string} and transactionId {string}")
    public void theOperatorRequiresTheVouchersWithTypeStatusAndUserId(String type, String status, String userId, String merchantId, String transactionId) throws Exception {
        resultComponent.resultActions = mockMvc.perform(get("/v1/voucher")
            .accept(MediaType.APPLICATION_JSON)
            .param("typeId", type)
            .param("status", status)
            .param("userId", userId)
            .param("merchantId", merchantId)
            .param("transactionId", transactionId)

        );
    }

    @When("the operator wants to {string} the voucher {string} reserved for typeId {string} and transactionId {string}")
    public void theOperatorWantsToOperationTheVoucherCodeReservedForTypeIdTypeId(String operation, String voucherCode, String typeId, String transactionId) throws Exception {
        VoucherRequest voucherRequest = new VoucherRequest();
        voucherRequest.setTransactionStatus(VoucherRequest.TransactionStatusEnum.valueOf(operation));
        voucherRequest.setTransactionId(transactionId);
        voucherRequest.setTransactionDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 6, 6, 6), ZoneOffset.UTC));
        voucherRequest.setUserId("usr_123");
        voucherRequest.setAmount(BigDecimal.ONE);
        voucherRequest.setCurrency("INR");

        resultComponent.resultActions = mockMvc.perform(patch("/v1/external/voucher-type/" + typeId + "/voucher/" + voucherCode)
            .header(Constants.API_KEY_HEADER, "TEST_API_KEY")
            .header(Constants.SIGNATURE_HEADER_NAME, "TEST_SIGNATURE_KEY")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(voucherRequest)));
    }

    @Then("the operator {string} the voucher {string} correctly for typeId {string}")
    public void theOperatorOperationTheVoucherCorrectlyForTypeIdTypeId(String operation, String voucherCode, String typeId) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(voucherCode))
            .andExpect(jsonPath("$.typeId").value(typeId));

        if ("SUCCESS".equalsIgnoreCase(operation)) {
            resultComponent.resultActions.andExpect(jsonPath("$.status").value("PURCHASED"))
                .andExpect(jsonPath("$.transactionId").value("txt1"))
                .andExpect(jsonPath("$.transactionDate").value("2020-01-01T06:06:06Z"))
                .andExpect(jsonPath("$.userId").value("usr_123"))
                .andExpect(jsonPath("$.purchaseDate").isNotEmpty())
                .andExpect(jsonPath("$.amount").value("1"))
                .andExpect(jsonPath("$.currency").value("INR"))
                .andExpect(jsonPath("$.reserveDate").isNotEmpty())
                .andExpect(jsonPath("$.activationUrl").isNotEmpty());
        } else {
            resultComponent.resultActions.andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.transactionId").isEmpty())
                .andExpect(jsonPath("$.transactionDate").isEmpty())
                .andExpect(jsonPath("$.userId").isEmpty())
                .andExpect(jsonPath("$.purchaseDate").isEmpty())
                .andExpect(jsonPath("$.reserveDate").isEmpty())
                .andExpect(jsonPath("$.amount").isEmpty())
                .andExpect(jsonPath("$.currency").isEmpty())
                .andExpect(jsonPath("$.activationUrl").isEmpty());
        }
    }


    @Then("the operator gets the voucher correctly with {string} and type {string}")
    public void theOperatorGetsTheVoucherCorrectlyWithCodeAndTypeTypeId(String code, String typeId) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(code))
            .andExpect(jsonPath("$.typeId").value(typeId));

    }

    @Then("the operator upload the {int} vouchers correctly for type {string}")
    public void theOperatorUploadTheSizeVouchersCorrectly(int size, String type) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.typeId").value(type))
            .andExpect(jsonPath("$.total").value(size))
            .andExpect(jsonPath("$.uploaded").value(size))
            .andExpect(jsonPath("$.errors").value(0));

    }

    @Then("the operator receive the voucher correctly for type {string}")
    public void theOperatorRedeemTheVoucherCorrectlyForType(String type) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.typeId").value(type))
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.uploaded").value(1))
            .andExpect(jsonPath("$.errors").value(0));
    }

    @Then("the operator receive the error 'Invalid request, parameter {string} is mandatory'")
    public void theOperatorReceiveTheErrorInvalidVoucherFieldIsMandatory(String missingField) throws Exception {
        checkBadRequest("MISSING_REQUEST_PARAM", "Invalid request, parameter " + missingField + " is mandatory");
    }

    @Then("the operator {string} the {int} vouchers correctly and {int} with error {string} and message {string}")
    public void theOperatorUploadTheVouchersCorrectlyAndWithErrorVoucherWithCode(String operation, int uploaded, int errors, String errorCode, String errorMessage) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UPLOADED"))
            .andExpect(jsonPath("$.operation").value(operation.toUpperCase()))
            .andExpect(jsonPath("$.filename").value("voucher_example.csv"))
            .andExpect(jsonPath("$.typeId").value("TIN1M"))
            .andExpect(jsonPath("$.total").value(uploaded + errors))
            .andExpect(jsonPath("$.uploaded").value(uploaded))
            .andExpect(jsonPath("$.errors").value(errors));

        //find the repository errors and find the single errorcode
        List<VoucherError> voucherErrors = voucherErrorRepository.findAll();
        Assertions.assertThat(voucherErrors).hasSize(1);
        VoucherError voucherError = voucherErrors.get(0);
        Assertions.assertThat(voucherError.getLine()).isNotNull();
        Assertions.assertThat(voucherError.getLineNumber()).isEqualTo(operation.equalsIgnoreCase("REDEEM") ? 2 : 1);
        Assertions.assertThat(voucherError.getErrorCode()).isEqualTo(errorCode);
        Assertions.assertThat(voucherError.getErrorMessage()).isEqualTo(errorMessage);

    }

    @Then("the user retrieve the list with {string} vouchers")
    public void theUserRetrieveTheListWithVouchers(String vouchers) throws Exception {
        resultComponent.resultActions.andExpect(status().isOk());

        String[] voucherList = StringUtils.split(vouchers, ",");
        resultComponent.resultActions.andExpect(jsonPath("$[*]", hasSize(voucherList.length)));
        for (String code : voucherList) {
            resultComponent.resultActions.andExpect(jsonPath("$[?(@.code== '" + code + "')]").exists());
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
            Instant reserveDate = StringUtils.trimToNull(row.get("reserveDate")) != null ?
                LocalDate.parse(row.get("reserveDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay().toInstant(ZoneOffset.UTC) : null;

            Voucher voucher = getVoucher(
                StringUtils.trimToNull(row.get("code")),
                StringUtils.trimToNull(row.get("typeId")),
                voucherStatus,
                StringUtils.trimToNull(row.get("userId")),
                StringUtils.trimToNull(row.get("transactionId")),
                StringUtils.trimToNull(row.get("activationUrl")),
                reserveDate
            );

            voucherRepository.save(voucher);
        });

    }

    private Voucher getVoucher(String code, String type, VoucherStatus status, String userId, String transactionId, String activationUrl,
                               Instant reserveDate) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type).get();
        Voucher voucher = new Voucher();
        voucher.setStatus(status);
        voucher.setCode(code);
        voucher.setTypeId(voucherType.getId());
        voucher.setUserId(userId);
        voucher.setTransactionId(transactionId);
        voucher.setActivationUrl(activationUrl);
        voucher.setReserveDate(reserveDate);
        return voucher;
    }

    @When("the operator wants to consume the voucher billed for shop {string}, product {string}, country {string} and paymentProvider {string}")
    public void theOperatorWantsToConsumeTheVoucher(String shopCode, String product, String country, String paymentProvider) throws Exception {
        String message = createJsonMessage(shopCode, product, country, paymentProvider);

        voucherQueueReceiverService.handleMessage(message.getBytes());
    }

    @When("the operator wants to consume the voucher billed for shop {string}, product {string}, country {string} and paymentProvider {string} receiving the error code {string} and description {string}")
    public void theOperatorWantsToConsumeTheVoucher(String shopCode, String product, String country, String paymentProvider, String errorCode, String errorDescription) throws Exception {
        String message = createJsonMessage(shopCode, product, country, paymentProvider);

        voucherQueueReceiverService.handleMessage(message.getBytes());

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
        Assertions.assertThat(voucherErrorRepository.findAll()).isEmpty();
    }

    @And("notification will be sent to requestor with error code {string} and description {string}")
    public void notificationWillBeSentToRequestorWithErrors(String errorCode, String errorDescription) {
        List<VoucherError> voucherErrors = voucherErrorRepository.findAll();
        Assertions.assertThat(voucherErrors).hasSize(1);
        Assertions.assertThat(voucherErrors.get(0).getErrorCode()).isEqualTo(errorCode);
        Assertions.assertThat(voucherErrors.get(0).getErrorMessage()).isEqualTo(errorDescription);
    }

    private String createJsonMessage(String shopCode, String product, String country, String paymentProvider) throws Exception {
        DocumentContext jsonContext = JsonPath.parse(FileUtils.readFileToString(new File("src/test/resources/exampleWLMRequestQueue.json"), StandardCharsets.UTF_8));

        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product'].['attributes']", "siteCode", shopCode);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['telco'].['attributes']", "code", paymentProvider);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product'].['attributes']", "code", product);
        jsonContext.put("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['country'].['attributes']", "code", country);

        return jsonContext.jsonString();
    }

}
