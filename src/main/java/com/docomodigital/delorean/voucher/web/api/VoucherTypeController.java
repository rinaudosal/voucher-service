package com.docomodigital.delorean.voucher.web.api;

import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Implementation of voucher-type endpoint to manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@RequestMapping("/v1")
public class VoucherTypeController implements VoucherTypeApi {

    private final VoucherService voucherService;

    public VoucherTypeController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @Override
    public ResponseEntity<List<AvailableVoucherTypes>> getAvailableVoucherTypes(String merchant, String paymentProvider, String country) {

        List<AvailableVoucherTypes> availableVoucherTypes = voucherService.getAvailableVoucherTypes(merchant, paymentProvider, country);
        if(availableVoucherTypes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableVoucherTypes);
    }
}
