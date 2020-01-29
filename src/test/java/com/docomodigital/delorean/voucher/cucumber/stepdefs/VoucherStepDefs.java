package com.docomodigital.delorean.voucher.cucumber.stepdefs;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        extractDatatableData(datatable);

    }

    @When("the operator wants to upload the voucher {string} with type {string}:")
    public void theOperatorWantsToUploadTheVoucherCodeWithTypeType(String voucherCode, String voucherType) throws Exception {
//        Voucher vouchers = getVoucher(datatable, null);

        resultActions = mockMvc.perform(post("/v1/voucher")
            .accept(MediaType.APPLICATION_JSON)
            .param("type", voucherType)
            .param("code", voucherCode));

    }

    private void extractDatatableData(List<Map<String, String>> datatable) {
        List<Voucher> vouchers = new ArrayList<>();

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
