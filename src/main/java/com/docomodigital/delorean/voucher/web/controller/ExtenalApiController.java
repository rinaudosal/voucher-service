package com.docomodigital.delorean.voucher.web.controller;

import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import com.docomodigital.delorean.voucher.web.api.ExternalApi;
import com.docomodigital.delorean.voucher.web.api.model.ReserveRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.util.Optional;

/**
 * Implementation of voucher-type endpoint to manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@RequestMapping("/v1")
public class ExtenalApiController implements ExternalApi {

    private final VoucherTypeService voucherTypeService;

    private final VoucherService voucherService;

    public ExtenalApiController(VoucherTypeService voucherTypeService, VoucherService voucherService) {
        this.voucherTypeService = voucherTypeService;
        this.voucherService = voucherService;
    }

    @Override
    public ResponseEntity<Vouchers> reserveVoucher(String typeId, @Valid ReserveRequest reserveRequest) {
        Optional<Vouchers> voucherReserved = voucherTypeService.reserveVoucher(typeId, reserveRequest);

        return ResponseEntity.of(voucherReserved);
    }

    @Override
    public ResponseEntity<Vouchers> updateVoucher(String typeId, String code, @Valid VoucherRequest voucherRequest) {
        Optional<Vouchers> voucher = voucherService.updateVoucher(code, typeId, voucherRequest);
        return ResponseEntity.of(voucher);
    }

    @Override
    public ResponseEntity<Vouchers> getVoucher(String typeId, String code) {
        Optional<Vouchers> voucher = voucherService.getVoucher(code, typeId);
        return ResponseEntity.of(voucher);
    }
}
