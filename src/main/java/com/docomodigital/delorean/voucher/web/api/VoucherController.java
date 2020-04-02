package com.docomodigital.delorean.voucher.web.api;

import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.List;

/**
 * Implementation of voucher-type endpoint to manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@Api(value = "voucher-api", tags = {"Voucher Management API"})
@RequestMapping("/v1")
public class VoucherController implements VoucherApi {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @Override
    public ResponseEntity<List<Vouchers>> getVouchers(
        @Valid String typeId,
        @Valid String status,
        @Valid String userId,
        @Valid String merchantId,
        @Valid String transactionId) {
        return ResponseEntity.ok(voucherService.getVouchers(typeId, status, userId, merchantId, transactionId));
    }

    @Override
    public ResponseEntity<VoucherUpload> uploadVoucher(@Valid MultipartFile file, String type) {
        VoucherUpload vouchersUploaded = voucherService.processVouchers(file, type, UploadOperation.UPLOAD);

        return ResponseEntity.ok(vouchersUploaded);
    }

    @Override
    public ResponseEntity<VoucherUpload> redeemVoucher(@Valid MultipartFile file, String type) {
        VoucherUpload vouchersUploaded = voucherService.processVouchers(file, type, UploadOperation.REDEEM);

        return ResponseEntity.ok(vouchersUploaded);
    }
}
