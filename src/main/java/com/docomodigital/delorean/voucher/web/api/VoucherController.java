package com.docomodigital.delorean.voucher.web.api;

import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of voucher-type endpoint to manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@RequestMapping("/v1")
public class VoucherController implements VoucherApi {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @Override
    public ResponseEntity<Vouchers> createVoucher(@NotNull @Valid String code, @NotNull @Valid String type) {
        Vouchers voucher = voucherService.createVoucher(code, type);

        try {
            return ResponseEntity.created(new URI("/v1/voucher/" + voucher.getCode()))
                .body(voucher);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(voucher);
        }
    }

    @Override
    public ResponseEntity<VoucherUpload> uploadVoucher(@Valid MultipartFile file, String type) {
        VoucherUpload vouchersUploaded = voucherService.uploadVouchers(file, type);

        return ResponseEntity.ok(vouchersUploaded);
    }
}
